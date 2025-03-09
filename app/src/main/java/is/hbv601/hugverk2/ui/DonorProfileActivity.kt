package `is`.hbv601.hugverk2.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import `is`.hbv601.hugverk2.model.DonorProfile
import `is`.hbv601.hugverk2.R
import `is`.hbv601.hbv601.hugverk2.data.api.RetrofitClient
import `is`.hbv601.hugverk2.customviews.MultiSelectSpinner
import `is`.hbv601.hugverk2.data.api.ApiService
import `is`.hbv601.hugverk2.model.FavoriteRequest
import `is`.hbv601.hugverk2.model.FavoriteResponse
import `is`.hbv601.hugverk2.model.MyAppUser
import `is`.hbv601.hugverk2.model.UploadResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class DonorProfileActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    // Drawer components
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView

    // Edit fields
    private lateinit var spinnerEyeColor: Spinner
    private lateinit var spinnerHairColor: Spinner
    private lateinit var spinnerEducationLevel: Spinner
    private lateinit var spinnerRace: Spinner
    private lateinit var spinnerEthnicity: Spinner
    private lateinit var spinnerBloodType: Spinner
    private lateinit var spinnerDonorType: Spinner
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
    private lateinit var textDonorType: TextView
    private lateinit var textMedicalHistory: TextView
    private lateinit var textHeight: TextView
    private lateinit var textWeight: TextView
    private lateinit var textAge: TextView
    private lateinit var textGetToKnow: TextView

    //Favorite button
   // private lateinit var btnFavorite: Button
   // private lateinit var donor: MyAppUser

    private var imageUri: Uri? = null
    private var currentProfile: DonorProfile? = null

    private fun openImageChooser() {
        pickImageLauncher.launch("image/*")
    }

    //Here we use ActivityResultLauncher for picking an image.
    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            imageUri = uri
            Log.d("DonorProfile", "Image URI selected: $uri")
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
            Log.d("DonorProfile", "File created from URI: ${tempFile.absolutePath}")
            tempFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_donor_profile)

        // Initialize DrawerLayout and NavigationView
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)




        //donor = intent.getSerializableExtra("donor") as? MyAppUser ?: throw IllegalStateException("Donor not found")//Get donor info from intent

        //Set initial button text
        //updateFavoriteButton()

        //Handle button click
        //btnFavorite = findViewById(R.id.btnFavorite)
        //btnFavorite.setOnClickListener {
          //  Log.d("FavoriteButton", "Button Click Detected") //Log that the button is clicked
            //Toast.makeText(this, "Favorite button clicked", Toast.LENGTH_SHORT).show() //Alternative if the Log doesn't come, shows a toast
            //toggleFavorite()
       // }

        // Update Navigation Header with logged in username
        val headerView = navigationView.getHeaderView(0)
        val navHeaderTitle = headerView.findViewById<TextView>(R.id.nav_header_title)
        val sharedPrefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val username = sharedPrefs.getString("username", "Guest")
        navHeaderTitle.text = "Welcome, $username!"

        // Setup Toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(android.R.drawable.ic_menu_sort_by_size)

        // Bind edit fields
        spinnerEyeColor = findViewById(R.id.spinner_eyeColor)
        spinnerHairColor = findViewById(R.id.spinner_hairColor)
        spinnerEducationLevel = findViewById(R.id.spinner_educationLevel)
        spinnerRace = findViewById(R.id.spinner_race)
        spinnerEthnicity = findViewById(R.id.spinner_ethnicity)
        spinnerBloodType = findViewById(R.id.spinner_bloodType)
        spinnerDonorType = findViewById(R.id.spinner_donorType)
        spinnerMedicalHistory = findViewById(R.id.spinner_medicalHistory)
        editHeight = findViewById(R.id.edit_height)
        editWeight = findViewById(R.id.edit_weight)
        editAge = findViewById(R.id.edit_age)
        editGetToKnow = findViewById(R.id.edit_getToKnow)
        buttonSaveEdit = findViewById(R.id.buttonSaveEdit)
        buttonChooseImage = findViewById(R.id.buttonChooseImage)

        // Bind preview views
        profileImage = findViewById(R.id.donor_profile_image)
        textEyeColor = findViewById(R.id.textEyeColor)
        textHairColor = findViewById(R.id.textHairColor)
        textEducationLevel = findViewById(R.id.textEducationLevel)
        textRace = findViewById(R.id.textRace)
        textEthnicity = findViewById(R.id.textEthnicity)
        textBloodType = findViewById(R.id.textBloodType)
        textDonorType = findViewById(R.id.textDonorType)
        textMedicalHistory = findViewById(R.id.textMedicalHistory)
        textHeight = findViewById(R.id.textHeight)
        textWeight = findViewById(R.id.textWeight)
        textAge = findViewById(R.id.textAge)
        textGetToKnow = findViewById(R.id.textGetToKnow)

        // Initialize MultiSelectSpinner options
        spinnerMedicalHistory.setItems(resources.getStringArray(R.array.medical_history_options))

        // Fetch the donor profile
        val userId = getLoggedInUserId()
        fetchDonorProfile(userId)

        // Set up choose image button
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
                    RetrofitClient.getInstance()
                        .uploadFile(multipartBody)
                        .enqueue(object : Callback<UploadResponse> {
                            override fun onResponse(call: Call<UploadResponse>, response: Response<UploadResponse>) {
                                Log.d("DonorProfile", "Upload response code: ${response.code()}")
                                if (response.isSuccessful) {
                                    response.body()?.let { uploadResponse ->
                                        Log.d("DonorProfile", "Uploaded path: ${uploadResponse.fileUrl}")
                                        val updatedProfile = DonorProfile(
                                            eyeColor = spinnerEyeColor.selectedItem.toString(),
                                            hairColor = spinnerHairColor.selectedItem.toString(),
                                            educationLevel = spinnerEducationLevel.selectedItem.toString(),
                                            race = spinnerRace.selectedItem.toString(),
                                            ethnicity = spinnerEthnicity.selectedItem.toString(),
                                            bloodType = spinnerBloodType.selectedItem.toString(),
                                            // Make sure the donor type string exactly matches your backend expectations.
                                            donorType = spinnerDonorType.selectedItem.toString(),
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
                                    Log.e("DonorProfile", "Error Code: ${response.code()}")
                                    onFailure(call, Throwable("Error uploading file"))
                                }
                            }
                            override fun onFailure(call: Call<UploadResponse>, t: Throwable) {
                                Log.e("DonorProfile", "Upload Failure: ${t.message}")
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
                    donorType = spinnerDonorType.selectedItem.toString(),
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

    private fun fetchDonorProfile(userId: Long) {
        RetrofitClient.getInstance().getDonorProfile(userId)
            .enqueue(object : Callback<DonorProfile> {
                override fun onResponse(call: Call<DonorProfile>, response: Response<DonorProfile>) {
                    if (response.isSuccessful) {
                        response.body()?.let { profile ->
                            currentProfile = profile
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
    /*private fun addFavorite(request: FavoriteRequest) {
        RetrofitClient.getInstance().addFavorite(request.recipientId, request.donorId)
            .enqueue(object : Callback<FavoriteResponse> {
                override fun onResponse(call: Call<FavoriteResponse>, response: Response<FavoriteResponse>) {
                    if (response.isSuccessful) {
                        donor.isFavorited = true
                        btnFavorite.text = "Unfavorite"
                        Toast.makeText(this@DonorProfileActivity, "Donor favorited!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@DonorProfileActivity, "Failed to favorite donor", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<FavoriteResponse>, t: Throwable) {
                    Toast.makeText(this@DonorProfileActivity, "Network error", Toast.LENGTH_SHORT).show()
                }
            })
    }
    */


    /*private fun removeFavorite(request: FavoriteRequest) {
        RetrofitClient.getInstance().removeFavorite(request.recipientId, request.donorId)
            .enqueue(object : Callback<FavoriteResponse> {
                override fun onResponse(call: Call<FavoriteResponse>, response: Response<FavoriteResponse>) {
                    if (response.isSuccessful) {
                        donor.isFavorited = false
                        btnFavorite.text = "Favorite"
                        Toast.makeText(this@DonorProfileActivity, "Donor unfavorited!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@DonorProfileActivity, "Failed to unfavorite donor", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<FavoriteResponse>, t: Throwable) {
                    Toast.makeText(this@DonorProfileActivity, "Network error", Toast.LENGTH_SHORT).show()
                }
            })
    }
*/


    /*private fun toggleFavorite() {
        Log.d("FavoriteButton", "toggleFavorite() triggered by recipient!") //Log when button is clicked
        val recipientId = getUserId() //Fetch current recipient (user)
        val donorId = donor.id

        //Ensure recipeintId and donorId are valid
        if (recipientId == -1L || donorId == null) {
            Log.e("FavoriteButton", "Error: recipientId or donorId is null")
            return
        }
        val request = FavoriteRequest(recipientId, donorId) //Construct API request

        if (donor.isFavorited) {
            Log.d("FavoriteButton", "Recipient($recipientId) unfavoriting donor($donorId)") //Log removal and debug
            removeFavorite(request)
            //Log.d("FavoriteButton", "Recipient($recipientId) unfavoriting donor($donorId)") //Log removal and debug
            //viewModel.removeFavorite(recipientId, donorId)
        } else {
            Log.d("FavoriteButton", "Adding favorite for donor ID: $donorId") //Log addition
            addFavorite(request)//viewModel.addFavorite(recipientId, donorId)
        }
        //Toggle favorite state **After Api calls succeeds**
        donor.isFavorited = !donor.isFavorited
        updateFavoriteButton()
    }

    private fun updateFavoriteButton() {
        btnFavorite.text = if (donor.isFavorited) "Unfavorite" else "Favorite"
    }
*/
    private fun getUserId(): Long {
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        return sharedPreferences.getLong("user_id", -1) //Retrieve the recipient's ID
        //Fetch recipient Id from session/shared preferences
        //return 1L //Replace with actual user ID retrieval
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

        val donorTypeArray = resources.getStringArray(R.array.donor_type_options)
        val donorTypeIndex = donorTypeArray.indexOf(profile.donorType ?: "")
        if (donorTypeIndex >= 0) spinnerDonorType.setSelection(donorTypeIndex)

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
        textDonorType.text = "Donor Type: ${profile.donorType ?: "Not specified"}"
        textMedicalHistory.text = "Medical History: ${profile.medicalHistory?.joinToString(", ") ?: "Not specified"}"
        textHeight.text = "Height: ${profile.height ?: "Not specified"}"
        textWeight.text = "Weight: ${profile.weight ?: "Not specified"}"
        textAge.text = "Age: ${profile.age ?: "Not specified"}"
        textGetToKnow.text = "Get to Know: ${profile.getToKnow ?: "Not specified"}"

        // Now we will be using Cloudinary, our imagePath should be a the full URL.
        profile.imagePath?.trim()?.let { path ->
            val imageUrl = path
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
        RetrofitClient.getInstance().saveOrEditDonorProfile(profile) //(this)
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                drawerLayout.openDrawer(GravityCompat.START)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> {
                val intent = Intent(this, DonorHomeActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_profile -> {
                // Already on the profile screen.
            }
            R.id.nav_messages -> {
                Toast.makeText(this, "Messages clicked", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_favorites -> {
                Toast.makeText(this, "Favorites clicked", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_booking -> {
                Toast.makeText(this, "Booking clicked", Toast.LENGTH_SHORT).show()
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun getLoggedInUserId(): Long {
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        return sharedPreferences.getLong("user_id", -1)
    }
}