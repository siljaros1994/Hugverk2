package `is`.hbv601.hugverk2.model

data class DonorProfile(
    val donorProfileId: Long? = null, // nullable in the case it is a new profile
    val donorType: String? = null,
    val height: Double? = null,
    val weight: Double? = null,
    val age: Int? = null,
    val eyeColor: String? = null,
    val hairColor: String? = null,
    val educationLevel: String? = null,
    val medicalHistory: List<String>? = null,
    val race: String? = null,
    val ethnicity: String? = null,
    val bloodType: String? = null,
    val getToKnow: String? = null,
    val traits: String? = null,
    val imagePath: String? = null,
    val donationLimit: Int? = 5,
    val donationsCompleted: Int? = 0,
    val user: MyAppUser? = null
)