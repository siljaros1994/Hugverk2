package `is`.hbv601.hugverk2.ui

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import android.util.Log
import android.os.Build
import android.window.OnBackInvokedDispatcher
import androidx.activity.OnBackPressedCallback
import androidx.room.RoomDatabase
import com.google.android.material.navigation.NavigationView
import `is`.hbv601.hbv601.hugverk2.data.api.RetrofitClient
import `is`.hbv601.hugverk2.R
import `is`.hbv601.hugverk2.databinding.ActivityDonorHomeBinding
import `is`.hbv601.hugverk2.model.LogoutResponse
//import okhttp3.Response
import `is`.hbv601.hugverk2.ui.LoginActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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

            // userType saved for possible later use
            this.userType = userType
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { //Android 13+
            onBackInvokedDispatcher.registerOnBackInvokedCallback(
                OnBackInvokedDispatcher.PRIORITY_DEFAULT
            ) {
                handleBackPressed()
            }
        } else  { //Older versions of Android
            onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true)
            {
                override fun handleOnBackPressed() {
                    handleBackPressed()
                }

            })
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
    private fun handleBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START) //Closes navigation drawer if open
        } else {
            finish() //Exits the activity
        }
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> {
                // Start donor home activity (or refresh the current one)
                val intent = Intent(this, DonorHomeActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_profile -> {
                // open donor profile activity
                val intent = Intent(this, DonorProfileActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_messages -> {
                Toast.makeText(this, "Messages clicked", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_matches -> {
                Toast.makeText(this, "Matches clicked", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_booking -> {
                Toast.makeText(this, "Booking clicked", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_logout_android -> { //Matches navigation menu ID
                Log.d("DonorHomeActivity", "Logout button clicked!") // ✅ Debugging Log

                            // Close the navigation drawer before logging out
                            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                                drawerLayout.closeDrawer(GravityCompat.START)
                            }

                            // Delay logout slightly to prevent UI conflicts
                            drawerLayout.postDelayed({
                                logoutUser() // Call logout function
                            }, 300) // Small delay ensures smooth UI transition
                        }
                    }
                    return true
                }




        }
    private fun logoutUser() {
        Log.d("LogoutActivity", "logoutUser() function triggered")
        val apiService = RetrofitClient.getInstance()

        Log.d("LogoutActivity", "Sending logout request to API...")

        apiService.logout().enqueue(object : retrofit2.Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                Log.d("LogoutActivity", "Response received: ${response.code()}") //Log API response

                if (response.isSuccessful) {
                    Log.d("LogoutActivity", "Logout successful, redirecting to login")

                    // Redirect to LoginActivity
                    val intent = Intent(this@DonorHomeActivity, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()

                    Toast.makeText(this@DonorHomeActivity, "Logged out successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Log.e("LogoutActivity", "Logout failed: ${response.code()}")
                    Toast.makeText(this@DonorHomeActivity, "Logout failed: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("Logout", "Network error during logout", t)
                Toast.makeText(this@DonorHomeActivity, "Network error", Toast.LENGTH_SHORT).show()
            }
        })
    }







