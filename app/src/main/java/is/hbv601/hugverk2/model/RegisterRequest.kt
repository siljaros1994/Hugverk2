package `is`.hbv601.hugverk2.model

data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String,
    val confirmPassword: String,
    val userType: String
)