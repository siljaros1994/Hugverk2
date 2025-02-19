package `is`.hbv601.hbv601.hugverk2.data.api

import `is`.hbv601.hugverk2.data.api.ApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    // use your ip number, 130.208.101.191 is for the school.
    //private const val BASE_URL = "http://10.0.2.2:8080/"
    private const val BASE_URL = "http://130.208.101.191:8080/"

    // an OkHttpClient with logging interceptor
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY // Logs request and response details
        })
        .build()

    val instance: ApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(ApiService::class.java)
    }
}