package cm.seeds.rdtsmartreader.ui.main.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import cm.seeds.rdtsmartreader.data.AppDatabase
import cm.seeds.rdtsmartreader.modeles.User

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = AppDatabase.database(application).getDao()

    var allUsers : LiveData<List<User>> = dao.getUsers()

}