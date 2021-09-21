package cm.seeds.rdtsmartreader.ui.main.informations

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import cm.seeds.rdtsmartreader.R
import cm.seeds.rdtsmartreader.adapters.UserAdapter
import cm.seeds.rdtsmartreader.data.ViewModelFactory
import cm.seeds.rdtsmartreader.databinding.FragmentSelectInformationsBinding
import cm.seeds.rdtsmartreader.helper.CONCLUSION_NEGATIF
import cm.seeds.rdtsmartreader.helper.CONCLUSION_POSITIF
import cm.seeds.rdtsmartreader.helper.ToDoOnClick
import cm.seeds.rdtsmartreader.helper.showToast
import cm.seeds.rdtsmartreader.modeles.Test
import cm.seeds.rdtsmartreader.modeles.User
import cm.seeds.rdtsmartreader.ui.MainViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class SelectInformationBottomSheet : BottomSheetDialogFragment() {

    private lateinit var dataBinding : FragmentSelectInformationsBinding
    private lateinit var mainViewModel: MainViewModel
    private lateinit var informationsViewModel: InformationsViewModel
    private lateinit var test: Test
    private lateinit var allUsers : List<User>
    private lateinit var adapterPerson : UserAdapter

/*    //Filter Params
    var consideredSynched = false
    var consideredNotSynched = false
    var consideredPositifs = false
    var consideredNegatifs = false*/

    companion object{

        const val TAG = "SelectInformationBottomSheet"
        const val TEST_TO_MATCH_WITH = "test_to_match_with"

        fun getNewInstance(test: Test) : SelectInformationBottomSheet{
            return SelectInformationBottomSheet().apply {
                arguments = Bundle().apply {
                    putSerializable(TEST_TO_MATCH_WITH,test)
                }
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainViewModel = ViewModelProvider(requireActivity(), ViewModelFactory(requireActivity().application)).get(MainViewModel::class.java)
        informationsViewModel = ViewModelProvider(requireActivity(), ViewModelFactory(requireActivity().application)).get(InformationsViewModel::class.java)
        val receiveTest = arguments?.getSerializable(TEST_TO_MATCH_WITH)
        if(receiveTest!=null){
            test = receiveTest as Test
        }else{
            dismiss()
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        dataBinding = FragmentSelectInformationsBinding.inflate(inflater,container,false)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupList()

        attachObservers()

        addACtionsOnViews()

    }

    private fun addACtionsOnViews() {

        dataBinding.backButton.setOnClickListener {
            dismiss()
        }

        val onCheckListener = CompoundButton.OnCheckedChangeListener { _, _ ->
            applyFilter()
        }

        dataBinding.checkboxNotSynched.setOnCheckedChangeListener(onCheckListener)

        dataBinding.checkboxSynched.setOnCheckedChangeListener(onCheckListener)

        dataBinding.checkboxTestNegatifs.setOnCheckedChangeListener(onCheckListener)

        dataBinding.checkboxTestPositifs.setOnCheckedChangeListener(onCheckListener)

        dataBinding.searchField.doOnTextChanged { _, _, _, _ ->
            applyFilter()
        }

        dataBinding.searchButton.setOnClickListener {
            if(dataBinding.editlayoutSearchField.visibility == VISIBLE){
                dataBinding.editlayoutSearchField.visibility = GONE
                dataBinding.searchField.setText("")
                dataBinding.searchButton.setImageResource(R.drawable.ic_round_search_24)
            }else{
                dataBinding.editlayoutSearchField.visibility = VISIBLE
                dataBinding.searchButton.setImageResource(R.drawable.ic_round_search_off_24)
            }
        }
    }

    private fun attachObservers() {
        mainViewModel.allUsers.observe(viewLifecycleOwner,{
            allUsers = it
            applyFilter()
        })
    }

    private fun applyFilter() {

        var results = ArrayList(allUsers) as List<User>
        if(dataBinding.checkboxTestNegatifs.isChecked){
            results = results.filter { it.test?.conclusion == CONCLUSION_NEGATIF }
        }

        if(dataBinding.checkboxTestPositifs.isChecked){
            results = results.filter { it.test?.conclusion == CONCLUSION_POSITIF }
        }

        if(dataBinding.checkboxSynched.isChecked){
            results = results.filter { !it.synchronised }
        }

        if(dataBinding.checkboxNotSynched.isChecked){
            results = results.filter { it.synchronised }
        }

        if(dataBinding.searchField.text.toString().isNotEmpty()){
            results = results.filter { it.userName.contains(dataBinding.searchField.text.toString(), true) }
        }
        adapterPerson.submitList(results)
    }

    private fun setupList() {

        adapterPerson = UserAdapter(object : ToDoOnClick{
            override fun onItemClick(item: Any, position: Int, view: View) {
                item as User
                item.test?.apply {
                    conclusion = test.conclusion
                    imageUri = test.imageUri
                }
                dismiss()
                informationsViewModel.userToSave.value = item
                informationsViewModel.saveUser(item)
                showToast(requireContext(),"Le résultat a été assigné au patient ${item.userName}")
            }
        }, R.layout.item_person)

        dataBinding.recyclerviewListPerson.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = adapterPerson
        }
    }

}