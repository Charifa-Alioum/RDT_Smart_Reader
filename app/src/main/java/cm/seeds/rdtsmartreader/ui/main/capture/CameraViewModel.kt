package cm.seeds.rdtsmartreader.ui.main.capture

import android.app.Application
import androidx.lifecycle.*
import cm.seeds.rdtsmartreader.helper.*
import cm.seeds.retrofitrequestandnavigation.retrofit.RequestResult.Companion.error
import cm.seeds.retrofitrequestandnavigation.retrofit.RequestResult.Companion.loading
import cm.seeds.retrofitrequestandnavigation.retrofit.RequestResult.Companion.success
import cm.seeds.retrofitrequestandnavigation.retrofit.RequestResult
import kotlinx.coroutines.launch

class CameraViewModel(application: Application) : ViewModel() {

    var liveDataImagePath = MutableLiveData<Any>("")

    var resultOfScan : MutableLiveData<RequestResult<Any?>> = MutableLiveData()
    private val pythonMod = PythonMod.getPythonMod(application)

    fun scanAndHandleResult(imagePath : Any){

        viewModelScope.launch {

            try {

                resultOfScan.value = loading("Traitement en cours")

                if(imagePath is String){

                    val preprocessBitmap = pythonMod.preproccessImage(imagePath)

                    if(preprocessBitmap!=null){

                        resultOfScan.value = loading("Traitement en cours",preprocessBitmap)

                        val category = pythonMod.identify(preprocessBitmap)

                        resultOfScan.value = success(pythonMod.getLabelOfCategory(category))

                    }else{

                        resultOfScan.value = error("Erreur de prétraitement de l'image")

                    }

                }else{

                    resultOfScan.value = error("Nous avons besoin du chemin d'acces au fichier")

                }

            }catch (e : Exception){
                resultOfScan.value = error(e.message!!)
            }
        }

    }

}