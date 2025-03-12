package `is`.hbv601.hugverk2.model

//import android.media.session.MediaSession.Token

data class LoginResponse(
    val message: String,
    val userId: Long,
    val userType: String,
    val username: String,
    //Token field for favorites exists
    //val token: String?
)
