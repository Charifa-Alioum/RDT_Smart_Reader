package cm.seeds.rdtsmartreader.ui.main.informations

import android.app.Dialog
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import cm.seeds.rdtsmartreader.R
import cm.seeds.rdtsmartreader.adapters.UserAdapter
import cm.seeds.rdtsmartreader.data.Status
import cm.seeds.rdtsmartreader.data.ViewModelFactory
import cm.seeds.rdtsmartreader.databinding.FragmentInformationsBinding
import cm.seeds.rdtsmartreader.databinding.LayoutSynchronisationBinding
import cm.seeds.rdtsmartreader.helper.*
import cm.seeds.rdtsmartreader.modeles.User

class InformationsFragment : Fragment() {

    private lateinit var informationsViewModel: InformationsViewModel
    private lateinit var dataBinding : FragmentInformationsBinding
    private lateinit var userAdapter: UserAdapter
    private var typeOfData = TYPE_OF_DATA_ALL_TEST

    private lateinit var synchronisationDialog : Dialog
    private lateinit var synchronisationDataBinding : LayoutSynchronisationBinding

    private var paramAlreadyUsed = false

    companion object{

        const val TYPE_DATA_TO_SHOW = "type_of_data"

        const val TYPE_OF_DATA_ALL_TEST = 1
        const val TYPE_OF_DATA_POSITIVE_TEST = 2
        const val TYPE_OF_DATA_NEGATIVE_TEST = 3
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedElementEnterTransition = TransitionInflater.from(requireContext()).inflateTransition(R.transition.explode)
        sharedElementReturnTransition = TransitionInflater.from(requireContext()).inflateTransition(R.transition.explode)

        informationsViewModel = ViewModelProvider(requireActivity(), ViewModelFactory(requireActivity().application)).get(InformationsViewModel::class.java)

        arguments?.let {
            typeOfData = it.getInt(TYPE_DATA_TO_SHOW, TYPE_OF_DATA_ALL_TEST)
        }

        setupSynchronisationDialog()

    }

    private fun setupSynchronisationDialog() {
        synchronisationDialog = Dialog(requireContext())
        synchronisationDataBinding = LayoutSynchronisationBinding.inflate(LayoutInflater.from(synchronisationDialog.context))
        synchronisationDialog.setContentView(synchronisationDataBinding.root)
        synchronisationDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        synchronisationDialog.setCancelable(false)

    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {

        dataBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_informations,container,false)

        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addActionsonViews()

        setupRecyclerViews()

        addObservers()
    }



    private fun setupRecyclerViews() {
        userAdapter = UserAdapter(object : ToDoOnClick{
            override fun onItemClick(item: Any, position: Int) {
                informationsViewModel.userToSave.value = item as User
                openBottomSheet()
            }
        },R.layout.item_person)
        dataBinding.recyclerviewListPerson.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = userAdapter
        }
    }

    private fun addObservers() {

        informationsViewModel.allUsers.observe(viewLifecycleOwner, {

            val numbersPersonnesSync = it.count { item -> item.synchronised }
            val numbersPersonnesNotSync = it.count { item -> !item.synchronised }
            val numbersPersonnesPositifs = it.count { item -> item.test?.conclusion == CONCLUSION_POSITIF }
            val numbersPersonnesNegatifs = it.count { item -> item.test?.conclusion == CONCLUSION_NEGATIF }

            dataBinding.textviewNombrePersonnes.text = "${it.size} ${getString(R.string.cas)}"
            dataBinding.textNumberNotSynchedUsers.text = "$numbersPersonnesNotSync ${getString(R.string.non_synchronisees)}"
            dataBinding.textNumberSynchedUsers.text = "$numbersPersonnesSync ${getString(R.string.synchronisees)}"
            dataBinding.textNumberPositifsUsers.text = "$numbersPersonnesPositifs ${getString(R.string.positifs)}"
            dataBinding.textNumberNegatifsUsers.text = "$numbersPersonnesNegatifs ${getString(R.string.negatifs)}"

            if(paramAlreadyUsed){
                userAdapter.submitList(it)
            }else{
                var list = listOf<User>()
                when(typeOfData){

                    TYPE_OF_DATA_ALL_TEST -> {
                        list = it
                    }

                    TYPE_OF_DATA_NEGATIVE_TEST -> {
                        list = it.filter { item -> item.test?.conclusion == CONCLUSION_NEGATIF }
                    }

                    TYPE_OF_DATA_POSITIVE_TEST -> {
                        list = it.filter { item -> item.test?.conclusion == CONCLUSION_POSITIF }
                    }

                }

                userAdapter.submitList(list)
            }

            userAdapter.notifyDataSetChanged()

            if(numbersPersonnesNotSync<=0){
                dataBinding.buttonSynchronise.visibility = GONE
            }else{
                dataBinding.buttonSynchronise.visibility = VISIBLE
            }

        })


        informationsViewModel.synchronisationStatusLiveData.observe(viewLifecycleOwner,{

            when(it.status){

                Status.LOADING ->{
                    if(!synchronisationDialog.isShowing){
                        synchronisationDialog.show()
                    }

                    if(it.data!=null){
                        val progress = it.data.userSynched.toFloat() / it.data.userToSynch.toFloat()
                        synchronisationDataBinding.progressHorizontal.progress = (progress * 100).toInt()
                        synchronisationDataBinding.texviewNumberSynched.text = it.data.userSynched.toString()
                        synchronisationDataBinding.texviewNumberToSynch.text = it.data.userToSynch.toString()
                    }
                }

                Status.ERROR ->{

                    synchronisationDialog.dismiss()
                    showMessage(requireContext(),"ERROR",it.message)
                }

                Status.SUCCESS -> {

                    synchronisationDialog.dismiss()
                    showToast(requireContext(), "Synchronisation Terminée")

                }

            }

        })

    }

    private fun addActionsonViews() {
        dataBinding.fabAddUser.setOnClickListener {
            openBottomSheet()
        }

        dataBinding.buttonSynchronise.setOnClickListener {
            informationsViewModel.synchronise()
        }

        dataBinding.backButton.setOnClickListener {
            requireActivity().onBackPressed()
        }

        dataBinding.layoutAllNotSyncUsers.setOnClickListener {
            userAdapter.submitList(informationsViewModel.allUsers.value?.filter { item -> !item.synchronised })
        }

        dataBinding.layoutAllSyncUsers.setOnClickListener {
            userAdapter.submitList(informationsViewModel.allUsers.value?.filter { item -> item.synchronised })
        }

        dataBinding.layoutAllNegatifsUsers.setOnClickListener {
            userAdapter.submitList(informationsViewModel.allUsers.value?.filter { item -> item.test?.conclusion == CONCLUSION_NEGATIF })
        }

        dataBinding.layoutAllPositifsUsers.setOnClickListener {
            userAdapter.submitList(informationsViewModel.allUsers.value?.filter { item -> item.test?.conclusion == CONCLUSION_POSITIF })
        }

        dataBinding.layoutAllUsers.setOnClickListener {
            userAdapter.submitList(informationsViewModel.allUsers.value)
        }

    }

    private fun openBottomSheet() {
        val bottomSheet = AddInformationsBottomSheetFragment()
        bottomSheet.show(childFragmentManager, AddInformationsBottomSheetFragment.TAG)
    }
}