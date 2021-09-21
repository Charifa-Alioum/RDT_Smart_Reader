package cm.seeds.rdtsmartreader.ui.form.form_edition.page

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cm.seeds.rdtsmartreader.modeles.Page

class FormPageEditionViewModel : ViewModel() {

    val page = MutableLiveData<Page>()

    fun setPage(page: Page){
        this.page.value = page
    }
}