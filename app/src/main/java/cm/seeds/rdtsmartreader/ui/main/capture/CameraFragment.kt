package cm.seeds.rdtsmartreader.ui.main.capture

import android.Manifest
import android.app.Activity.RESULT_OK
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import cm.seeds.rdtsmartreader.R
import cm.seeds.rdtsmartreader.adapters.UserAdapter
import cm.seeds.rdtsmartreader.data.ViewModelFactory
import cm.seeds.rdtsmartreader.databinding.FragmentCameraBinding
import cm.seeds.rdtsmartreader.helper.*
import cm.seeds.rdtsmartreader.modeles.Test
import cm.seeds.rdtsmartreader.modeles.User
import cm.seeds.rdtsmartreader.ui.camera.CameraActivity
import cm.seeds.rdtsmartreader.ui.main.informations.AddInformationsBottomSheetFragment
import cm.seeds.rdtsmartreader.ui.main.informations.InformationsViewModel
import cm.seeds.rdtsmartreader.ui.main.informations.SelectInformationBottomSheet
import cm.seeds.retrofitrequestandnavigation.retrofit.Status
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class CameraFragment : Fragment() {

    /**
     * QUand on prend l'image sur une surface assez claire l'analyse d'image n'est plus assez efficace
     * Offrir une possibilité dans les paramètres permettant la connection au dispositif
     */

    private lateinit var viewModel : CameraViewModel
    private lateinit var dataBinding : FragmentCameraBinding
    private lateinit var informationsViewModel: InformationsViewModel
    private lateinit var loadingDialog : Dialog
    private var currentPhotoPath : String? = null
    private lateinit var adapterUserWithImage : UserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this, ViewModelFactory(application = requireActivity().application)).get(CameraViewModel::class.java)
        informationsViewModel = ViewModelProvider(requireActivity(),ViewModelFactory(requireActivity().application)).get(InformationsViewModel::class.java)

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        when {

            requestCode == REQUEST_CODE_CAMERA_PERMISSION && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED -> captureImage()

            requestCode == REQUEST_CODE_READ_EXTERNAL_STORAGE_PERMISSION && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED -> chooseImage()

            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        when{
            requestCode == REQUEST_CODE_CAPTURE_IMAGE && resultCode == RESULT_OK ->{
                viewModel.liveDataImagePath.value = currentPhotoPath
            }

            requestCode == REQUEST_CODE_CHOOSE_IMAGE && resultCode == RESULT_OK ->{
                val imageUri = data?.data
                imageUri?.let {
                    viewModel.liveDataImagePath.value = imageUri
                }
            }

            requestCode == REQUEST_CODE_ACTIVITY_IMAGE_CAPTURE && resultCode == RESULT_OK -> {
                val imagePath = data?.getStringExtra(Intent.EXTRA_TEXT)
                imagePath?.let {
                    viewModel.liveDataImagePath.value = imagePath
                }
            }

            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun processImage() {

        val imageLink = viewModel.liveDataImagePath.value
        var imagePath = ""
        when (imageLink) {
            is String -> imagePath = imageLink
            is Uri -> {
                requireContext().contentResolver.openInputStream(imageLink)?.use {
                    val file = createImageFile()
                    it.copyTo(file.outputStream())
                    imagePath = file.absolutePath
                }
            }
        }

        if(imagePath.isNotEmpty()){
            viewModel.scanAndHandleResult(imagePath)
        }


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment

        dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_camera, container, false)

        return dataBinding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupList()

        loadingDialog = getLoadingDialog(requireContext())

        addActionsonViews()

        attachObservers()

    }

    private fun setupList() {

        adapterUserWithImage = UserAdapter(object : ToDoOnClick{
            override fun onItemClick(item: Any, position: Int) {
                item as User
                dataBinding.recyclerviewListPerson.visibility = GONE
                viewModel.liveDataImagePath.value = item.test?.imageUri
            }
        },R.layout.item_person_card)

        dataBinding.recyclerviewListPerson.apply {
            layoutManager = GridLayoutManager(requireContext(), numberOfItemInLine(requireActivity(),R.dimen.image_size_item_person_card))
            adapter = adapterUserWithImage
        }

    }

    private fun attachObservers() {

        viewModel.liveDataImagePath.observe(viewLifecycleOwner,{
            dataBinding.buttonAnalyseImage.visibility = VISIBLE
            dataBinding.recyclerviewListPerson.visibility = GONE
            when{

                it is String && it.isNotEmpty() -> dataBinding.pageLabel.text = it

                it is Uri -> dataBinding.pageLabel.text = it.toString()

                else -> {
                    dataBinding.pageLabel.text = "Images"
                    dataBinding.buttonAnalyseImage.visibility = GONE
                    dataBinding.recyclerviewListPerson.visibility = VISIBLE
                }

            }

            loadImageInView(dataBinding.imageViewPreview,it)

        })

        informationsViewModel.allUsers.observe(viewLifecycleOwner,{
            val usersToShows = it.filter { user -> user.test?.imageUri?.isNotEmpty() == true }
            adapterUserWithImage.submitList(usersToShows)
            //adapterUserWithImage.notifyDataSetChanged()
        })

        viewModel.resultOfScan.observe(viewLifecycleOwner,{

            when (it.status){

                Status.LOADING -> {
                    if(!loadingDialog.isShowing){
                        loadingDialog.show()
                    }

                    when (it.data){

                        is Bitmap ->{
                            dataBinding.imageViewPreview.setImageBitmap(it.data)
                        }

                        is String ->{
                            loadImageInView(dataBinding.imageViewPreview,it.data)
                        }

                    }
                }

                Status.ERROR -> {
                    loadingDialog.dismiss()
                    showMessage(requireContext(),"ERROR",it.message)
                }

                Status.SUCCESS -> {
                    loadingDialog.dismiss()

                    val toDoOnClick = DialogInterface.OnClickListener { dialog, which ->

                        dialog.dismiss()
                        when(which){

                            DialogInterface.BUTTON_POSITIVE -> {
                                val testToSave = Test(conclusion = it.data.toString(), imageUri = viewModel.liveDataImagePath.value.toString())
                                val bottomSheet = SelectInformationBottomSheet.getNewInstance(testToSave)
                                bottomSheet.show(childFragmentManager,SelectInformationBottomSheet.TAG)
                            }

                            DialogInterface.BUTTON_NEGATIVE ->{
                                val bottomSheet = AddInformationsBottomSheetFragment()
                                bottomSheet.show(childFragmentManager,AddInformationsBottomSheetFragment.TAG)
                            }

                            DialogInterface.BUTTON_NEUTRAL ->{

                            }
                        }

                        viewModel.liveDataImagePath.value = ""
                    }

                    MaterialAlertDialogBuilder(requireContext())
                            .setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.dialog_background))
                            .setMessage("Ce test est ${it.data.toString()}")
                            .setTitle("Resultats du Scan")
                            .setPositiveButton("Associer à un patient",toDoOnClick)
                            .setNeutralButton("Fermer",toDoOnClick)
                            .setNegativeButton("Créer un Patient",toDoOnClick)
                            .show()
                }
            }
        })
    }

    private fun addActionsonViews() {

        dataBinding.buttonAnalyseImage.setOnClickListener {
            processImage()
        }

        dataBinding.backButton.setOnClickListener {
            requireActivity().onBackPressed()
        }

        dataBinding.fabCaptureChooseImage.setOnClickListener {
            chooseImage()
        }

        dataBinding.fabCaptureImage.setOnClickListener {
            captureImage()
        }

        dataBinding.fabEditCurrentImage.setOnClickListener {
            if(dataBinding.fabCaptureChooseImage.visibility == VISIBLE){
                dataBinding.fabCaptureImage.hide()
                dataBinding.fabCaptureChooseImage.hide()
            }else{
                dataBinding.fabCaptureImage.show()
                dataBinding.fabCaptureChooseImage.show()
            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File = requireContext().filesDir
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    private fun captureImage() {

        when {

            ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED ->{

                /*Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                    // Ensure that there's a camera activity to handle the intent
                    takePictureIntent.resolveActivity(requireContext().packageManager)?.also {
                        // Create the File where the photo should go
                        val photoFile: File? = try {
                            createImageFile()
                        } catch (ex: IOException) {
                            null
                        }
                        // Continue only if the File was successfully created
                        photoFile?.also {
                            val photoURI: Uri = FileProvider.getUriForFile(
                                requireContext(),
                                "com.example.android.fileprovider",
                                it
                            )
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                            startActivityForResult(takePictureIntent, REQUEST_CODE_CAPTURE_IMAGE)
                        }
                    }
                }*/

                startActivityForResult(Intent(requireContext(),CameraActivity::class.java), REQUEST_CODE_ACTIVITY_IMAGE_CAPTURE)

            }

            shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {

                val toDoOnButtonCLick = DialogInterface.OnClickListener { dialog, which ->

                    when(which){

                        DialogInterface.BUTTON_POSITIVE -> {
                            dialog.dismiss()
                            requestPermissions(arrayOf(Manifest.permission.CAMERA), REQUEST_CODE_CAMERA_PERMISSION)
                        }

                        else -> dialog.dismiss()
                    }

                }

                MaterialAlertDialogBuilder(requireContext())
                    .setBackground(ContextCompat.getDrawable(requireContext(),R.drawable.dialog_background))
                    .setPositiveButton(R.string.attribuer, toDoOnButtonCLick)
                    .setNegativeButton(R.string.refuser, toDoOnButtonCLick)
                    .setTitle("Permission manquante")
                    .setMessage("Nous avons besoin de votre autorisation pour capturer une image")
                    .show()
            }

            else -> {
                requestPermissions(arrayOf(Manifest.permission.CAMERA), REQUEST_CODE_CAMERA_PERMISSION)
            }

        }
    }

    private fun chooseImage() {

        when {

            ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED ->{
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).also { pickImageIntent ->
                    // Ensure that there's a camera activity to handle the intent
                    pickImageIntent.resolveActivity(requireContext().packageManager)?.also {
                        startActivityForResult(pickImageIntent, REQUEST_CODE_CHOOSE_IMAGE)
                    }
                }

            }

            ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),Manifest.permission.READ_EXTERNAL_STORAGE) -> {

                val toDoOnButtonCLick = DialogInterface.OnClickListener { dialog, which ->

                    when(which){

                        DialogInterface.BUTTON_POSITIVE -> {
                            dialog.dismiss()
                            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_CODE_READ_EXTERNAL_STORAGE_PERMISSION)
                        }

                        else -> dialog.dismiss()
                    }

                }

                MaterialAlertDialogBuilder(requireContext())
                    .setBackground(ContextCompat.getDrawable(requireContext(),R.drawable.dialog_background))
                    .setPositiveButton(R.string.attribuer, toDoOnButtonCLick)
                    .setNegativeButton(R.string.refuser, toDoOnButtonCLick)
                    .setTitle("Permission manquante")
                    .setMessage("Nous avons besoin de votre autorisation pour pouvoir accéder à vos images")
                    .show()
            }

            else -> {
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_CODE_READ_EXTERNAL_STORAGE_PERMISSION)
            }

        }
    }
}