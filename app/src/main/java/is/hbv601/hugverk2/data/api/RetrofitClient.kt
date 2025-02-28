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
    private const val BASE_URL = "http://192.168.101.4:8080/"

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

    fun getInstance(context: Context): ApiService {
        return retrofit.create(ApiService::class.java)
    }
}