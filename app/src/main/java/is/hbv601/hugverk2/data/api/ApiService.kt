package `is`.hbv601.hugverk2.data.api

import `is`.hbv601.hugverk2.data.model.RegisterRequest
import `is`.hbv601.hugverk2.data.model.RegisterResponse
import `is`.hbv601.hugverk2.model.LoginRequest
import `is`.hbv601.hugverk2.model.LoginResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("api/authenticate")
    fun login(@Body loginRequest: LoginRequest): Call<LoginResponse>

    @POST("users/register")
    fun register(@Body registerRequest: RegisterRequest): Call<RegisterResponse>

    // we will add other endpoints here like search, match, message, etc.
}