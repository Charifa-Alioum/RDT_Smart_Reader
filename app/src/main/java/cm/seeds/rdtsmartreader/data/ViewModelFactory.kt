package cm.seeds.rdtsmartreader.data

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import cm.seeds.rdtsmartreader.ui.MainViewModel
import cm.seeds.rdtsmartreader.ui.form.form_edition.FormCreationActivityViewModel
import cm.seeds.rdtsmartreader.ui.form.all_form.AllFormViewModel
import cm.seeds.rdtsmartreader.ui.main.capture.CameraViewModel
import cm.seeds.rdtsmartreader.ui.main.home.HomeViewModel
import cm.seeds.rdtsmartreader.ui.main.informations.InformationsViewModel
import cm.seeds.rdtsmartreader.ui.main.user_profil.UserProfilViewModel

class ViewModelFactory(val application: Application) : ViewModelProvider.Factory{

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        return when{

            modelClass.isAssignableFrom(HomeViewModel::class.java) -> HomeViewModel(application) as T

            modelClass.isAssignableFrom(MainViewModel::class.java) -> MainViewModel(application) as T

            modelClass.isAssignableFrom(InformationsViewModel::class.java) -> InformationsViewModel(application) as T

            modelClass.isAssignableFrom(CameraViewModel::class.java) -> CameraViewModel(application) as T

            modelClass.isAssignableFrom(AllFormViewModel::class.java) -> AllFormViewModel(application) as T

            modelClass.isAssignableFrom(FormCreationActivityViewModel::class.java) -> FormCreationActivityViewModel(application) as T

            modelClass.isAssignableFrom(UserProfilViewModel::class.java) -> UserProfilViewModel(application) as T

            else -> throw Exception("Une erreur de classe")

        }

    }
}