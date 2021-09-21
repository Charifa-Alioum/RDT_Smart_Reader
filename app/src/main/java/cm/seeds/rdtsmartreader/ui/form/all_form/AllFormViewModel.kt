package cm.seeds.rdtsmartreader.ui.form.all_form

import android.app.Application
import androidx.lifecycle.ViewModel
import cm.seeds.rdtsmartreader.data.AppDatabase

class AllFormViewModel(application: Application) : ViewModel() {
    private val dao = AppDatabase.database(application).getDao()
    val forms = dao.getAllForms()
}