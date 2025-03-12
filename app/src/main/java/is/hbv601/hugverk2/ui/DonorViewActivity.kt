package `is`.hbv601.hugverk2.ui

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import `is`.hbv601.hugverk2.data.api.RetrofitClient
//import `is`.hbv601.hbv601.hugverk2.data.api.RetrofitClient
import `is`.hbv601.hugverk2.R
import `is`.hbv601.hugverk2.model.DonorProfile
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DonorViewActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView

    // UI components for displaying donor details
    private lateinit var donorImage: ImageView
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
    private lateinit var tvDonorType: TextView
    private lateinit var tvGetToKnow: TextView

    private var donorProfileId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_donor_view)

        // Initialize DrawerLayout and NavigationView
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        // Update Navigation Header with logged in username
        val headerView = navigationView.getHeaderView(0)
        val navHeaderTitle = headerView.findViewById<TextView>(R.id.nav_header_title)
        val sharedPrefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val username = sharedPrefs.getString("username", "Guest")
        navHeaderTitle.text = "Welcome, $username!"

        // Setup toolbar and drawer for side panel navigation
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(android.R.drawable.ic_menu_sort_by_size)


        // Get donorProfileId passed from the donor card button
        donorProfileId = intent.getLongExtra("donorProfileId", -1L)
        if (donorProfileId == -1L) {
            Toast.makeText(this, "Donor profile not found", Toast.LENGTH_SHORT).show()
            finish()
        }

        // Bind UI components
        donorImage = findViewById(R.id.donorImage)
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
        tvDonorType = findViewById(R.id.tvDonorType)
        tvGetToKnow = findViewById(R.id.tvGetToKnow)

        // Load donor profile data
        loadDonorProfile()
    }

    private fun loadDonorProfile() {
        RetrofitClient.getInstance().viewDonorProfile(donorProfileId) //(this)
            .enqueue(object : Callback<DonorProfile> {
                override fun onResponse(call: Call<DonorProfile>, response: Response<DonorProfile>) {
                    if (response.isSuccessful) {
                        response.body()?.let { donor ->
                            updateUI(donor)
                        } ?: Toast.makeText(this@DonorViewActivity, "Donor profile not found", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@DonorViewActivity, "Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onFailure(call: Call<DonorProfile>, t: Throwable) {
                    Toast.makeText(this@DonorViewActivity, "Network error", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun updateUI(donor: DonorProfile) {
        tvTraits.text = "Traits: ${donor.traits ?: "Not specified"}"
        tvHeight.text = "Height: ${donor.height ?: "Not specified"} cm"
        tvWeight.text = "Weight: ${donor.weight ?: "Not specified"} kg"
        tvEyeColor.text = "Eye Color: ${donor.eyeColor ?: "Not specified"}"
        tvHairColor.text = "Hair Color: ${donor.hairColor ?: "Not specified"}"
        tvEducationLevel.text = "Education Level: ${donor.educationLevel ?: "Not specified"}"
        tvMedicalHistory.text = "Medical History: ${donor.medicalHistory?.joinToString(", ") ?: "Not specified"}"
        tvRace.text = "Race: ${donor.race ?: "Not specified"}"
        tvEthnicity.text = "Ethnicity: ${donor.ethnicity ?: "Not specified"}"
        tvBloodType.text = "Blood Type: ${donor.bloodType ?: "Not specified"}"
        tvDonorType.text = "Donor Type: ${donor.donorType ?: "Not specified"}"
        tvGetToKnow.text = "Get to Know: ${donor.getToKnow ?: "Not specified"}"

        // Load donor image with Glide
        donor.imagePath?.let { imageUrl ->
            Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.default_avatar)
                .error(R.drawable.default_avatar)
                .into(donorImage)
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
}