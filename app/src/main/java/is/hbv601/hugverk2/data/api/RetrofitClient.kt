package `is`.hbv601.hbv601.hugverk2.data.api

import android.content.Context
import android.util.Log
import `is`.hbv601.hugverk2.data.api.ApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.CookieManager
import java.net.CookiePolicy
import okhttp3.JavaNetCookieJar

object RetrofitClient {
    private const val BASE_URL = "http://192.168.56.1:8080/" //Replace your API here as well in other files

    // Create a CookieManager that accepts all cookies.
    private val cookieManager = CookieManager().apply {
        setCookiePolicy(CookiePolicy.ACCEPT_ALL)
    }

    // Use JavaNetCookieJar so cookies persist between requests.
    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .cookieJar(JavaNetCookieJar(cookieManager))
            .addInterceptor { chain ->
                val request = chain.request()
                val cookies = cookieManager.cookieStore.cookies
                Log.d("CookieManager", "Before request, stored cookies: $cookies")
                val response = chain.proceed(request)
                Log.d("CookieManager", "After response, stored cookies: ${cookieManager.cookieStore.cookies}")
                response
            }
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()
    }

    // Create a singleton Retrofit instance.
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun getInstance(): ApiService {
       return retrofit.create(ApiService::class.java)
    }

    object RetrofitClient {
        private var INSTANCE : ApiService? = null
        fun getInstance(): ApiService {
            if (INSTANCE == null) {
                val cookieManager = CookieManager()
                cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL)

                val client = OkHttpClient.Builder()
                    .cookieJar(JavaNetCookieJar(cookieManager))
                    .build()
                val retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build()

                INSTANCE = retrofit.create(ApiService::class.java)
            }
            return INSTANCE !!
        }
    }
}