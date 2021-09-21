package cm.seeds.rdtsmartreader.ui.form.form_edition.page

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import cm.seeds.rdtsmartreader.R
import cm.seeds.rdtsmartreader.adapters.QuestionEditionAdapter
import cm.seeds.rdtsmartreader.databinding.FragmentPageEditionBinding
import cm.seeds.rdtsmartreader.helper.gone
import cm.seeds.rdtsmartreader.helper.show
import cm.seeds.rdtsmartreader.modeles.Page
import cm.seeds.rdtsmartreader.modeles.Question
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class FormPageEditionFragment : Fragment() {

    companion object{
        const val PAGE_TO_SHOW = "PAGE_TO_SHOW"

        /**
         * Retourne une instance de ce frgament en précisant [page] comme la page à afficher
         */
        fun getInstance(page: Page) : FormPageEditionFragment {
            return FormPageEditionFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(PAGE_TO_SHOW,page)
                }
            }
        }
    }

    private lateinit var adapterQuestionEdition: QuestionEditionAdapter
    private lateinit var formPageEditionViewModel: FormPageEditionViewModel
    private lateinit var databinding : FragmentPageEditionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        formPageEditionViewModel = ViewModelProvider(this).get(FormPageEditionViewModel::class.java)
        arguments?.let {
            formPageEditionViewModel.setPage(it.getSerializable(PAGE_TO_SHOW) as Page)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        databinding = FragmentPageEditionBinding.inflate(inflater,container,false)
        return databinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupList()

        attachObservers()
    }

    private fun addACtionsOnViews(page: Page) {
        databinding.edittextPageName.doAfterTextChanged {
            page.title = it.toString()
        }

        databinding.edittextPageDescription.doAfterTextChanged {
            page.description = it.toString()
        }
    }

    private fun attachObservers() {
        formPageEditionViewModel.page.observe(viewLifecycleOwner,{
            addDataToViews(it)

            addACtionsOnViews(it)
        })
    }

    private fun setupList() {
        adapterQuestionEdition = QuestionEditionAdapter(childFragmentManager){ item, questionPositiion, view ->
            val page = formPageEditionViewModel.page.value!!
            when(view.id){
                R.id.button_delete_question -> {
                    val actionListener = DialogInterface.OnClickListener { dialog, which ->
                        dialog.dismiss()
                        when(which){
                            DialogInterface.BUTTON_POSITIVE -> {
                                page.questions.remove(item as Question)
                                formPageEditionViewModel.page.value = page
                                adapterQuestionEdition.submitList(page.questions)
                                adapterQuestionEdition.notifyDataSetChanged()
                            }
                        }
                    }

                    MaterialAlertDialogBuilder(databinding.root.context)
                            .setTitle("Suppression d'une question")
                            .setMessage("Toutes les informations déja fournie pour cette question seront perdues")
                            .setBackground(ContextCompat.getDrawable(databinding.root.context, R.drawable.dialog_background))
                            .setPositiveButton(databinding.root.context.getString(R.string.supprimer),actionListener)
                            .setNegativeButton(databinding.root.context.getString(R.string.annuler),actionListener)
                            .show()

                }

            }

        }
        databinding.recyclerviewQuestions.apply {
            layoutManager = LinearLayoutManager(databinding.root.context)
            adapter = adapterQuestionEdition
        }
    }

    private fun addDataToViews(page: Page) {
        databinding.edittextPageDescription.setText(page.description)
        databinding.edittextPageName.setText(page.title)

        databinding.butttonShowPageDetails.setOnClickListener {
            if (databinding.layoutPageDetailsFields.visibility == View.VISIBLE) {
                databinding.layoutPageDetailsFields.gone()
            } else {
                databinding.layoutPageDetailsFields.show()
            }
            databinding.butttonShowPageDetails.rotation += 180f
        }

        adapterQuestionEdition.submitList(page.questions)

        databinding.buttonAddQuestion.setOnClickListener {
            val pageSize = page.questions.size
            page.questions.add(Question(
                    questionLabel = "Question ${pageSize + 1}"
            ))
            adapterQuestionEdition.submitList(page.questions)
            adapterQuestionEdition.notifyDataSetChanged()
        }
    }

}