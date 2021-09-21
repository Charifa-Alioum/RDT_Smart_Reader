package cm.seeds.rdtsmartreader.ui.main.user_profil

import android.app.Application
import androidx.lifecycle.ViewModel
import cm.seeds.rdtsmartreader.data.AppDatabase

class UserProfilViewModel(application: Application) : ViewModel() {

    private val dao = AppDatabase.database(application).getDao()

    val forms = dao.getAllForms()

}