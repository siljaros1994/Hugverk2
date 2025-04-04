package `is`.hbv601.hbv601.hugverk2.data.api

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


    private const val BASE_URL = "http://192.168.56.1:8080/"


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
                // Forcefully attach JSESSIONID
                val sessionCookie = cookies.find { it.name == "JSESSIONID" }
                val requestWithSession = if (sessionCookie != null) {
                    request.newBuilder()
                        .addHeader("Cookie", "JSESSIONID=${sessionCookie.value}")
                        .build()
                } else {
                    request
                }

                val response = chain.proceed(requestWithSession) //(request)
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

    fun getCookieString(): String {
        return cookieManager.cookieStore.cookies.joinToString("; ") { it.toString() }
    }
}