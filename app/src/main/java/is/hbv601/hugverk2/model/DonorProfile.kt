package `is`.hbv601.hugverk2.model

data class DonorProfile(
    var donorProfileId: Long? = null, // nullable in the case it is a new profile
    var donorType: String? = null,
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
    var donationLimit: Int? = 5,
    var donationsCompleted: Int? = 0,
    var user: MyAppUser? = null
)