package cm.seeds.squarecm.retrofit

import cm.seeds.rdtsmartreader.data.DhisResponse
import cm.seeds.rdtsmartreader.modeles.SynchronisationParams
import retrofit2.http.Body
import retrofit2.http.POST


interface ApiService {

    companion object{
        const val BASE_URL = "http://41.211.120.34:8082/api/26/"
    }

    @POST("events")
    suspend fun saveInformations(@Body synchronisationParams: SynchronisationParams) : DhisResponse

}