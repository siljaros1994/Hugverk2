package `is`.hbv601.hugverk2.model

data class RecipientProfile(
    val recipientProfileId: Long? = null,
    val recipientType: String? = null,
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
    val user: MyAppUser? = null
)