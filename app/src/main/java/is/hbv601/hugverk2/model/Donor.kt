package `is`.hbv601.hugverk2.model

data class Donor (
    val donorProfileId: Long,
    val donorType: String?,
    val height: Double?,
    val weight: Double?,
    val age: Int?,
    val eyeColor: String?,
    val hairColor: String?,
    val educationLevel: String?,
    val medicalHistory: List<String>?,
    val race: String?,
    val ethnicity: String?,
    val bloodType: String?,
    val getToKnow: String?,
    val traits: String?,
    val imagePath: String?,
    val donationLimit: Int,
    val donationsCompleted: Int
)