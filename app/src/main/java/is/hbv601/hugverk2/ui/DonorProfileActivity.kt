package `is`.hbv601.hugverk2.ui

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import `is`.hbv601.hbv601.hugverk2.data.api.RetrofitClient
import `is`.hbv601.hugverk2.R
import `is`.hbv601.hugverk2.model.DonorProfile
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DonorProfileActivity : AppCompatActivity() {

    // Edit fields
    private lateinit var editEyeColor: EditText
    private lateinit var editHairColor: EditText
    private lateinit var editEducationLevel: EditText
    private lateinit var editRace: EditText
    private lateinit var editEthnicity: EditText
    private lateinit var editBloodType: EditText
    private lateinit var editMedicalHistory: EditText
    private lateinit var editHeight: EditText
    private lateinit var editWeight: EditText
    private lateinit var editAge: EditText
    private lateinit var editGetToKnow: EditText
    private lateinit var buttonSaveEdit: Button

    // Preview views
    private lateinit var profileImage: ImageView
    private lateinit var donorType: TextView
    private lateinit var textEyeColor: TextView
    private lateinit var textHairColor: TextView
    private lateinit var textEducationLevel: TextView
    private lateinit var textRace: TextView
    private lateinit var textEthnicity: TextView
    private lateinit var textBloodType: TextView
    private lateinit var textMedicalHistory: TextView
    private lateinit var textHeight: TextView
    private lateinit var textWeight: TextView
    private lateinit var textAge: TextView
    private lateinit var textGetToKnow: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_donor_profile)

        // Bind edit fields
        editEyeColor = findViewById(R.id.edit_eyeColor)
        editHairColor = findViewById(R.id.edit_hairColor)
        editEducationLevel = findViewById(R.id.edit_educationLevel)
        editRace = findViewById(R.id.edit_race)
        editEthnicity = findViewById(R.id.edit_ethnicity)
        editBloodType = findViewById(R.id.edit_bloodType)
        editMedicalHistory = findViewById(R.id.edit_medicalHistory)
        editHeight = findViewById(R.id.edit_height)
        editWeight = findViewById(R.id.edit_weight)
        editAge = findViewById(R.id.edit_age)
        editGetToKnow = findViewById(R.id.edit_getToKnow)
        buttonSaveEdit = findViewById(R.id.buttonSaveEdit)

        // Initialize views
        profileImage = findViewById(R.id.donor_profile_image)
        donorType = findViewById(R.id.donorType)
        textEyeColor = findViewById(R.id.textEyeColor)
        textHairColor = findViewById(R.id.textHairColor)
        textEducationLevel = findViewById(R.id.textEducationLevel)
        textRace = findViewById(R.id.textRace)
        textEthnicity = findViewById(R.id.textEthnicity)
        textBloodType = findViewById(R.id.textBloodType)
        textMedicalHistory = findViewById(R.id.textMedicalHistory)
        textHeight = findViewById(R.id.textHeight)
        textWeight = findViewById(R.id.textWeight)
        textAge = findViewById(R.id.textAge)
        textGetToKnow = findViewById(R.id.textGetToKnow)

        // Fetch the donor profile
        val userId = getLoggedInUserId()
        fetchDonorProfile(userId)

        // Set up save button
        buttonSaveEdit.setOnClickListener {
            val updatedProfile = DonorProfile(
                eyeColor = editEyeColor.text.toString(),
                hairColor = editHairColor.text.toString(),
                educationLevel = editEducationLevel.text.toString(),
                race = editRace.text.toString(),
                ethnicity = editEthnicity.text.toString(),
                bloodType = editBloodType.text.toString(),
                medicalHistory = editMedicalHistory.text.toString().split(",").map { it.trim() },
                height = editHeight.text.toString().toDoubleOrNull(),
                weight = editWeight.text.toString().toDoubleOrNull(),
                age = editAge.text.toString().toIntOrNull(),
                getToKnow = editGetToKnow.text.toString(),
                imagePath = null // Handle image upload
            )
            saveOrEditProfile(updatedProfile)
        }
    }

    private fun fetchDonorProfile(userId: Long) {
        val call = RetrofitClient.getInstance(this).getDonorProfile(userId) // Use getInstance(this)
        call.enqueue(object : Callback<DonorProfile> {
            override fun onResponse(call: Call<DonorProfile>, response: Response<DonorProfile>) {
                if (response.isSuccessful) {
                    response.body()?.let { profile ->
                        updateFormFields(profile)
                        updatePreview(profile)
                    } ?: Toast.makeText(this@DonorProfileActivity, "Empty response", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@DonorProfileActivity, "Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<DonorProfile>, t: Throwable) {
                Toast.makeText(this@DonorProfileActivity, "Network error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateFormFields(profile: DonorProfile) {
        // Pre-fill edit fields with existing profile data
        editEyeColor.setText(profile.eyeColor ?: "")
        editHairColor.setText(profile.hairColor ?: "")
        editEducationLevel.setText(profile.educationLevel ?: "")
        editRace.setText(profile.race ?: "")
        editEthnicity.setText(profile.ethnicity ?: "")
        editBloodType.setText(profile.bloodType ?: "")
        editMedicalHistory.setText(profile.medicalHistory?.joinToString(", ") ?: "")
        editHeight.setText(profile.height?.toString() ?: "")
        editWeight.setText(profile.weight?.toString() ?: "")
        editAge.setText(profile.age?.toString() ?: "")
        editGetToKnow.setText(profile.getToKnow ?: "")
    }

    private fun updatePreview(profile: DonorProfile) {
        // Update preview TextViews with fetched profile data
        textEyeColor.text = "Eye Color: ${profile.eyeColor ?: "Not specified"}"
        textHairColor.text = "Hair Color: ${profile.hairColor ?: "Not specified"}"
        textEducationLevel.text = "Education Level: ${profile.educationLevel ?: "Not specified"}"
        textRace.text = "Race: ${profile.race ?: "Not specified"}"
        textEthnicity.text = "Ethnicity: ${profile.ethnicity ?: "Not specified"}"
        textBloodType.text = "Blood Type: ${profile.bloodType ?: "Not specified"}"
        textMedicalHistory.text = "Medical History: ${profile.medicalHistory?.joinToString(", ") ?: "Not specified"}"
        textHeight.text = "Height: ${profile.height ?: "Not specified"}"
        textWeight.text = "Weight: ${profile.weight ?: "Not specified"}"
        textAge.text = "Age: ${profile.age ?: "Not specified"}"
        textGetToKnow.text = "Get to Know: ${profile.getToKnow ?: "Not specified"}"

        // Load profile image
        val imagePath = profile.imagePath
        if (!imagePath.isNullOrEmpty()) {
            Glide.with(this)
                .load("http://10.0.2.2:8080$imagePath")
                .placeholder(R.drawable.default_avatar)
                .error(R.drawable.default_avatar)
                .into(profileImage)
        }
    }

    private fun saveOrEditProfile(profile: DonorProfile) {
        // Calls the API to save or update the profile
        RetrofitClient.getInstance(this).saveOrEditDonorProfile(profile)
            .enqueue(object : Callback<DonorProfile> {
                override fun onResponse(call: Call<DonorProfile>, response: Response<DonorProfile>) {
                    if (response.isSuccessful) {
                        response.body()?.let { updatedProfile ->
                            updatePreview(updatedProfile)
                            Toast.makeText(this@DonorProfileActivity, "Profile saved", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@DonorProfileActivity, "Error saving profile: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onFailure(call: Call<DonorProfile>, t: Throwable) {
                    Toast.makeText(this@DonorProfileActivity, "Network error", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun getLoggedInUserId(): Long {
        // Retrieve user ID from SharedPreferences or intent
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        return sharedPreferences.getLong("user_id", -1)
    }
}