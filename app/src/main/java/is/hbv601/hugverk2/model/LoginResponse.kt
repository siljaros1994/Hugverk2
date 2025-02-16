package `is`.hbv601.hugverk2.model

data class LoginResponse(
    val message: String,
    val userId: Long,
    val userType: String
)