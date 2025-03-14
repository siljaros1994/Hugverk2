package `is`.hbv601.hugverk2.data.api

import `is`.hbv601.hugverk2.model.RegisterRequest
import `is`.hbv601.hugverk2.model.RegisterResponse
import `is`.hbv601.hugverk2.model.LoginRequest
import `is`.hbv601.hugverk2.model.LoginResponse
import `is`.hbv601.hugverk2.model.DonorProfile
import `is`.hbv601.hugverk2.model.LogoutRequest
import `is`.hbv601.hugverk2.model.LogoutResponse
import `is`.hbv601.hugverk2.model.RecipientProfile
import `is`.hbv601.hugverk2.model.UploadResponse
import `is`.hbv601.hugverk2.model.UserDTO
import `is`.hbv601.hugverk2.model.MessageDTO
import `is`.hbv601.hugverk2.model.MessageForm
import `is`.hbv601.hugverk2.model.DeleteResponseDTO
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Header

interface ApiService {

    //Login and register:
    @POST("api/users/login")
    fun login(@Body loginRequest: LoginRequest): Call<LoginResponse>

    @POST("api/users/register")
    fun register(@Body registerRequest: RegisterRequest): Call<RegisterResponse>

    //Logout
    @POST("api/users/logout")
    fun logout(): Call<Void>

    //donor profile:
    @GET("api/donor/profile/{userId}")
    fun getDonorProfile(@Path("userId") userId: Long): Call<DonorProfile>

    @Headers("Content-Type: application/json")
    @POST("api/donor/profile/saveOrEdit")
    fun saveOrEditDonorProfile(@Body profile: DonorProfile): Call<DonorProfile>

    //donorpage view profile:
    @GET("api/donor/view/{donorProfileId}")
    fun viewDonorProfile(@Path("donorProfileId") donorProfileId: Long): Call<DonorProfile>

    @GET("api/donor/all")
    fun getDonors(@Query("page") page: Int, @Query("size") size: Int, @Query("location") location: String? = null): Call<List<DonorProfile>>

    // Recipient profile:
    @GET("api/recipient/profile/{userId}")
    fun getRecipientProfile(@Path("userId") userId: Long): Call<RecipientProfile>

    @POST("api/recipient/profile/saveOrEdit")
    fun saveOrEditRecipientProfile(@Body profile: RecipientProfile): Call<RecipientProfile>

    @Multipart
    @POST("api/upload")
    fun uploadFile(@Part file: MultipartBody.Part): Call<UploadResponse>

    @GET("api/users/all")
    fun getAllUsers(): Call<List<UserDTO>>

    // Here we get favorite.
    @GET("api/recipient/favorites")
    fun getFavoriteDonors(): Call<List<DonorProfile>>

    @GET("api/recipient/favoritedByDonor/{donorId}")
    fun getRecipientsWhoFavoritedDonor(@Path("donorId") donorId: Long): Call<List<RecipientProfile>>

    @GET("api/recipient/favorite/{donorProfileId}")
    fun addFavoriteDonor(@Path("donorProfileId") donorProfileId: Long): Call<Void>

    @POST("api/recipient/unfavorite/{donorProfileId}")
    fun unfavoriteDonor(@Path("donorProfileId") donorProfileId: Long): Call<Void>

    // Here we get match users
    @GET("api/match/recipient/matches")
    fun getRecipientMatches(): Call<List<DonorProfile>>

    @GET("api/match/donor/matches")
    fun getDonorMatches(): Call<List<RecipientProfile>>

    @GET("delete/{username}")
    fun deleteUser(
        @Path("username") username: String,
        @Header("Cookie") cookie: String
    ): Call<DeleteResponseDTO>
  
    @GET("api/messages/{userType}/{id}")
    fun getMessages(
        @Path("userType") userType: String,
        @Path("id") userId: Long
    ): Call<List<MessageDTO>>

    @Headers("Content-Type: application/json")
    @POST("api/messages/send")
    fun sendMessage(@Body messageForm: MessageForm): Call<Void>

    // Here we add our other endpoints here like search, match, message,...

}