package `is`.hbv601.hugverk2.data.model

data class RegisterResponse(
    val message: String,
    val userId: Long,
    val userType: String
)