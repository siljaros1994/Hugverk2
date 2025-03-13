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
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.navigation.NavigationView
import `is`.hbv601.hbv601.hugverk2.data.api.RetrofitClient
import `is`.hbv601.hugverk2.R
import `is`.hbv601.hugverk2.adapter.RecipientAdapter
import `is`.hbv601.hugverk2.databinding.ActivityDonorHomeBinding
import `is`.hbv601.hugverk2.model.RecipientProfile
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DonorHomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener { // Implement the listener

    private lateinit var binding: ActivityDonorHomeBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView

    private var userType: String? = null

    private var recipientList = mutableListOf<RecipientProfile>()
    private lateinit var recipientAdapter: RecipientAdapter

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

        // Here we setup an RecyclerView for recipient cards
        binding.rvRecipientCards.layoutManager = GridLayoutManager(this, 1)
        recipientAdapter = RecipientAdapter(recipientList, object : RecipientAdapter.OnRecipientClickListener {
            override fun onMatchClicked(recipient: RecipientProfile) {
                Toast.makeText(this@DonorHomeActivity, "Match action for recipient ID ${recipient.recipientProfileId}", Toast.LENGTH_SHORT).show()
            }
            override fun onViewProfileClicked(recipient: RecipientProfile) {
                // Launch a RecipientViewActivity to show full recipient details (create this activity separately)
                val intent = Intent(this@DonorHomeActivity, RecipientViewActivity::class.java)
                intent.putExtra("recipientProfileId", recipient.recipientProfileId)
                startActivity(intent)
            }
        })
        binding.rvRecipientCards.adapter = recipientAdapter

        // Retrieve donor ID from sharedPreferences.
        val donorId = sharedPreferences.getLong("donor_id", -1)
        if (donorId != -1L) {
            RetrofitClient.getInstance().getRecipientsWhoFavoritedDonor(donorId)
                .enqueue(object : Callback<List<RecipientProfile>> {
                    override fun onResponse(
                        call: Call<List<RecipientProfile>>,
                        response: Response<List<RecipientProfile>>
                    ) {
                        if (response.isSuccessful) {
                            val recipients = response.body() ?: emptyList()
                            recipientList.clear()
                            recipientList.addAll(recipients)
                            recipientAdapter.notifyDataSetChanged()
                        } else {
                            Toast.makeText(this@DonorHomeActivity, "Error fetching recipient favorites", Toast.LENGTH_SHORT).show()
                        }
                    }
                    override fun onFailure(call: Call<List<RecipientProfile>>, t: Throwable) {
                        Toast.makeText(this@DonorHomeActivity, "Network error", Toast.LENGTH_SHORT).show()
                    }
                })
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
                val intent = Intent(this, MessageListActivity::class.java)
                startActivity(intent)
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
                }, 300)
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}