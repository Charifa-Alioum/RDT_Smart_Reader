package cm.seeds.rdtsmartreader.retrofit

import android.util.Log
import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.Response

class BasicAuthInterceptor(userName: String, password : String) : Interceptor {

    private var credentials: String = Credentials.basic(userName, password)

    override fun intercept(chain: Interceptor.Chain): Response {
        val newRequest = chain.request().newBuilder()
                .header("Authorization", credentials)
                .build()

/*                val response = chain.proceed(newRequest)
                Log.e("TAG"," data "+response.body()?.string())
                response.close()*/

        return chain.proceed(newRequest)
    }
}