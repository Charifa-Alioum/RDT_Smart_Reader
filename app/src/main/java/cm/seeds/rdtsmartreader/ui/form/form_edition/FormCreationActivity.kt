package cm.seeds.rdtsmartreader.ui.form.form_edition

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import cm.seeds.rdtsmartreader.R
import cm.seeds.rdtsmartreader.adapters.FormPagesAdapter
import cm.seeds.rdtsmartreader.data.ViewModelFactory
import cm.seeds.rdtsmartreader.databinding.ActivityCreateFormBinding
import cm.seeds.rdtsmartreader.helper.showToast
import cm.seeds.rdtsmartreader.modeles.Form
import cm.seeds.rdtsmartreader.modeles.Page
import cm.seeds.rdtsmartreader.modeles.Question
import cm.seeds.rdtsmartreader.ui.form.form_preview.FormPreviewActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText

class FormCreationActivity : AppCompatActivity() {

    private lateinit var dataBinding: ActivityCreateFormBinding
    private lateinit var viewModel: FormCreationActivityViewModel
    private lateinit var formPagesAdapter: FormPagesAdapter
    private var whatToDo = CREATE_FORM

    companion object {

        const val WHAT_TO_DO = "WHAT_TO_DO"
        const val CREATE_FORM = 1
        const val UPDATE_FORM = 2

        const val FORM = "FORM"

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this, ViewModelFactory(application)).get(
            FormCreationActivityViewModel::class.java)
        dataBinding = ActivityCreateFormBinding.inflate(layoutInflater)
        setContentView(dataBinding.root)

        whatToDo = intent.getIntExtra(WHAT_TO_DO, CREATE_FORM)

        attachObservers()

        addActionOnViews()

        when (whatToDo) {

            CREATE_FORM -> {
                askFormDetails()
            }

            UPDATE_FORM -> {
                val formToEdit = intent.getSerializableExtra(FORM) as Form
                viewModel.setForm(formToEdit)
            }
        }
    }

    override fun onBackPressed() {
        if(viewModel.modelHaveBeenEdited()){
            val actionListener = DialogInterface.OnClickListener{ dialog, which ->
                dialog.dismiss()
                when(which){
                    DialogInterface.BUTTON_POSITIVE -> {
                        viewModel.saveForm()
                        super.onBackPressed()
                    }

                    DialogInterface.BUTTON_NEGATIVE ->{
                        super.onBackPressed()
                    }
                }
            }

            MaterialAlertDialogBuilder(this)
                .setBackground(ContextCompat.getDrawable(this,R.drawable.dialog_background))
                .setMessage("Les corrections apportées à ce formulaire seront perdues.")
                .setPositiveButton("Sauvegarder et quitter",actionListener)
                .setNegativeButton("Quitter sans sauvegarder",actionListener)
                .setNeutralButton("Ne pas quitter",null)
                .show()
        }else{
            super.onBackPressed()
        }
    }

    private fun attachObservers() {

        viewModel.form.observe(this, {
            addDataToViews(it)
        })

    }

    private fun addDataToViews(form: Form) {
        dataBinding.title.text = form.formName
        dataBinding.subtitle.text = "${form.pages.size} pages"

        formPagesAdapter = FormPagesAdapter(this,isEdition = true).apply {
            submitList(form.pages)
        }

        dataBinding.viewpagerFormPage.adapter = formPagesAdapter
    }

    private fun askFormDetails() {

        val dialogRoot = LayoutInflater.from(this).inflate(R.layout.dialog_form_details, null, false)

        val actionListener = DialogInterface.OnClickListener { dialog, which ->
            when (which) {
                DialogInterface.BUTTON_NEGATIVE -> {
                    dialog.dismiss()
                    finish()
                }
            }
        }

        val dialog = MaterialAlertDialogBuilder(this)
                .setView(dialogRoot)
                .setBackground(ContextCompat.getDrawable(this@FormCreationActivity, R.drawable.dialog_background))
                .setTitle("Création d'un formulaire")
                .setCancelable(false)
                .setPositiveButton("Créer", null)
                .setNegativeButton(getString(R.string.annuler), actionListener)
                .show()

        val positiveButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE)
        positiveButton.setOnClickListener {
            val formName = dialogRoot.findViewById<TextInputEditText>(R.id.edittext_form_name).text.toString()
            if (formName.isBlank()) {
                showToast(this@FormCreationActivity, "Un formulaire doit avoir un nom")
            } else {
                dialog.dismiss()
                val form = Form(formName = formName,
                        pages = mutableListOf(
                                Page(title = "Page 1",
                                        questions = mutableListOf(
                                                Question(
                                                        questionLabel = "Question 1")
                                        )
                                )
                        )
                )
                viewModel.setForm(form)
            }
        }
    }

    private fun addActionOnViews() {

        dataBinding.buttonSave.setOnClickListener {
            viewModel.saveForm()
            showToast(this,"Formulaire enregistré")
        }

        dataBinding.buttoPreview.setOnClickListener {
            startActivity(Intent(this@FormCreationActivity,FormPreviewActivity::class.java).apply {
                putExtra(FormPreviewActivity.FORM_TO_SHOW,viewModel.form.value)
            })
        }

        dataBinding.backButton.setOnClickListener {
            onBackPressed()
        }

        dataBinding.buttonAddPage.setOnClickListener {
            val form = viewModel.form.value
            form?.let {
                val numberOfPage = it.pages.size
                val newPage = Page(title = "Page ${numberOfPage + 1}",
                        questions = mutableListOf(
                                Question(
                                        questionLabel = "Question 1")
                        )
                )
                it.pages.add(newPage)
                formPagesAdapter.submitList(it.pages)
                formPagesAdapter.notifyDataSetChanged()
                dataBinding.viewpagerFormPage.setCurrentItem(it.pages.size - 1, true)
                dataBinding.subtitle.text = "${it.pages.size} Pages"
            }
        }
    }
}