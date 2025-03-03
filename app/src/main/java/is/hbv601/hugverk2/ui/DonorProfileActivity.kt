package `is`.hbv601.hugverk2.ui

import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import `is`.hbv601.hugverk2.model.DonorProfile
import `is`.hbv601.hugverk2.R
import `is`.hbv601.hbv601.hugverk2.data.api.RetrofitClient
import `is`.hbv601.hugverk2.customviews.MultiSelectSpinner
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class DonorProfileActivity : AppCompatActivity() {

    // Edit fields
    private lateinit var spinnerEyeColor: Spinner
    private lateinit var spinnerHairColor: Spinner
    private lateinit var spinnerEducationLevel: Spinner
    private lateinit var spinnerRace: Spinner
    private lateinit var spinnerEthnicity: Spinner
    private lateinit var spinnerBloodType: Spinner
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
    private lateinit var textMedicalHistory: TextView
    private lateinit var textHeight: TextView
    private lateinit var textWeight: TextView
    private lateinit var textAge: TextView
    private lateinit var textGetToKnow: TextView

    private val PICK_IMAGE_REQUEST = 1
    private var imageUri: Uri? = null
    private var currentProfile: DonorProfile? = null

    // Register an ActivityResultLauncher for picking images.
    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            imageUri = uri
            Glide.with(this).load(uri).into(profileImage)
        }
    }

    private fun getFileFromUri(uri: Uri): File? {
        return try {
            val inputStream = contentResolver.openInputStream(uri) ?: return null
            val tempFile = File.createTempFile("upload_", ".jpg", cacheDir)
            tempFile.outputStream().use { output ->
                inputStream.copyTo(output)
            }
            tempFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_donor_profile)

        // Bind edit fields
        spinnerEyeColor = findViewById(R.id.spinner_eyeColor)
        spinnerHairColor = findViewById(R.id.spinner_hairColor)
        spinnerEducationLevel = findViewById(R.id.spinner_educationLevel)
        spinnerRace = findViewById(R.id.spinner_race)
        spinnerEthnicity = findViewById(R.id.spinner_ethnicity)
        spinnerBloodType = findViewById(R.id.spinner_bloodType)
        spinnerMedicalHistory = findViewById(R.id.spinner_medicalHistory)
        editHeight = findViewById(R.id.edit_height)
        editWeight = findViewById(R.id.edit_weight)
        editAge = findViewById(R.id.edit_age)
        editGetToKnow = findViewById(R.id.edit_getToKnow)
        buttonSaveEdit = findViewById(R.id.buttonSaveEdit)
        buttonChooseImage = findViewById(R.id.buttonChooseImage)

        // Initialize preview views
        profileImage = findViewById(R.id.donor_profile_image)
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

        // Initialize MultiSelectSpinner with options
        spinnerMedicalHistory.setItems(resources.getStringArray(R.array.medical_history_options))

        // Fetch the donor profile
        val userId = getLoggedInUserId()
        fetchDonorProfile(userId)

        // Set up choose image button
        buttonChooseImage.setOnClickListener {
            openImageChooser()
        }


        // Set up save button with image upload support.
        buttonSaveEdit.setOnClickListener {
            if (imageUri != null) {
                // Instead of getRealPathFromUri, try getFileFromUri to obtain a File
                val file = getFileFromUri(imageUri!!)
                if (file != null) {
                    val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                    val multipartBody = MultipartBody.Part.createFormData("file", file.name, requestFile)

                    RetrofitClient.getInstance(this)
                        .uploadFile(multipartBody)
                        .enqueue(object : Callback<String> {
                            override fun onResponse(call: Call<String>, response: Response<String>) {
                                if (response.isSuccessful) {
                                    response.body()?.let { uploadedPath ->
                                        Log.d("DonorProfile", "Uploaded path: $uploadedPath")
                                        // Build your updated profile with the uploadedPath (server-relative)
                                        val updatedProfile = DonorProfile(
                                            eyeColor = spinnerEyeColor.selectedItem.toString(),
                                            hairColor = spinnerHairColor.selectedItem.toString(),
                                            educationLevel = spinnerEducationLevel.selectedItem.toString(),
                                            race = spinnerRace.selectedItem.toString(),
                                            ethnicity = spinnerEthnicity.selectedItem.toString(),
                                            bloodType = spinnerBloodType.selectedItem.toString(),
                                            medicalHistory = spinnerMedicalHistory.getSelectedItems(),
                                            height = editHeight.text.toString().toDoubleOrNull(),
                                            weight = editWeight.text.toString().toDoubleOrNull(),
                                            age = editAge.text.toString().toIntOrNull(),
                                            getToKnow = editGetToKnow.text.toString(),
                                            imagePath = uploadedPath // use the returned relative path here!
                                        )
                                        saveOrEditProfile(updatedProfile)
                                    } ?: onFailure(call, Throwable("Empty response body"))
                                } else {
                                    Log.e("Upload Error", "Error Code: ${response.code()}")
                                    onFailure(call, Throwable("Error uploading file"))
                                }
                            }
                            override fun onFailure(call: Call<String>, t: Throwable) {
                                Log.e("Upload Failure", t.message ?: "Unknown error")
                                Toast.makeText(this@DonorProfileActivity, "Image upload failed", Toast.LENGTH_SHORT).show()
                            }
                        })
                } else {
                    Toast.makeText(this, "Unable to access image file", Toast.LENGTH_SHORT).show()
                }
            } else {
                // Save profile without updating the imagePath
                val updatedProfile = DonorProfile(
                    eyeColor = spinnerEyeColor.selectedItem.toString(),
                    hairColor = spinnerHairColor.selectedItem.toString(),
                    educationLevel = spinnerEducationLevel.selectedItem.toString(),
                    race = spinnerRace.selectedItem.toString(),
                    ethnicity = spinnerEthnicity.selectedItem.toString(),
                    bloodType = spinnerBloodType.selectedItem.toString(),
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

    private fun openImageChooser() {
        pickImageLauncher.launch("image/*")
    }

    // Helper to get a real file system path from a content Uri.
    private fun getRealPathFromUri(uri: Uri): String {
        var realPath = ""
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = contentResolver.query(uri, projection, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                realPath = it.getString(columnIndex)
            }
        }
        return realPath
    }

    // Here we have a function to upload the image file to the backend.
    private fun uploadImage(uri: Uri, onSuccess: (String) -> Unit, onFailure: () -> Unit) {
        val realPath = getRealPathFromUri(uri)
        if (realPath.isEmpty()) {
            onFailure()
            return
        }
        val file = File(realPath)
        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
        val multipartBody = MultipartBody.Part.createFormData("file", file.name, requestFile)

        RetrofitClient.getInstance(this)
            .uploadFile(multipartBody)
            .enqueue(object : Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    if (response.isSuccessful) {
                        response.body()?.let { filePath ->
                            onSuccess(filePath)
                        } ?: onFailure()
                    } else {
                        Log.e("Upload Error", "Error Code: ${response.code()}")
                        onFailure()
                    }
                }
                override fun onFailure(call: Call<String>, t: Throwable) {
                    Log.e("Upload Failure", t.message ?: "Unknown error")
                    onFailure()
                }
            })
    }

    private fun fetchDonorProfile(userId: Long) {
        RetrofitClient.getInstance(this).getDonorProfile(userId)
            .enqueue(object : Callback<DonorProfile> {
                override fun onResponse(call: Call<DonorProfile>, response: Response<DonorProfile>) {
                    if (response.isSuccessful) {
                        response.body()?.let { profile ->
                            currentProfile = profile // save the current profile
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
        val eyeColorArray = resources.getStringArray(R.array.eye_color_options)
        val eyeColorIndex = eyeColorArray.indexOf(profile.eyeColor ?: "")
        if (eyeColorIndex >= 0) spinnerEyeColor.setSelection(eyeColorIndex)

        val hairColorArray = resources.getStringArray(R.array.hair_color_options)
        val hairColorIndex = hairColorArray.indexOf(profile.hairColor ?: "")
        if (hairColorIndex >= 0) spinnerHairColor.setSelection(hairColorIndex)

        val educationArray = resources.getStringArray(R.array.education_level_options)
        val educationIndex = educationArray.indexOf(profile.educationLevel ?: "")
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

        profile.medicalHistory?.let { history ->
            spinnerMedicalHistory.setSelection(history.toList())
        }

        editHeight.setText(profile.height?.toString() ?: "")
        editWeight.setText(profile.weight?.toString() ?: "")
        editAge.setText(profile.age?.toString() ?: "")
        editGetToKnow.setText(profile.getToKnow ?: "")
    }

    private fun updatePreview(profile: DonorProfile) {
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

        profile.imagePath?.trim()?.let { path ->
            val baseUrl = "http://192.168.101.4:8080"
            val formattedPath = if (path.startsWith("/")) path else "/$path"
            val imageUrl = baseUrl + formattedPath
            Log.d("DonorProfile", "Loading image from: $imageUrl")
            Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.default_avatar)
                .error(R.drawable.default_avatar)
                .into(profileImage)
        }
    }

    private fun saveOrEditProfile(profile: DonorProfile) {
        Log.d("DonorProfile", "Saving profile with imagePath: ${profile.imagePath}")
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
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        return sharedPreferences.getLong("user_id", -1)
    }
}
