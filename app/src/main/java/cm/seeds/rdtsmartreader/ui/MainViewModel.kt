package cm.seeds.rdtsmartreader.ui

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.seeds.rdtsmartreader.data.AppDatabase
import cm.seeds.rdtsmartreader.data.RequestResult
import cm.seeds.rdtsmartreader.helper.*
import cm.seeds.rdtsmartreader.modeles.*
import cm.seeds.rdtsmartreader.retrofit.DhisStatus
import cm.seeds.squarecm.retrofit.ApiService
import cm.seeds.squarecm.retrofit.RetrofitApiBuilder
import kotlinx.coroutines.launch
import java.lang.Exception

class MainViewModel(private val application: Application) : ViewModel() {
    private val dao = AppDatabase.database(application).getDao()
    private val apiService = RetrofitApiBuilder.getApiService(ApiService.BASE_URL)
    val serverState = MutableLiveData(ServerState(state = ServerListener.State.STOPPED))
    val allUsers : LiveData<List<User>> = dao.getUsers()
    val synchronisationStatusLiveData = MutableLiveData<RequestResult<SynchronisationStatus>>()



    fun synchronise(){
        viewModelScope.launch {

            try {
                val FORMAT_DATA = "yyyy-MM-dd"
                val infoToSynchronise = allUsers.value?.filter { user -> !user.synchronised}?.toMutableList()

                if(!infoToSynchronise.isNullOrEmpty()){

                    val synchonisedUser = mutableListOf<User>()

                    val synchronisationStatus = SynchronisationStatus(
                        userSynched = 0,
                        userToSynch = infoToSynchronise.size.toLong(),
                        synchronisationFinished = false
                    )

                    synchronisationStatusLiveData.value = RequestResult.loading(synchronisationStatus)

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

                        dataValues.add(DataValue(dataElement = "HHaxq67VW0Y", value = formatDate(it.dateDebutSymptomes,FORMAT_DATA)))
                        dataValues.add(DataValue(dataElement = "HJPMbxdYV4I", value = if(it.haveSymptoms) "Oui" else "Non"))
                        dataValues.add(DataValue(dataElement = "DEYynacgiIR", value = it.autres))
                        dataValues.add(DataValue(dataElement = "JNY1RlbivSS", value = if(it.isAlive) "Vivant" else "Décédé"))
                        dataValues.add(DataValue(dataElement = "uWKVr6Nslos", value = it.test?.conclusion))
                        dataValues.add(DataValue(dataElement = "YFJDsJXB4u3", value = formatDate(it.dateDeces,FORMAT_DATA)))

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
                        dataValues.add(DataValue(dataElement = "X74sz6MKgH2", value = formatDate(it.test?.datePrelevement,FORMAT_DATA)))

                        val synchronisationParams = SynchronisationParams(
                            eventDate = formatDate(it.dateEnregistrement,FORMAT_DATA),
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

                            synchronisationStatusLiveData.value =
                                RequestResult.loading(synchronisationStatus)
                        }
                    }

                    synchronisationStatus.apply {
                        synchronisationFinished = true
                    }

                    val numberNotSynched = synchronisationStatus.userToSynch - synchronisationStatus.userSynched
                    if(numberNotSynched==0.toLong()){
                        synchronisationStatusLiveData.value =
                            RequestResult.success(synchronisationStatus)
                    }else{
                        val message = "les inforations de $numberNotSynched cas n'ont pas pu être synchronisée. Vérifiez votre connexion et rééssayez"
                        synchronisationStatusLiveData.value =
                            RequestResult.error(message, synchronisationStatus)
                    }

                    if(synchonisedUser.isNotEmpty()){
                        dao.saveUser(synchonisedUser)
                    }
                }

            }catch (e : Exception){
                e.printStackTrace()
                synchronisationStatusLiveData.value =
                    RequestResult.error(e.stackTraceToString(), null)
            }

        }
    }
}
