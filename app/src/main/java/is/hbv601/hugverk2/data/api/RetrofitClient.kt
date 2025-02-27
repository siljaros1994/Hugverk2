package `is`.hbv601.hbv601.hugverk2.data.api

import `is`.hbv601.hugverk2.data.api.ApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import android.content.Context
import android.util.Log

object RetrofitClient {
    // use your ip number, 130.208.101.191 is for the school.
    //private const val BASE_URL = "http://10.0.2.2:8080/"
    //private const val BASE_URL = "http://130.208.101.112:8080/" //skóli
    private const val BASE_URL = "http://192.168.1.15:8080/" //heima

    //retrive stored session cookie
    fun getSessionCookie(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("session_cookie", null)
    }
    private fun getOkHttpClient(context: Context): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .addInterceptor { chain ->
                val requestBuilder = chain.request().newBuilder()

                //retrive stores session cookie and attach it
                val sessionCookie = getSessionCookie(context)
                if (!sessionCookie.isNullOrEmpty()) {
                    requestBuilder.addHeader("Cookie", sessionCookie) //send the session cookie
                }
                val response = chain.proceed(requestBuilder.build())
                //capture session cookie
                val cookies = response.headers("Set-Cookie")
                if (cookies.isNotEmpty()) {
                    val cookie = cookies[0]
                    Log.d("RetrofitClient", "Captured Session Cookie: $cookie")

                    val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putString("session_cookie", cookie)
                    editor.apply()
                }

                response
            }
            .build()
    }
    fun getInstance(context: Context): ApiService {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(getOkHttpClient(context))
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(ApiService::class.java)
    }
}
















                // an OkHttpClient with logging interceptor
   // private val okHttpClient = OkHttpClient.Builder()
     //   .addInterceptor(HttpLoggingInterceptor().apply {
       //     level = HttpLoggingInterceptor.Level.BODY // Logs request and response details
       // })
       // .build()

    //val instance: ApiService by lazy {
      //  val retrofit = Retrofit.Builder()
        //    .baseUrl(BASE_URL)
          //  .client(okHttpClient)
            //.addConverterFactory(GsonConverterFactory.create())
            //.build()

        //retrofit.create(ApiService::class.java)
  //  /}
//}