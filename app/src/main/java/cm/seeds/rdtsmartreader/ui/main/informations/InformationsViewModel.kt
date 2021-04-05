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

    var userSavingResult : LiveData<RequestResult<User>>? = null

    val synchronisationStatusLiveData : MutableLiveData<RequestResult<SynchronisationStatus>> = MutableLiveData()

    var allUsers : LiveData<List<User>>

    var userToSave : MutableLiveData<User> = MutableLiveData(User(coordonnee = loadLocation(application)))

    var testToSave : MutableLiveData<Test> = MutableLiveData(null)

    var liveDataAllDomicile : LiveData<List<String>>

    var liveDataAllRegion = MutableLiveData<List<String>>()

    var liveDataAllDistrict = MutableLiveData<List<String>>()

    var liveDataAllAireDeSante = MutableLiveData<List<String>>()

    init {
        allUsers = dao.getUsers()

        liveDataAllDomicile = dao.getDomiciles()

        liveDataAllRegion.value = mapRegionAndDistrict.keys.asIterable().sortedBy { it }

        //liveDataAllDistrict = dao.getDisctrics()

        //liveDataAllAireDeSante = dao.getAiresDeSantes()
    }

    fun setupDistrictOf(region : String){

        liveDataAllDistrict.value = mapRegionAndDistrict[region]

    }

    fun saveUser(){

        userSavingResult = liveData {

            emit(RequestResult.loading(null))

            try {
                val user = userToSave.value

                user?.test = testToSave.value

                if(user?.test != null){
                    user.synchronised = false
                    dao.saveUser(mutableListOf(user))
                }

                emit(RequestResult.success(userToSave.value))

                userToSave = MutableLiveData(User(coordonnee = loadLocation(application)))

            }catch (e : Exception){
                emit(RequestResult.error(e.message?:"Erreur lors de l'enregistrement",null))
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


    fun synchronise(){

        viewModelScope.launch {

            try {

                val FORMAT_DATA = "yyyy-MM-dd"

                //val infoToSynchronise = allUsers.value?.filter { user -> !user.synchronised && !TextUtils.isEmpty(user.test?.conclusion) }
                val infoToSynchronise = allUsers.value?.filter { user -> !user.synchronised}?.toMutableList()

/*            (1..100000).forEach {
                allUsers.value?.forEach { user ->
                    infoToSynchronise?.add(user)
                }
            }*/

                if(!infoToSynchronise.isNullOrEmpty()){

                    val synchonisedUser = mutableListOf<User>()

/*                val synchronisationStatus = SynchronisationStatus(
                        userSynched = 0,
                        userToSynch = infoToSynchronise.size,
                        synchronisationFinished = false
                )*/

                    val synchronisationStatus = SynchronisationStatus(
                            userSynched = 0,
                            userToSynch = infoToSynchronise.size.toLong(),
                            synchronisationFinished = false
                    )

                    synchronisationStatusLiveData.value = loading(synchronisationStatus)

                    infoToSynchronise.forEach {

                        val dataValues = mutableListOf<DataValue>()

                        //Infos sur la personne
                        dataValues.add(DataValue(dataElement = "Wr0LXes6UNr", value = it.userName))

                        dataValues.add(DataValue(dataElement = "bLFPoUNWmaH", value = it.epiId))

                        dataValues.add(DataValue(dataElement = "GGLXr2skute", value = it.userDomicile))

                        dataValues.add(DataValue(dataElement = "YMiP4BhlgVE", value = it.userAge.toString()))
                        if(it.isAgeInYear){
                            dataValues.add(DataValue(dataElement = "w2khUXj4N53", value = it.isAgeInYear.toString()))
                        }else{
                            dataValues.add(DataValue(dataElement = "QMTsVh3uNH3", value = (!it.isAgeInYear).toString()))
                        }

                        dataValues.add(DataValue(dataElement = "IWsHB4bMMrb", value = it.genre))

                        dataValues.add(DataValue(dataElement = "ppF52yDaPvL", value = it.telephone))

                        if(listAllProfession.contains(it.profession)){
                            dataValues.add(DataValue(dataElement = "lsjGdSsOcpm", value = it.profession))
                        }else{
                            dataValues.add(DataValue(dataElement = "lsjGdSsOcpm", value = "Autres"))
                            dataValues.add(DataValue(dataElement = "gLhVIrH6KM8", value = it.profession))
                        }

                        dataValues.add(DataValue(dataElement = "RcWqOEUyqYK", value = it.ville))
                        dataValues.add(DataValue(dataElement = "cnkevMB2Dku", value = it.aireDeSante))
                        dataValues.add(DataValue(dataElement = "mNw1VInPhcD", value = it.district))

                        dataValues.add(DataValue(dataElement = "e8IfJ2s3ANn", value = if(it.isDoctor) "Oui" else "Non"))
                        if(it.isDoctor){
                            dataValues.add(DataValue(dataElement = "z8hKI0WwiUd", value = it.professionPersonnelSante))
                        }

                        dataValues.add(DataValue(dataElement = "HHaxq67VW0Y", value = parseDateToString(it.dateDebutSymptomes,FORMAT_DATA)))
                        dataValues.add(DataValue(dataElement = "HJPMbxdYV4I", value = if(it.asymptomatique) "Oui" else "Non"))
                        dataValues.add(DataValue(dataElement = "DEYynacgiIR", value = it.autres))
                        dataValues.add(DataValue(dataElement = "JNY1RlbivSS", value = if(it.isAlive) "Vivant" else "Décédé"))
                        dataValues.add(DataValue(dataElement = "uWKVr6Nslos", value = it.test?.conclusion))
                        dataValues.add(DataValue(dataElement = "YFJDsJXB4u3", value = parseDateToString(it.dateDeces,FORMAT_DATA)))

                        //Symptome
                        dataValues.add(DataValue(dataElement = "kjv6QFN1TmN", value = it.diarhee))
                        dataValues.add(DataValue(dataElement = "EHfcbDKjFaO", value = it.difficultesARespirerer))
                        dataValues.add(DataValue(dataElement = "j1WRW3UZjjd", value = it.douleursMusculaires))
                        dataValues.add(DataValue(dataElement = "cuyJWNdHxQA", value = it.ecoulementNasal))
                        dataValues.add(DataValue(dataElement = "muqgWADpAcH", value = it.eruptionCutanee))
                        dataValues.add(DataValue(dataElement = "Ycj61tCk67H", value = it.essouflement))
                        dataValues.add(DataValue(dataElement = "sXnJuJetQZE", value = it.fatigueIntense))
                        dataValues.add(DataValue(dataElement = "af1cOAgiU6k", value = it.fievre))
                        dataValues.add(DataValue(dataElement = "HEJ4il6fEMw", value = it.frisson))
                        dataValues.add(DataValue(dataElement = "nqGjez1kfNL", value = it.toux))
                        dataValues.add(DataValue(dataElement = "kPxVi042hpX", value = it.conjonctivite))
                        dataValues.add(DataValue(dataElement = "XMO1kcT0Hfe", value = it.test?.indicationPrelevement))
                        dataValues.add(DataValue(dataElement = "mn7rXpDHQOA", value = it.malDeGorge))
                        dataValues.add(DataValue(dataElement = "bftDDx4sM7I", value = it.perteOdorat))
                        dataValues.add(DataValue(dataElement = "lm8UZ2xLGYc", value = it.perteSaveur))
                        dataValues.add(DataValue(dataElement = "TKuS8VPiouh", value = it.vomissement))

                        //Infos sur le test
                        dataValues.add(DataValue(dataElement = "NsVbuZFNmoT", value = it.test?.manipulateur))
                        dataValues.add(DataValue(dataElement = "BFgnUASLoJo", value = it.test?.telephoneManipulateur))
                        dataValues.add(DataValue(dataElement = "n3nkBPy7WsN", value = it.test?.natureTest))

                        if(it.test?.natureTest == NATURE_TEST_COVID_19_AC){
                            dataValues.add(DataValue(dataElement = "l2vEIvJEpmI", value = it.test?.resultatsCovidIgg))
                            dataValues.add(DataValue(dataElement = "FaLsG2eu75m", value = it.test?.resultatsCovidIgm))
                        }else if(it.test?.natureTest == NATURE_TEST_COVID_19_AG){
                            dataValues.add(DataValue(dataElement = "Hb41AFhxnOW", value = it.test?.resultatsCovidAg))
                        }


                        dataValues.add(DataValue(dataElement = "BD1vYi9Wyrk", value = it.test?.typePrelevement))
                        dataValues.add(DataValue(dataElement = "X74sz6MKgH2", value = parseDateToString(it.test?.datePrelevement,FORMAT_DATA)))

                        val synchronisationParams = SynchronisationParams(
                                eventDate = parseDateToString(it.dateEnregistrement,FORMAT_DATA),
                                status = DhisStatus.ACTIVE.toString(),
                                storedBy = "Krawist",
                                coordinate = it.coordonnee?: loadLocation(application)!!,
                                dataValues = dataValues
                        )

                        val responses = apiService.saveInformations(synchronisationParams)

                        if (responses.httpStatusCode == 200){
                            it.synchronised = true
                            synchonisedUser.add(it)

                            synchronisationStatus.apply {
                                userSynched++
                            }

                            synchronisationStatusLiveData.value = loading(synchronisationStatus)
                        }
                    }

                    synchronisationStatus.apply {
                        synchronisationFinished = true
                    }

                    val numberNotSynched = synchronisationStatus.userToSynch - synchronisationStatus.userSynched
                    if(numberNotSynched==0.toLong()){
                        synchronisationStatusLiveData.value = success(synchronisationStatus)
                    }else{
                        val message = "les inforations de $numberNotSynched cas n'ont pas pu être synchronisée. Vérifiez votre connexion et rééssayez"
                        synchronisationStatusLiveData.value = error(message,synchronisationStatus)
                    }

                    if(synchonisedUser.isNotEmpty()){
                        dao.saveUser(synchonisedUser)
                    }
                }

            }catch (e : Exception){
                e.printStackTrace()
                synchronisationStatusLiveData.value = error(e.stackTraceToString(),null)
            }

        }

    }

}