package `is`.hbv601.hugverk2.data.api

import `is`.hbv601.hugverk2.data.model.RegisterRequest
import `is`.hbv601.hugverk2.data.model.RegisterResponse
import `is`.hbv601.hugverk2.model.LoginRequest
import `is`.hbv601.hugverk2.model.LoginResponse
import `is`.hbv601.hugverk2.model.DonorProfile
import `is`.hbv601.hugverk2.model.RecipientProfile
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @POST("api/users/login") // Update to match the actual login endpoint
    fun login(@Body loginRequest: LoginRequest): Call<LoginResponse>

    @POST("api/users/register") // Update to match the actual register endpoint
    fun register(@Body registerRequest: RegisterRequest): Call<RegisterResponse>

    @GET("api/donor/profile/{userId}") // Update to match the actual donor profile endpoint
    fun getDonorProfile(@Path("userId") userId: Long): Call<DonorProfile>

    @GET("api/recipient/profile/{userId}") // Update to match the actual recipient profile endpoint
    fun getRecipientProfile(@Path("userId") userId: Long): Call<RecipientProfile>

    // Add other endpoints here like search, match, message,...

}