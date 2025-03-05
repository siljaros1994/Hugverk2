package `is`.hbv601.hugverk2.ui

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import com.bumptech.glide.Glide
import `is`.hbv601.hbv601.hugverk2.data.api.RetrofitClient
import `is`.hbv601.hugverk2.R
import `is`.hbv601.hugverk2.model.RecipientProfile
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import `is`.hbv601.hugverk2.customviews.MultiSelectSpinner
import `is`.hbv601.hugverk2.model.UploadResponse

class RecipientProfileActivity : AppCompatActivity() {

    // Edit fields
    private lateinit var spinnerEyeColor: Spinner
    private lateinit var spinnerHairColor: Spinner
    private lateinit var spinnerEducationLevel: Spinner
    private lateinit var spinnerRace: Spinner
    private lateinit var spinnerEthnicity: Spinner
    private lateinit var spinnerBloodType: Spinner
    private lateinit var spinnerRecipientType: Spinner
    private lateinit var spinnerMedicalHistory: MultiSelectSpinner
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

    private var imageUri: Uri? = null

    // Use ActivityResultLauncher for picking an image.
    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            imageUri = uri
            Log.d("RecipientProfile", "Image URI selected: $uri")
            Glide.with(this).load(uri).into(profileImage)
        }
    }

    // Helper method: convert a Uri to a File.
    private fun getFileFromUri(uri: Uri): File? {
        return try {
            val inputStream = contentResolver.openInputStream(uri) ?: return null
            val tempFile = File.createTempFile("upload_", ".jpg", cacheDir)
            tempFile.outputStream().use { output ->
                inputStream.copyTo(output)
            }
            Log.d("RecipientProfile", "File created from URI: ${tempFile.absolutePath}")
            tempFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun openImageChooser() {
        pickImageLauncher.launch("image/*")
    }

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

        // Bind preview views
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

        // Initialize MultiSelectSpinner options
        spinnerMedicalHistory.setItems(resources.getStringArray(R.array.medical_history_options))

        // Fetch recipient profile data
        val userId = getLoggedInUserId()
        fetchRecipientProfile(userId)

        // Set up image chooser button
        buttonChooseImage.setOnClickListener {
            openImageChooser()
        }

        // Set up save button with image upload support
        buttonSaveEdit.setOnClickListener {
            if (imageUri != null) {
                val file = getFileFromUri(imageUri!!)
                if (file != null) {
                    val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                    val multipartBody = MultipartBody.Part.createFormData("file", file.name, requestFile)
                    RetrofitClient.getInstance(this)
                        .uploadFile(multipartBody)
                        .enqueue(object : Callback<UploadResponse> {
                            override fun onResponse(call: Call<UploadResponse>, response: Response<UploadResponse>) {
                                Log.d("RecipientProfile", "Upload response code: ${response.code()}")
                                if (response.isSuccessful) {
                                    response.body()?.let { uploadResponse ->
                                        Log.d("RecipientProfile", "Uploaded path: ${uploadResponse.fileUrl}")
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
                                            imagePath = uploadResponse.fileUrl
                                        )
                                        saveOrEditProfile(updatedProfile)
                                    } ?: onFailure(call, Throwable("Empty response body"))
                                } else {
                                    onFailure(call, Throwable("Error uploading file"))
                                }
                            }
                            override fun onFailure(call: Call<UploadResponse>, t: Throwable) {
                                Log.e("RecipientProfile", "Upload Failure: ${t.message}")
                                Toast.makeText(this@RecipientProfileActivity, "Image upload failed", Toast.LENGTH_SHORT).show()
                            }
                        })
                } else {
                    Toast.makeText(this, "Unable to access image file", Toast.LENGTH_SHORT).show()
                }
            } else {
                // No new image selected; save profile with current values.
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
                    imagePath = null
                )
                saveOrEditProfile(updatedProfile)
            }
        }
    }

    private fun fetchRecipientProfile(userId: Long) {
        RetrofitClient.getInstance(this).getRecipientProfile(userId)
            .enqueue(object : Callback<RecipientProfile> {
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
        if (eyeColorIndex >= 0) spinnerEyeColor.setSelection(eyeColorIndex)

        val hairColorArray = resources.getStringArray(R.array.hair_color_options)
        val hairColorIndex = hairColorArray.indexOf(profile.hairColor ?: "")
        if (hairColorIndex >= 0) spinnerHairColor.setSelection(hairColorIndex)

        val educationLevelArray = resources.getStringArray(R.array.education_level_options)
        val educationIndex = educationLevelArray.indexOf(profile.educationLevel ?: "")
        if (educationIndex >= 0) spinnerEducationLevel.setSelection(educationIndex)

        val raceArray = resources.getStringArray(R.array.race_options)
        val raceIndex = raceArray.indexOf(profile.race ?: "")
        if (raceIndex >= 0) spinnerRace.setSelection(raceIndex)

        val ethnicityArray = resources.getStringArray(R.array.ethnicity_options)
        val ethnicityIndex = ethnicityArray.indexOf(profile.ethnicity ?: "")
        if (ethnicityIndex >= 0) spinnerEthnicity.setSelection(ethnicityIndex)

        val bloodTypeArray = resources.getStringArray(R.array.blood_type_options)
        val bloodTypeIndex = bloodTypeArray.indexOf(profile.bloodType ?: "")
        if (bloodTypeIndex >= 0) spinnerBloodType.setSelection(bloodTypeIndex)

        val recipientTypeArray = resources.getStringArray(R.array.recipient_type_options)
        val recipientTypeIndex = recipientTypeArray.indexOf(profile.recipientType ?: "")
        if (recipientTypeIndex >= 0) spinnerRecipientType.setSelection(recipientTypeIndex)

        profile.medicalHistory?.let { history ->
            spinnerMedicalHistory.setSelection(history.toList())
        }

        editHeight.setText(profile.height?.toString() ?: "")
        editWeight.setText(profile.weight?.toString() ?: "")
        editAge.setText(profile.age?.toString() ?: "")
        editGetToKnow.setText(profile.getToKnow ?: "")
    }

    private fun updatePreview(profile: RecipientProfile) {
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

        profile.imagePath?.trim()?.let { path ->
            val imageUrl = if (path.startsWith("http")) {
                path
            } else {
                // Here we use the base URL only if the path is relative.
                val baseUrl = "http://192.168.101.4:8080"
                val formattedPath = if (path.startsWith("/")) path else "/$path"
                baseUrl + formattedPath
            }
            Log.d("RecipientProfile", "Loading image from: $imageUrl")
            Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.default_avatar)
                .error(R.drawable.default_avatar)
                .into(profileImage)
        }
    }

    private fun saveOrEditProfile(profile: RecipientProfile) {
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
