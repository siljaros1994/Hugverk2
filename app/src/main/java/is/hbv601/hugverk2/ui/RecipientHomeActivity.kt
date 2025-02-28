package `is`.hbv601.hugverk2.ui

import android.content.Intent
import `is`.hbv601.hugverk2.R
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import `is`.hbv601.hugverk2.databinding.ActivityRecipientHomeBinding

class RecipientHomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityRecipientHomeBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecipientHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Here we setup the toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(android.R.drawable.ic_menu_sort_by_size)

        // Here we initialize the drawer layout and navigation view
        drawerLayout = binding.drawerLayout
        navigationView = binding.navView

        // Set up navigation item selection
        navigationView.setNavigationItemSelectedListener(this)

        // Here we retrieve the user data
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val username = sharedPreferences.getString("username", null)

        if (username == null) {
            Toast.makeText(this, "User data not found. Please log in again.", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            // Here we display the username on the homepage
            binding.welcomeMessage.text = "Hello, $username!"
            val headerView = navigationView.getHeaderView(0)
            val navHeaderTitle = headerView.findViewById<TextView>(R.id.nav_header_title)
            navHeaderTitle.text = "Welcome, $username!"
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
                Toast.makeText(this, "Home clicked", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_profile -> {
                val intent = Intent(this, RecipientProfileActivity::class.java) // Always Recipient profile for RecipientHome
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
            // Handle other menu items
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}