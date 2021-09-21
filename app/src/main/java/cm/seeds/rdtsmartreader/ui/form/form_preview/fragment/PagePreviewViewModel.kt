package cm.seeds.rdtsmartreader.ui.form.form_preview.fragment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cm.seeds.rdtsmartreader.modeles.Page

class PagePreviewViewModel : ViewModel() {

    val page = MutableLiveData<Page>()
    val position = MutableLiveData<Int>()

    fun setPage(page: Page, position : Int){
        this.page.value = page
        this.position.value = position
    }

}