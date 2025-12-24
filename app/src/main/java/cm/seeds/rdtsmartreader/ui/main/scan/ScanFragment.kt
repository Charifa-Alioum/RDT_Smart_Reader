package cm.seeds.rdtsmartreader.ui.main.scan

import android.Manifest
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import cm.seeds.rdtsmartreader.R
import cm.seeds.rdtsmartreader.data.ViewModelFactory
import cm.seeds.rdtsmartreader.databinding.FragmentScanBinding
import cm.seeds.rdtsmartreader.helper.REQUEST_CODE_CAMERA_PERMISSION
import cm.seeds.rdtsmartreader.helper.getDialogForPermissionDetails
import cm.seeds.rdtsmartreader.helper.loadLocation
import cm.seeds.rdtsmartreader.helper.showToast
import cm.seeds.rdtsmartreader.modeles.User
import cm.seeds.rdtsmartreader.ui.main.informations.InformationsViewModel
import com.google.zxing.BarcodeFormat
import com.google.zxing.Result
import me.dm7.barcodescanner.zxing.ZXingScannerView

class ScanFragment : Fragment(),  ZXingScannerView.ResultHandler {

    private lateinit var dataBinding : FragmentScanBinding
    private lateinit var viewModel : ScanVieModel
    private lateinit var scannerView : ZXingScannerView
    private var isFlashOn = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ScanViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        dataBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_scan,container,false)

        return dataBinding.root
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        if(requestCode == REQUEST_CODE_CAMERA_PERMISSION && grantResults[0] ==PackageManager.PERMISSION_GRANTED){
            launchScan()
        }else{
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prepareAndLaunchScan()

        addActionsonViews()

    }

    private fun addActionsonViews() {
        dataBinding.viewCancelScan.setOnClickListener {
            cancelScan()
        }
    }

    private fun cancelScan() {
        dataBinding.containerScanner.visibility = View.GONE
        scannerView.stopCamera()
    }

    private fun prepareAndLaunchScan() {
        when {
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED -> {
                launchScan()
            }
            ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.CAMERA) -> {
                val buttonClick = DialogInterface.OnClickListener { dialog, which ->
                    when (which) {

                        DialogInterface.BUTTON_POSITIVE -> {
                            dialog.dismiss()
                            launchPermissionRequest()
                        }

                        DialogInterface.BUTTON_NEGATIVE ->{
                            dialog.dismiss()
                        }

                    }
                }

                getDialogForPermissionDetails(requireContext(),Manifest.permission.CAMERA,true,buttonClick)

            }
            else -> {
                launchPermissionRequest()
            }
        }
    }

    private fun launchPermissionRequest() {
        requestPermissions(arrayOf(Manifest.permission.CAMERA), REQUEST_CODE_CAMERA_PERMISSION)
    }


    override fun handleResult(result: Result?) {
        if(result!=null){
            scannerView.stopCamera()
            /*val bottomSheet = ScanResultActionsButtonSheet()
            bottomSheet.show(childFragmentManager,ScanResultActionsButtonSheet.TAG)
            bottomSheet.dialog?.setOnCancelListener {
                launchScan()
            }*/
            val model = ViewModelProvider(requireActivity(), ViewModelFactory(requireActivity().application)).get(InformationsViewModel::class.java)
            val user = model.userToSave.value?: User(coordonnee = loadLocation(requireContext()))
            user.userId = result.text

            model.userToSave.value = user

            showToast(requireContext(),result.text)

            requireActivity().onBackPressed()

        }
    }

    private fun launchScan() {
        dataBinding.containerScanner.visibility = View.VISIBLE
        //dataBinding.textviewResultatDuScan.visibility = View.VISIBLE
        scannerView = ZXingScannerView(requireContext())
        dataBinding.scannerView.addView(scannerView)
        val formatList: MutableList<BarcodeFormat> = mutableListOf(BarcodeFormat.QR_CODE)
        scannerView.setFormats(formatList)
        scannerView.setResultHandler(this)
        scannerView.setAutoFocus(true)
        scannerView.stopCamera()
        scannerView.startCamera()
        scannerView.setAutoFocus(true)
        scannerView.flash = isFlashOn
    }

}