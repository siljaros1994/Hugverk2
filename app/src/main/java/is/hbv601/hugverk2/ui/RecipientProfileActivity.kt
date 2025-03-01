package `is`.hbv601.hugverk2.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import `is`.hbv601.hbv601.hugverk2.data.api.RetrofitClient
import `is`.hbv601.hugverk2.R
import `is`.hbv601.hugverk2.model.RecipientProfile
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RecipientProfileActivity : AppCompatActivity() {

    // Edit fields
    private lateinit var spinnerEyeColor: Spinner
    private lateinit var spinnerHairColor: Spinner
    private lateinit var spinnerEducationLevel: Spinner
    private lateinit var spinnerRace: Spinner
    private lateinit var spinnerEthnicity: Spinner
    private lateinit var spinnerBloodType: Spinner
    private lateinit var spinnerRecipientType: Spinner
    private lateinit var spinnerMedicalHistory: `is`.hbv601.hugverk2.customviews.MultiSelectSpinner
    private lateinit var editHeight: EditText
    private lateinit var editWeight: EditText
    private lateinit var editAge: EditText
    private lateinit var editGetToKnow: EditText
    private lateinit var buttonSaveEdit: Button
    private lateinit var buttonChooseImage: Button

    // Preview views
    private lateinit var profileImage: ImageView
    private lateinit var textEyeColor: TextView
    private lateinit var textHairColor: TextView
    private lateinit var textEducationLevel: TextView
    private lateinit var textRace: TextView
    private lateinit var textEthnicity: TextView
    private lateinit var textBloodType: TextView
    private lateinit var textRecipientType: TextView
    private lateinit var textMedicalHistory: TextView
    private lateinit var textHeight: TextView
    private lateinit var textWeight: TextView
    private lateinit var textAge: TextView
    private lateinit var textGetToKnow: TextView

    private val PICK_IMAGE_REQUEST = 1
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipient_profile)

        // Bind edit fields
        spinnerEyeColor = findViewById(R.id.spinner_eyeColor)
        spinnerHairColor = findViewById(R.id.spinner_hairColor)
        spinnerEducationLevel = findViewById(R.id.spinner_educationLevel)
        spinnerRace = findViewById(R.id.spinner_race)
        spinnerEthnicity = findViewById(R.id.spinner_ethnicity)
        spinnerBloodType = findViewById(R.id.spinner_bloodType)
        spinnerRecipientType = findViewById(R.id.spinner_recipientType)
        spinnerMedicalHistory = findViewById(R.id.spinner_medicalHistory)
        editHeight = findViewById(R.id.edit_height)
        editWeight = findViewById(R.id.edit_weight)
        editAge = findViewById(R.id.edit_age)
        editGetToKnow = findViewById(R.id.edit_getToKnow)
        buttonSaveEdit = findViewById(R.id.buttonSaveEdit)
        buttonChooseImage = findViewById(R.id.buttonChooseImage)

        // Initialize views
        profileImage = findViewById(R.id.recipient_profile_image)
        textEyeColor = findViewById(R.id.textEyeColor)
        textHairColor = findViewById(R.id.textHairColor)
        textEducationLevel = findViewById(R.id.textEducationLevel)
        textRace = findViewById(R.id.textRace)
        textEthnicity = findViewById(R.id.textEthnicity)
        textBloodType = findViewById(R.id.textBloodType)
        textRecipientType = findViewById(R.id.textRecipientType)
        textMedicalHistory = findViewById(R.id.textMedicalHistory)
        textHeight = findViewById(R.id.textHeight)
        textWeight = findViewById(R.id.textWeight)
        textAge = findViewById(R.id.textAge)
        textGetToKnow = findViewById(R.id.textGetToKnow)

        // Initialize MultiSelectSpinner with medical history options
        spinnerMedicalHistory.setItems(resources.getStringArray(R.array.medical_history_options))

        // Fetch profile data
        val userId = getLoggedInUserId()
        fetchRecipientProfile(userId)

        // Set up save button
        buttonSaveEdit.setOnClickListener {
            val updatedProfile = RecipientProfile(
                eyeColor = spinnerEyeColor.selectedItem.toString(),
                hairColor = spinnerHairColor.selectedItem.toString(),
                educationLevel = spinnerEducationLevel.selectedItem.toString(),
                race = spinnerRace.selectedItem.toString(),
                ethnicity = spinnerEthnicity.selectedItem.toString(),
                bloodType = spinnerBloodType.selectedItem.toString(),
                recipientType = spinnerRecipientType.selectedItem.toString(),
                medicalHistory = spinnerMedicalHistory.getSelectedItems(),
                height = editHeight.text.toString().toDoubleOrNull(),
                weight = editWeight.text.toString().toDoubleOrNull(),
                age = editAge.text.toString().toIntOrNull(),
                getToKnow = editGetToKnow.text.toString(),
                imagePath = imageUri?.toString()
            )
            saveOrEditProfile(updatedProfile)
        }

        buttonChooseImage.setOnClickListener {
            openImageChooser()
        }
    }

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            imageUri = uri
            Glide.with(this).load(uri).into(profileImage)
        }
    }

    private fun openImageChooser() {
        pickImageLauncher.launch("image/*")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            imageUri = data.data
            Glide.with(this).load(imageUri).into(profileImage)
        }
    }

    private fun fetchRecipientProfile(userId: Long) {
        RetrofitClient.getInstance(this).getRecipientProfile(userId).enqueue(object : Callback<RecipientProfile> {
            override fun onResponse(call: Call<RecipientProfile>, response: Response<RecipientProfile>) {
                if (response.isSuccessful) {
                    response.body()?.let { profile ->
                        updateFormFields(profile)
                        updatePreview(profile)
                    } ?: Toast.makeText(this@RecipientProfileActivity, "Empty response", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@RecipientProfileActivity, "Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<RecipientProfile>, t: Throwable) {
                Toast.makeText(this@RecipientProfileActivity, "Network error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateFormFields(profile: RecipientProfile) {
        val eyeColorArray = resources.getStringArray(R.array.eye_color_options)
        val eyeColorIndex = eyeColorArray.indexOf(profile.eyeColor ?: "")
        if (eyeColorIndex >= 0) {
            spinnerEyeColor.setSelection(eyeColorIndex)
        }

        val hairColorArray = resources.getStringArray(R.array.hair_color_options)
        val hairColorIndex = hairColorArray.indexOf(profile.hairColor ?: "")
        if (hairColorIndex >= 0) {
            spinnerHairColor.setSelection(hairColorIndex)
        }

        val educationLevelArray = resources.getStringArray(R.array.education_level_options)
        val educationIndex = educationLevelArray.indexOf(profile.educationLevel ?: "")
        if (educationIndex >= 0) {
            spinnerEducationLevel.setSelection(educationIndex)
        }

        val raceArray = resources.getStringArray(R.array.race_options)
        val raceIndex = raceArray.indexOf(profile.race ?: "")
        if (raceIndex >= 0) {
            spinnerRace.setSelection(raceIndex)
        }

        val ethnicityArray = resources.getStringArray(R.array.ethnicity_options)
        val ethnicityIndex = ethnicityArray.indexOf(profile.ethnicity ?: "")
        if (ethnicityIndex >= 0) {
            spinnerEthnicity.setSelection(ethnicityIndex)
        }

        val bloodTypeArray = resources.getStringArray(R.array.blood_type_options)
        val bloodTypeIndex = bloodTypeArray.indexOf(profile.bloodType ?: "")
        if (bloodTypeIndex >= 0) {
            spinnerBloodType.setSelection(bloodTypeIndex)
        }

        val recipientTypeArray = resources.getStringArray(R.array.recipient_type_options)
        val recipientTypeIndex = recipientTypeArray.indexOf(profile.recipientType ?: "")
        if (recipientTypeIndex >= 0) {
            spinnerRecipientType.setSelection(recipientTypeIndex)
        }

        profile.medicalHistory?.let { history ->
            spinnerMedicalHistory.setSelection(history.toList())
        }

        editHeight.setText(profile.height?.toString() ?: "")
        editWeight.setText(profile.weight?.toString() ?: "")
        editAge.setText(profile.age?.toString() ?: "")
        editGetToKnow.setText(profile.getToKnow ?: "")
    }

    private fun updatePreview(profile: RecipientProfile) {
        // Here we update preview TextViews with fetched profile data
        textEyeColor.text = "Eye Color: ${profile.eyeColor ?: "Not specified"}"
        textHairColor.text = "Hair Color: ${profile.hairColor ?: "Not specified"}"
        textEducationLevel.text = "Education Level: ${profile.educationLevel ?: "Not specified"}"
        textRace.text = "Race: ${profile.race ?: "Not specified"}"
        textEthnicity.text = "Ethnicity: ${profile.ethnicity ?: "Not specified"}"
        textBloodType.text = "Blood Type: ${profile.bloodType ?: "Not specified"}"
        textRecipientType.text = "Recipient Type: ${profile.recipientType ?: "Not specified"}"
        textMedicalHistory.text = "Medical History: ${profile.medicalHistory?.joinToString(", ") ?: "Not specified"}"
        textHeight.text = "Height: ${profile.height ?: "Not specified"}"
        textWeight.text = "Weight: ${profile.weight ?: "Not specified"}"
        textAge.text = "Age: ${profile.age ?: "Not specified"}"
        textGetToKnow.text = "Get to Know: ${profile.getToKnow ?: "Not specified"}"

        // Load image if available
        profile.imagePath?.let { path ->
            Glide.with(this)
                .load("http://10.0.2.2:8080$path")
                .placeholder(R.drawable.default_avatar)
                .error(R.drawable.default_avatar)
                .into(profileImage)
        }
    }

    private fun saveOrEditProfile(profile: RecipientProfile) {
        // Calls the API to save or update the profile
        RetrofitClient.getInstance(this).saveOrEditRecipientProfile(profile)
            .enqueue(object : Callback<RecipientProfile> {
                override fun onResponse(call: Call<RecipientProfile>, response: Response<RecipientProfile>) {
                    if (response.isSuccessful) {
                        response.body()?.let { updatedProfile ->
                            updatePreview(updatedProfile)
                            Toast.makeText(this@RecipientProfileActivity, "Profile saved", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@RecipientProfileActivity, "Error saving profile: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onFailure(call: Call<RecipientProfile>, t: Throwable) {
                    Toast.makeText(this@RecipientProfileActivity, "Network error", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun getLoggedInUserId(): Long {
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        return sharedPreferences.getLong("user_id", -1)
    }
}