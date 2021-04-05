package cm.seeds.rdtsmartreader.ui.main.informations

import android.app.Dialog
import android.os.Bundle
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
import cm.seeds.rdtsmartreader.helper.ToDoOnClick
import cm.seeds.rdtsmartreader.helper.showMessage
import cm.seeds.rdtsmartreader.helper.showToast
import cm.seeds.rdtsmartreader.modeles.User

class InformationsFragment : Fragment() {

    private lateinit var informationsViewModel: InformationsViewModel
    private lateinit var dataBinding : FragmentInformationsBinding
    private lateinit var userAdapter: UserAdapter
    private var typeOfData = TYPE_OF_DATA_PERSON

    private lateinit var synchronisationDialog : Dialog
    private lateinit var synchronisationDataBinding : LayoutSynchronisationBinding

    companion object{

        const val TYPE_DATA_TO_SHOW = "type_of_data"

        const val TYPE_OF_DATA_PERSON = 1
        const val TYPE_OF_DATA_TEST = 2

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        informationsViewModel = ViewModelProvider(requireActivity(), ViewModelFactory(requireActivity().application)).get(InformationsViewModel::class.java)

        arguments?.let {
            typeOfData = it.getInt(TYPE_DATA_TO_SHOW, TYPE_OF_DATA_PERSON)
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
        })
        dataBinding.recyclerviewListPerson.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = userAdapter
        }
    }

    private fun addObservers() {

        informationsViewModel.allUsers.observe(viewLifecycleOwner, {

            userAdapter.submitList(it)
            userAdapter.notifyDataSetChanged()

            val numbersPersonnesSync = it.count { item -> item.synchronised }
            val numbersPersonnesNotSync = it.count { item -> !item.synchronised }

            if(numbersPersonnesNotSync<=0){
                dataBinding.buttonSynchronise.visibility = GONE
            }else{
                dataBinding.buttonSynchronise.visibility = VISIBLE
            }

            dataBinding.textviewNombrePersonnes.text = "${it.size} ${getString(R.string.cas)}"
            dataBinding.textviewNombrePersonnesNotSynched.text = "$numbersPersonnesNotSync ${getString(R.string.non_synchronisees)}"
            dataBinding.textviewNombrePersonnesSynchronisees.text = "$numbersPersonnesSync ${getString(R.string.synchronisees)}"

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

        dataBinding.layoutAllUsers.setOnClickListener {
            userAdapter.submitList(informationsViewModel.allUsers.value)
        }

    }

    private fun openBottomSheet() {
        val bottomSheet = AddInformationsBottomSheetFragment()
        bottomSheet.show(childFragmentManager, AddInformationsBottomSheetFragment.TAG)
    }
}