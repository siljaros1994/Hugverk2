package `is`.hbv601.hugverk2.model

data class RecipientProfile(
    var recipientProfileId: Long? = null,
    var recipientType: String? = null,
    var height: Double? = null,
    var weight: Double? = null,
    var age: Int? = null,
    var eyeColor: String? = null,
    var hairColor: String? = null,
    var educationLevel: String? = null,
    var medicalHistory: List<String>? = null,
    var race: String? = null,
    var ethnicity: String? = null,
    var bloodType: String? = null,
    var getToKnow: String? = null,
    var traits: String? = null,
    var imagePath: String? = null,
    var userId: Long? = null,
    var user: MyAppUser? = null
)