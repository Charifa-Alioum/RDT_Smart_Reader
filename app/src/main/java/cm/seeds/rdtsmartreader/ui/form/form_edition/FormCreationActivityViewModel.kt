package cm.seeds.rdtsmartreader.ui.form.form_edition

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.seeds.rdtsmartreader.data.AppDatabase
import cm.seeds.rdtsmartreader.databinding.ActivityCreateFormBinding
import cm.seeds.rdtsmartreader.helper.*
import cm.seeds.rdtsmartreader.modeles.Form
import kotlinx.coroutines.launch

class FormCreationActivityViewModel(application: Application) : ViewModel() {

    private var previousForm : Form? = null

    private val dao = AppDatabase.database(application).getDao()

    val form = MutableLiveData<Form>()

    fun setForm(form: Form) {
        this.form.value = form
        previousForm = form
    }

    fun saveForm() {
        viewModelScope.launch {
            val formToSave = form.value
            formToSave?.haveErrors = !processVerification(formToSave)
            if(formToSave!=null){
                dao.saveForms(listOf(formToSave))
            }
            previousForm = formToSave
            form.value = formToSave!!
        }
    }

    fun modelHaveBeenEdited(): Boolean {
        /*val actualForm = form.value
        return actualForm != previousForm*/
        return true
    }


    /**
     * Méthode de vérification d'un formulaire.
     * Renvoit true si le formulaire est correct et false sinon
     */
    private fun processVerification(formToSave: Form?): Boolean {
        if (formToSave != null) {
            //On Vérifie le nom
            if (formToSave.formName.isBlank()) {
                return false
            }

            //On vérifie le nombre de page
            if (formToSave.pages.isNullOrEmpty()) {
                return false
            }

            //Pour chaque page, on vérifie
            formToSave.pages.forEach {

                //le nom est correct
                if (it.title.isBlank()) {
                    return false
                }
                //Il ya des pages
                if (it.questions.isNullOrEmpty()) {
                    return false
                }

                //Pour chaque question
                it.questions.forEach { question ->
                    //le nom de la question
                    if (question.questionLabel.isBlank()) {
                        return false
                    }

                    if (question.questionType.isBlank()) {
                        return false
                    }

                    if (question.questionLabelForSaving.isBlank()) {
                        return false
                    }

                    when (question.questionType) {

                        TYPE_QUESTION_DATE -> {
                            if (question.minDate == 0.toLong() || question.maxDate == 0.toLong() || question.maxDate < question.minDate) {
                                return false
                            }
                        }

                        TYPE_QUESTION_TEXT, TYPE_QUESTION_NUMBER_ENTIER, TYPE_QUESTION_NUMBER_REEL -> {

                        }

                        TYPE_QUESTION_YES_OR_NO, TYPE_QUESTION_RADIO, TYPE_QUESTION_CHECKOX, TYPE_QUESTION_SELECT_MULTIPLE, TYPE_QUESTION_SELECT_ONE -> {

                            if (question.options.isNullOrEmpty()) {
                                return false
                            }

                            question.options.forEach { option ->
                                if (option.optionLabel.isBlank()) {
                                    return false
                                }
                            }
                        }
                    }
                }
            }

            return true
        }

        return false
    }

}