package cm.seeds.rdtsmartreader.ui.main.capture

import android.app.Application
import androidx.lifecycle.*
import cm.seeds.rdtsmartreader.data.AppDatabase
import cm.seeds.rdtsmartreader.helper.*
import cm.seeds.rdtsmartreader.modeles.Image
import cm.seeds.retrofitrequestandnavigation.retrofit.RequestResult.Companion.error
import cm.seeds.retrofitrequestandnavigation.retrofit.RequestResult.Companion.loading
import cm.seeds.retrofitrequestandnavigation.retrofit.RequestResult.Companion.success
import cm.seeds.retrofitrequestandnavigation.retrofit.RequestResult
import kotlinx.coroutines.launch
import java.io.File

class CameraViewModel(application: Application) : ViewModel() {

    var dao = AppDatabase.database(application).getDao()
    var liveDataImagePath = MutableLiveData<Any>("")
    var recogniseText = MutableLiveData<List<String?>?>()
    var resultOfScan : MutableLiveData<RequestResult<Any?>> = MutableLiveData()
    private val pythonMod = PythonMod.getPythonMod(application)

    var saveImages = dao.getImages()

    fun scanAndHandleResult(imagePath : Any){

        viewModelScope.launch {

            try {

                resultOfScan.value = loading("Traitement en cours")

                if(imagePath is String){

                    val preprocessBitmaps = pythonMod.preproccessImage(imagePath)

                    if(!preprocessBitmaps.isNullOrEmpty()){

                        resultOfScan.value = loading("Traitement en cours",preprocessBitmaps[0])

                        if(preprocessBitmaps[0]!=null){
                            val category = pythonMod.identify(preprocessBitmaps[0]!!)
                            val label = pythonMod.getLabelOfCategory(category)
                            resultOfScan.value = success(label)

                            viewModelScope.launch {
                                dao.saveImages(listOf(Image(filePath = imagePath,name = imagePath,result = label)))
                            }
                        }

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