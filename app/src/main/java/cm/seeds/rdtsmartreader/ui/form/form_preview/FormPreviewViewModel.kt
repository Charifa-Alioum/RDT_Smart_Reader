package cm.seeds.rdtsmartreader.ui.form.form_preview

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cm.seeds.rdtsmartreader.modeles.Form

class FormPreviewViewModel : ViewModel() {

    val form = MutableLiveData<Form>()

    fun setForm(form: Form?){
        if(form!=null){
            this.form.value = form
        }
    }

}