package cm.seeds.rdtsmartreader.ui.main.informations

import android.app.Application
import android.text.TextUtils
import androidx.lifecycle.*
import cm.seeds.rdtsmartreader.data.AppDatabase
import cm.seeds.rdtsmartreader.data.RequestResult.Companion.error
import cm.seeds.rdtsmartreader.data.RequestResult.Companion.loading
import cm.seeds.rdtsmartreader.data.RequestResult.Companion.success
import cm.seeds.rdtsmartreader.data.RequestResult
import cm.seeds.rdtsmartreader.helper.*
import cm.seeds.rdtsmartreader.modeles.*
import cm.seeds.rdtsmartreader.retrofit.DhisStatus
import cm.seeds.squarecm.retrofit.ApiService
import cm.seeds.squarecm.retrofit.RetrofitApiBuilder
import kotlinx.coroutines.launch
import java.lang.Exception

class InformationsViewModel(val application: Application) : ViewModel() {

    private val dao = AppDatabase.database(application).getDao()
    private val apiService = RetrofitApiBuilder.getApiService(ApiService.BASE_URL)

    val userSavingResult = MutableLiveData<RequestResult<User>>()
    val userToSave = MutableLiveData(User(coordonnee = loadLocation(application)))
    val testToSave = MutableLiveData<Test>(null)
    val liveDataAllDomicile = dao.getDomiciles()
    var liveDataAllRegion = MutableLiveData<List<String>>()
    var liveDataAllDistrict = MutableLiveData<List<String>>()
    var liveDataAllAireDeSante = MutableLiveData<List<String>>()

    init {
        liveDataAllRegion.value = mapRegionAndDistrict.keys.asIterable().sortedBy { it }

    }

    fun setupDistrictOf(region : String){
        liveDataAllDistrict.value = mapRegionAndDistrict[region]
    }

    fun saveUser(user: User? = null){

       when{
           user!=null ->{
               viewModelScope.launch {
                   dao.saveUser(listOf(user))
               }
           }

           else -> {

               viewModelScope.launch {
                   userSavingResult.value = loading()
                   try {
                       val userToSave = userToSave.value
                       userToSave?.test = testToSave.value

                       if(userToSave?.test != null){
                           userToSave.synchronised = false
                           dao.saveUser(mutableListOf(userToSave))
                       }

                       userSavingResult.value = success(userToSave)

                       this@InformationsViewModel.userToSave.value = User(coordonnee = loadLocation(application))

                   }catch (e : Exception){
                       userSavingResult.value = error(e.message?:"Erreur lors de l'enregistrement",null)
                   }
               }
           }

       }

    }

    fun allFieldIsCorrect(user: User?): Boolean {
        var allIsCorrect = true

        if(user!=null){
            if(TextUtils.isEmpty(user.userId)){
                allIsCorrect = false
            }

            if(user.userAge<=0){
                allIsCorrect = false
            }

            if(TextUtils.isEmpty(user.userDomicile)){
                allIsCorrect = false
            }

            if(TextUtils.isEmpty(user.userName)){
                allIsCorrect = false
            }

            if(TextUtils.isEmpty(user.telephone)){
                allIsCorrect = false
            }
        }else{
            allIsCorrect = false
        }

        return allIsCorrect
    }

}