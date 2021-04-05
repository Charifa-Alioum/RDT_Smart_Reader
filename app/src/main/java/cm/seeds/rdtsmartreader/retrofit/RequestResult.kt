package cm.seeds.retrofitrequestandnavigation.retrofit

import com.google.gson.annotations.SerializedName

data class RequestResult<out T>(
    @SerializedName("body")
    val data : T?,
    val message : String,
    val status: Status
) {

    companion object{

        fun <T> success(data: T?, msg: String = ""): RequestResult<T> {
            return RequestResult(data,msg,Status.SUCCESS)
        }

        fun <T> error(msg: String, data: T? = null): RequestResult<T> {
            return RequestResult(data,msg,Status.ERROR)
        }

        fun <T> loading(msg: String, data: T? = null): RequestResult<T> {
            return RequestResult(data,msg,Status.LOADING)
        }

    }

}