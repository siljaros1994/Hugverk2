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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.RoomDatabase
import com.google.android.material.navigation.NavigationView
import `is`.hbv601.hbv601.hugverk2.data.api.RetrofitClient
import `is`.hbv601.hugverk2.R
import `is`.hbv601.hugverk2.adapter.DonorAdapter
import `is`.hbv601.hugverk2.adapter.RecipientAdapter
import `is`.hbv601.hugverk2.data.api.ApiService
import `is`.hbv601.hugverk2.databinding.ActivityDonorHomeBinding
import `is`.hbv601.hugverk2.model.LogoutResponse
import `is`.hbv601.hugverk2.model.RecipientProfile
//import okhttp3.Response
import `is`.hbv601.hugverk2.ui.LoginActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DonorHomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener { // Implement the listener

    private lateinit var binding: ActivityDonorHomeBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var recyclerView: RecyclerView
    private lateinit var recipientAdapter: RecipientAdapter
    private lateinit var apiService: ApiService


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

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recipientAdapter = RecipientAdapter(emptyList()) { recipient ->
            Toast.makeText(this, "Clicked on ${recipient.user?.username}", Toast.LENGTH_SHORT).show()
        }
        recyclerView.adapter = recipientAdapter

        // Retrieve user data
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val username = sharedPreferences.getString("username", null)
        val userType = sharedPreferences.getString("user_type", null) // Retrieve user type
        val donorId = sharedPreferences.getLong("user_id", -1L) //Assuming user_id is donor ID
        val authToken = "Bearer" + sharedPreferences.getString("auth_token", null) //Replace with actual token key

        if (username == null || userType == null || donorId == -1L ) {
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

            //Fetch favoriting recipients
            fetchFavoritingRecipients(donorId, authToken)
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

    private fun fetchFavoritingRecipients(donorId: Long, authToken: String) {
        val apiService = RetrofitClient.getInstance() // Use getInstance() instead of direct apiService
        apiService.getFavoritingRecipients(donorId, authToken).enqueue(object : Callback<List<RecipientProfile>> {
            override fun onResponse(call: Call<List<RecipientProfile>>, response: Response<List<RecipientProfile>>) {
                if (response.isSuccessful) {
                    val recipients = response.body() ?: emptyList()
                    recipientAdapter.updateList(recipients) // Update RecyclerView
                } else {
                    Log.e("DonorHomeActivity", "Error fetching recipients: ${response.errorBody()?.string()}")
                }
            }
            override fun onFailure(call: Call<List<RecipientProfile>>, t: Throwable) {
                Log.e("DonorHomeActivity", "API call failed", t)
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
            R.id.nav_logout -> { //Matches navigation menu ID
                Log.d("DonorHomeActivity", "Logout button clicked!") // Debugging Log

                            // Close the navigation drawer before logging out
                            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                                drawerLayout.closeDrawer(GravityCompat.START)
                            }

                            // Delay logout slightly to prevent UI conflicts
                            drawerLayout.postDelayed({
                                val intent = Intent(this, LogoutActivity::class.java)
                                startActivity(intent) //Call logout function
                                finish()
                            }, 300) // Small delay ensures smooth UI transition
                        }
                    }
                    return true
                }




        }









