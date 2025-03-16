package `is`.hbv601.hugverk2.ui

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import `is`.hbv601.hbv601.hugverk2.data.api.RetrofitClient
import `is`.hbv601.hugverk2.R
import `is`.hbv601.hugverk2.model.RecipientProfile
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RecipientViewActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView

    // UI components for displaying recipient details
    private lateinit var recipientImage: ImageView
    private lateinit var tvTraits: TextView
    private lateinit var tvHeight: TextView
    private lateinit var tvWeight: TextView
    private lateinit var tvEyeColor: TextView
    private lateinit var tvHairColor: TextView
    private lateinit var tvEducationLevel: TextView
    private lateinit var tvMedicalHistory: TextView
    private lateinit var tvRace: TextView
    private lateinit var tvEthnicity: TextView
    private lateinit var tvBloodType: TextView
    private lateinit var tvRecipientType: TextView
    private lateinit var tvGetToKnow: TextView

    private var recipientProfileId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipient_view)

        // Initialize DrawerLayout and NavigationView
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        // Setup toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(android.R.drawable.ic_menu_sort_by_size)

        // Update Navigation Header with logged-in username
        val headerView = navigationView.getHeaderView(0)
        val navHeaderTitle = headerView.findViewById<TextView>(R.id.nav_header_title)
        val sharedPrefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val username = sharedPrefs.getString("username", "Guest")
        navHeaderTitle.text = "Welcome, $username!"

        // Get recipientProfileId passed from previous activity
        recipientProfileId = intent.getLongExtra("recipientProfileId", -1L)
        if (recipientProfileId == -1L) {
            Toast.makeText(this, "Recipient profile not found", Toast.LENGTH_SHORT).show()
            finish()
        }

        // Bind UI components from layout
        recipientImage = findViewById(R.id.recipientImage)
        tvTraits = findViewById(R.id.tvTraits)
        tvHeight = findViewById(R.id.tvHeight)
        tvWeight = findViewById(R.id.tvWeight)
        tvEyeColor = findViewById(R.id.tvEyeColor)
        tvHairColor = findViewById(R.id.tvHairColor)
        tvEducationLevel = findViewById(R.id.tvEducationLevel)
        tvMedicalHistory = findViewById(R.id.tvMedicalHistory)
        tvRace = findViewById(R.id.tvRace)
        tvEthnicity = findViewById(R.id.tvEthnicity)
        tvBloodType = findViewById(R.id.tvBloodType)
        tvRecipientType = findViewById(R.id.tvRecipientType)
        tvGetToKnow = findViewById(R.id.tvGetToKnow)

        // Load recipient profile data from the API
        loadRecipientProfile()
    }

    private fun loadRecipientProfile() {
        RetrofitClient.getInstance().getRecipientProfile(recipientProfileId)
            .enqueue(object : Callback<RecipientProfile> {
                override fun onResponse(
                    call: Call<RecipientProfile>,
                    response: Response<RecipientProfile>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let { recipient ->
                            updateUI(recipient)
                        } ?: Toast.makeText(this@RecipientViewActivity, "Recipient profile not found", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@RecipientViewActivity, "Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onFailure(call: Call<RecipientProfile>, t: Throwable) {
                    Toast.makeText(this@RecipientViewActivity, "Network error", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun updateUI(recipient: RecipientProfile) {
        tvTraits.text = "Traits: ${recipient.traits ?: "Not specified"}"
        tvHeight.text = "Height: ${recipient.height ?: "Not specified"} cm"
        tvWeight.text = "Weight: ${recipient.weight ?: "Not specified"} kg"
        tvEyeColor.text = "Eye Color: ${recipient.eyeColor ?: "Not specified"}"
        tvHairColor.text = "Hair Color: ${recipient.hairColor ?: "Not specified"}"
        tvEducationLevel.text = "Education Level: ${recipient.educationLevel ?: "Not specified"}"
        tvMedicalHistory.text = "Medical History: ${recipient.medicalHistory ?: "Not specified"}"
        tvRace.text = "Race: ${recipient.race ?: "Not specified"}"
        tvEthnicity.text = "Ethnicity: ${recipient.ethnicity ?: "Not specified"}"
        tvBloodType.text = "Blood Type: ${recipient.bloodType ?: "Not specified"}"
        tvRecipientType.text = "Recipient Type: ${recipient.recipientType ?: "Not specified"}"
        tvGetToKnow.text = "Get to Know: ${recipient.getToKnow ?: "Not specified"}"

        // Load recipient image using Glide
        recipient.imagePath?.let { imageUrl ->
            Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.default_avatar)
                .error(R.drawable.default_avatar)
                .into(recipientImage)
        }
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
                val intent = Intent(this, RecipientHomeActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_profile -> {
                val intent = Intent(this, RecipientProfileActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_messages -> {
                val intent = Intent(this, MessageListActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_favorites -> {
                val intent = Intent(this, FavoriteActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_matches -> {
                val intent = Intent(this, RecipientMatchesActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_booking -> {
                Toast.makeText(this, "Booking clicked", Toast.LENGTH_SHORT).show()
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}
