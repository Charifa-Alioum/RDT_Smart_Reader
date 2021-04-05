package cm.seeds.rdtsmartreader.data

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import cm.seeds.rdtsmartreader.ui.main.capture.CameraViewModel
import cm.seeds.rdtsmartreader.ui.main.home.HomeViewModel
import cm.seeds.rdtsmartreader.ui.main.informations.InformationsViewModel

class ViewModelFactory(val application: Application) : ViewModelProvider.Factory{

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        return when{

            modelClass.isAssignableFrom(HomeViewModel::class.java) -> HomeViewModel(application) as T

            modelClass.isAssignableFrom(InformationsViewModel::class.java) -> InformationsViewModel(application) as T

            modelClass.isAssignableFrom(CameraViewModel::class.java) -> CameraViewModel(application) as T

            else -> throw Exception("Une erreur de classe")

        }

    }
}