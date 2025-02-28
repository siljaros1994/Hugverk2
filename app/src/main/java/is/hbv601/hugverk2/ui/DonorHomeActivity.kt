package `is`.hbv601.hugverk2.ui

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import `is`.hbv601.hugverk2.R
import `is`.hbv601.hugverk2.databinding.ActivityDonorHomeBinding

class DonorHomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener { // Implement the listener

    private lateinit var binding: ActivityDonorHomeBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDonorHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup for the toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(android.R.drawable.ic_menu_sort_by_size)

        // Initialize for drawer layout and navigation view
        drawerLayout = binding.drawerLayout
        navigationView = binding.navView

        // Set up navigation item selection
        navigationView.setNavigationItemSelectedListener(this)

        // Retrieve user data
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val username = sharedPreferences.getString("username", null)
        val userType = sharedPreferences.getString("user_type", null) // Retrieve user type

        if (username == null || userType == null) {
            Toast.makeText(this, "User data not found. Please log in again.", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            // Display the username on the homepage
            binding.welcomeMessage.text = "Hello, $username!"
            val headerView = navigationView.getHeaderView(0)
            val navHeaderTitle = headerView.findViewById<TextView>(R.id.nav_header_title)
            navHeaderTitle.text = "Welcome, $username!"

            // Save userType for later use
            this.userType = userType
        }
    }
    private var userType: String? = null

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
            R.id.nav_profile -> {
                val intent = if (userType == "donor") {
                    Intent(this, DonorProfileActivity::class.java)
                } else {
                    Intent(this, RecipientProfileActivity::class.java)
                }
                startActivity(intent)
            }
            // Handle other menu items
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}