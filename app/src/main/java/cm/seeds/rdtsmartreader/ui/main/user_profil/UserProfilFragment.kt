package cm.seeds.rdtsmartreader.ui.main.user_profil

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import cm.seeds.rdtsmartreader.R
import cm.seeds.rdtsmartreader.adapters.FormAdapter
import cm.seeds.rdtsmartreader.data.ViewModelFactory
import cm.seeds.rdtsmartreader.databinding.FragmentUserProfilBinding
import cm.seeds.rdtsmartreader.helper.ToDoOnClick
import cm.seeds.rdtsmartreader.helper.gone
import cm.seeds.rdtsmartreader.helper.loadImageInView
import cm.seeds.rdtsmartreader.helper.show
import cm.seeds.rdtsmartreader.modeles.Form
import cm.seeds.rdtsmartreader.ui.form.form_edition.FormCreationActivity

class UserProfilFragment : Fragment(){

    private lateinit var databinding : FragmentUserProfilBinding
    private lateinit var userProfilViewModel: UserProfilViewModel
    private lateinit var formAdapter : FormAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userProfilViewModel = ViewModelProvider(this, ViewModelFactory(requireActivity().application)).get(UserProfilViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        databinding = FragmentUserProfilBinding.inflate(inflater,container,false)
        return databinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupList()

        attachObservers()

        addDataToViews()

        addActionsOnViews()
    }

    private fun addActionsOnViews() {

        databinding.buttonCreateForm.setOnClickListener {
            startActivity(Intent(requireContext(), FormCreationActivity::class.java).apply {
                putExtra(FormCreationActivity.WHAT_TO_DO, FormCreationActivity.CREATE_FORM)
            })
        }
    }

    private fun addDataToViews() {
        loadImageInView(imageView = databinding.imageviewUserImage, imageReference = R.drawable.profil,isCircle = true)
    }

    private fun setupList() {
        formAdapter = FormAdapter { item, position, view ->
            when(view.id){

                else -> startActivity(Intent(requireContext(), FormCreationActivity::class.java).apply {
                    putExtra(FormCreationActivity.WHAT_TO_DO, FormCreationActivity.UPDATE_FORM)
                    putExtra(FormCreationActivity.FORM, item as Form)
                })

            }
        }

        databinding.recyclerviewForms.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = formAdapter
            addItemDecoration(DividerItemDecoration(requireContext(),LinearLayoutManager.VERTICAL))
        }
    }

    private fun attachObservers() {
        userProfilViewModel.forms.observe(viewLifecycleOwner,{
            if(!it.isNullOrEmpty()){
                formAdapter.submitList(it)
                databinding.layoutForms.show()
            }else{
                databinding.layoutForms.gone()
            }
        })
    }

}