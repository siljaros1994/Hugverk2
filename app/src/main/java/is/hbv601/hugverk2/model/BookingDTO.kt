package `is`.hbv601.hugverk2.models

//import java.time.LocalDate
//import java.time.LocalTime

//Matches the backend version BookingDTO.java

data class BookingDTO (
    val id: Long?,
    val donorId: Long,
    val recipientId: Long,
    val date: String,  // Use String instead of LocalDate for JSON parsing
    val time: String,  // Use String instead of LocalTime for JSON parsing
    val confirmed: Boolean,
    val status: String

)

