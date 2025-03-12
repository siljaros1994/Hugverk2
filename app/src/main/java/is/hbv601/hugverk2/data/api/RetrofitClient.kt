package `is`.hbv601.hugverk2.data.api



import android.util.Log
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
    // OkHttpClient with Cookie Management and Logging Interceptor
    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .cookieJar(JavaNetCookieJar(cookieManager)) // Stores authentication cookies //Handles session cookies automatically
            .addInterceptor { chain ->
                val originalRequest = chain.request()  // Correct reference


                // Retrieve stored cookies before sending request
                val storedCookies = cookieManager.cookieStore.cookies
                Log.d("CookieManager", "Before request, stored cookies: $storedCookies")

                // Add JSESSIONID if available
                val requestBuilder = originalRequest.newBuilder()
                storedCookies.find { it.toString().startsWith("JSESSIONID") }?.let { cookie ->
                    requestBuilder.addHeader("Cookie", cookie.toString())
                    Log.d("RetrofitClient", "Adding JSESSIONID to request: $cookie")
                }

                val newRequest = requestBuilder.build()
                val response = chain.proceed(newRequest)

                // Log cookies after receiving response
                Log.d("CookieManager", "After response, stored cookies: ${cookieManager.cookieStore.cookies}")

                //Retrieve stored cookies
                //val requestBuilder = originalRequest.newBuilder()
                //Retrieved stored session ID from SharedPreferences
                //val sharedPreferences = appContext.getSharedPreferences("user_prefs",Context.MODE_PRIVATE)
                //val sessionId = sharedPreferences.getString("session_id", null)

                // Log cookies before request
                //val storedcookies = cookieManager.cookieStore.cookies
                //Log.d("CookieManager", "Before request, stored cookies: $storedcookies")
                //val response = chain.proceed(request)
                // Log cookies after response
                //Log.d("CookieManager", "After response, stored cookies: ${cookieManager.cookieStore.cookies}")
                //if (!sessionId.isNullOrEmpty()) {
                //    requestBuilder.addHeader("Cookie", "JSESSIONID=$sessionId")
                //    Log.d("RetrofitClient", "Adding JSESSIONID to request: $sessionId")
                //}

                response

                //val newRequest = requestBuilder.build()
                //val response = chain.proceed(newRequest)

                // Save new cookies if response contains a Set-Cookie header
                //val cookies = response.headers("Set-Cookie")
                //for (cookie in cookies) {
                //    if (cookie.startsWith("JSESSIONID")) {
                //        val newSessionId = cookie.split(";")[0].split("=")[1]
                //        sharedPreferences.edit().putString("session_id", newSessionId).apply()
                //        Log.d("RetrofitClient", "Updated Session ID: $newSessionId")
                //        break
                //    }
                //}


                //response
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
    /*
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

     */
}