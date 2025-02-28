package `is`.hbv601.hugverk2.ui

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import `is`.hbv601.hbv601.hugverk2.data.api.RetrofitClient
import `is`.hbv601.hugverk2.R
import `is`.hbv601.hugverk2.model.RecipientProfile
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RecipientProfileActivity : AppCompatActivity() {

    private lateinit var profileImage: ImageView
    private lateinit var recipientType: TextView
    private lateinit var eyeColor: TextView
    private lateinit var hairColor: TextView
    private lateinit var educationLevel: TextView
    private lateinit var race: TextView
    private lateinit var ethnicity: TextView
    private lateinit var bloodType: TextView
    private lateinit var medicalHistory: TextView
    private lateinit var height: TextView
    private lateinit var weight: TextView
    private lateinit var age: TextView
    private lateinit var getToKnow: TextView
    private lateinit var editProfileButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipient_profile)

        // Initialize views
        profileImage = findViewById(R.id.recipient_profile_image)
        recipientType = findViewById(R.id.recipientType)
        eyeColor = findViewById(R.id.eyeColor)
        hairColor = findViewById(R.id.hairColor)
        educationLevel = findViewById(R.id.educationLevel)
        race = findViewById(R.id.race)
        ethnicity = findViewById(R.id.ethnicity)
        bloodType = findViewById(R.id.bloodType)
        medicalHistory = findViewById(R.id.medicalHistory)
        height = findViewById(R.id.height)
        weight = findViewById(R.id.weight)
        age = findViewById(R.id.age)
        getToKnow = findViewById(R.id.getToKnow)
        editProfileButton = findViewById(R.id.editProfileButton)

        // Fetch the donor profile
        val userId = getLoggedInUserId() // Replace with actual user ID retrieval logic
        fetchRecipientProfile(userId)

        // Set up Edit Profile button
        editProfileButton.setOnClickListener {
            // Launch EditProfileActivity
            Toast.makeText(this, "Edit Profile clicked", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchRecipientProfile(userId: Long) {
        val call = RetrofitClient.getInstance(this).getRecipientProfile(userId) // Use getInstance(this)
        call.enqueue(object : Callback<RecipientProfile> {
            override fun onResponse(call: Call<RecipientProfile>, response: Response<RecipientProfile>) {
                if (response.isSuccessful) {
                    val profile = response.body()
                    if (profile != null) {
                        updateUI(profile)
                    } else {
                        Toast.makeText(this@RecipientProfileActivity, "Empty response", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@RecipientProfileActivity, "Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<RecipientProfile>, t: Throwable) {
                Toast.makeText(this@RecipientProfileActivity, "Network error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateUI(profile: RecipientProfile) {
        recipientType.text = profile.recipientType ?: "Not specified"
        eyeColor.text = profile.eyeColor ?: "Not specified"
        hairColor.text = profile.hairColor ?: "Not specified"
        educationLevel.text = "Education Level: ${profile.educationLevel ?: "Not specified"}"
        race.text = "Race: ${profile.race ?: "Not specified"}"
        ethnicity.text = "Ethnicity: ${profile.ethnicity ?: "Not specified"}"
        bloodType.text = "Blood Type: ${profile.bloodType ?: "Not specified"}"
        medicalHistory.text = "Medical History: ${profile.medicalHistory?.joinToString(", ") ?: "Not specified"}"
        height.text = "Height: ${profile.height ?: "Not specified"}"
        weight.text = "Weight: ${profile.weight ?: "Not specified"}"
        age.text = "Age: ${profile.age ?: "Not specified"}"
        getToKnow.text = "Get to Know: ${profile.getToKnow ?: "Not specified"}"

        // Load profile image if available
        if (!profile.imagePath.isNullOrEmpty()) {
            Glide.with(this)
                .load("http://10.0.2.2:8080" + profile.imagePath)
                .placeholder(R.drawable.default_avatar)
                .error(R.drawable.default_avatar)
                .into(profileImage)
        }
    }

    private fun getLoggedInUserId(): Long {
        // Retrieve user ID from SharedPreferences or intent
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        return sharedPreferences.getLong("user_id", -1)
    }
}