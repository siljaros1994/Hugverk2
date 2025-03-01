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

    //Login and register:
    @POST("api/users/login")
    fun login(@Body loginRequest: LoginRequest): Call<LoginResponse>

    @POST("api/users/register")
    fun register(@Body registerRequest: RegisterRequest): Call<RegisterResponse>

    //donor profile:
    @GET("api/donor/profile/{userId}")
    fun getDonorProfile(@Path("userId") userId: Long): Call<DonorProfile>

    @POST("api/donor/profile/saveOrEdit")
    fun saveOrEditDonorProfile(@Body profile: DonorProfile): Call<DonorProfile>

    // Recipient profile:
    @GET("api/recipient/profile/{userId}")
    fun getRecipientProfile(@Path("userId") userId: Long): Call<RecipientProfile>

    @POST("api/recipient/profile/saveOrEdit")
    fun saveOrEditRecipientProfile(@Body profile: RecipientProfile): Call<RecipientProfile>

    // Here we add our other endpoints here like search, match, message,...

}