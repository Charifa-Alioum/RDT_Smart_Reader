package cm.seeds.squarecm.retrofit

import android.util.Log
import cm.seeds.rdtsmartreader.retrofit.BasicAuthInterceptor
import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Protocol
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit

class RetrofitApiBuilder {

    companion object{


        private val credentials = Credentials.basic("covidtdr","Covidtdr@2020")

        private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
            .connectTimeout(2, TimeUnit.MINUTES)
            .protocols(listOf(Protocol.HTTP_2, Protocol.HTTP_1_1))
            .readTimeout(1, TimeUnit.MINUTES)
            .writeTimeout(1, TimeUnit.MINUTES)
            .retryOnConnectionFailure(true)
            .addInterceptor(BasicAuthInterceptor("covidtdr","Covidtdr@2020"))
            .build()

        fun getApiService(baseUrl : String) : ApiService{
            return Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService::class.java)
        }
    }

}