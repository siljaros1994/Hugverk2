package `is`.hbv601.hugverk2.data.api

import `is`.hbv601.hugverk2.data.model.RegisterRequest
import `is`.hbv601.hugverk2.data.model.RegisterResponse
import `is`.hbv601.hugverk2.model.LoginRequest
import `is`.hbv601.hugverk2.model.LoginResponse
import `is`.hbv601.hugverk2.model.PaginatedResponse
import `is`.hbv601.hugverk2.model.Donor
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @POST("api/login")
    fun login(@Body loginRequest: LoginRequest): Call<LoginResponse>

    @POST("api/register")
    fun register(@Body registerRequest: RegisterRequest): Call<RegisterResponse>

    // we will add other endpoints here like search, match, message, etc.
    @GET("home/recipient")
    fun getDonors(
        @Query("page") page: Int = 0,
        @Query("keyword") keyword: String = "",
        @Query("sortOrder") sortOrder: String = "asc",
        @Query("sortBy") sortBy: String = "age"
    ): Call<PaginatedResponse<Donor>>
}

