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

    companion object {
        val removedFavoriteIds = mutableSetOf<Long>()
    }

    private var currentPage = 0
    private val pageSize = 5
    private var isLastPage = false
    private var isLoading = false

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

        // Retrieve user data from shared preferences.
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
                val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
                // Here we approve match, by calling API to approve match.
                val donorId = sharedPreferences.getLong("user_id", -1)
                val recipientUserId = recipient.userId
                if (donorId == -1L) {
                    Log.e("DonorHomeActivity", "Donor ID not found in shared preferences.")
                    Toast.makeText(this@DonorHomeActivity, "Donor ID not found", Toast.LENGTH_SHORT).show()
                    return
                }
                if (recipientUserId == null) {
                    Log.e("DonorHomeActivity", "Recipient user ID is null for recipient: $recipient")
                    Toast.makeText(this@DonorHomeActivity, "Recipient ID not found", Toast.LENGTH_SHORT).show()
                    return
                }
                Log.d("DonorHomeActivity", "Match button pressed. Donor ID: $donorId, Recipient ID: $recipientUserId")
                RetrofitClient.getInstance().approveMatch(donorId, recipientUserId)
                    .enqueue(object : Callback<Void> {
                        override fun onResponse(call: Call<Void>, response: Response<Void>) {
                            Log.d("DonorHomeActivity", "ApproveMatch response: ${response.code()} ${response.message()}")
                            if (response.isSuccessful) {
                                Toast.makeText(this@DonorHomeActivity, "Match approved successfully", Toast.LENGTH_SHORT).show()
                                loadFavorites(currentPage) // refresh list
                            } else {
                                Toast.makeText(this@DonorHomeActivity, "Error approving match: ${response.code()}", Toast.LENGTH_SHORT).show()
                            }
                        }
                        override fun onFailure(call: Call<Void>, t: Throwable) {
                            Log.e("DonorHomeActivity", "ApproveMatch failure", t)
                            Toast.makeText(this@DonorHomeActivity, "Network error", Toast.LENGTH_SHORT).show()
                        }
                    })
            }

            override fun onUnMatchClicked(recipient: RecipientProfile) {
                val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
                val donorId = sharedPreferences.getLong("user_id", -1)
                val recipientUserId = recipient.userId
                if (donorId == -1L) {
                    Toast.makeText(this@DonorHomeActivity, "Donor ID not found", Toast.LENGTH_SHORT).show()
                    return
                }
                if (recipientUserId == null) {
                    Toast.makeText(this@DonorHomeActivity, "Recipient ID not found", Toast.LENGTH_SHORT).show()
                    return
                }
                Log.d("DonorHomeActivity", "Unmatch pressed. Donor ID: $donorId, Recipient ID: $recipientUserId")
                RetrofitClient.getInstance().unmatch(donorId, recipientUserId)
                    .enqueue(object : Callback<Void> {
                        override fun onResponse(call: Call<Void>, response: Response<Void>) {
                            if (response.isSuccessful) {
                                Toast.makeText(this@DonorHomeActivity, "Unmatched successfully", Toast.LENGTH_SHORT).show()
                                // Here we remove recipient from current list and refresh the view
                                recipientList.remove(recipient)
                                recipientAdapter.notifyDataSetChanged()
                                loadFavorites(currentPage)
                            } else {
                                Toast.makeText(this@DonorHomeActivity, "Error unmatching: ${response.code()}", Toast.LENGTH_SHORT).show()
                            }
                        }
                        override fun onFailure(call: Call<Void>, t: Throwable) {
                            Toast.makeText(this@DonorHomeActivity, "Network error", Toast.LENGTH_SHORT).show()
                        }
                    })
            }

            override fun onViewProfileClicked(recipient: RecipientProfile) {
                Log.d("DonorHomeActivity", "View profile clicked for recipient id: ${recipient.recipientProfileId}")
                val intent = Intent(this@DonorHomeActivity, RecipientViewActivity::class.java)
                intent.putExtra("recipientProfileId", recipient.recipientProfileId)
                startActivity(intent)
            }
        })
        binding.rvRecipientCards.adapter = recipientAdapter

        binding.btnPreviousPage.setOnClickListener {
            if (currentPage > 0 && !isLoading) {
                loadFavorites(currentPage - 1)
            }
        }
        binding.btnNextPage.setOnClickListener {
            if (!isLastPage && !isLoading) {
                loadFavorites(currentPage + 1)
            }
        }

        loadFavorites(currentPage)
    }

    private fun loadFavorites(page: Int) {
        isLoading = true
        val donorId = getSharedPreferences("user_prefs", MODE_PRIVATE).getLong("donor_id", -1)
        Log.d("DonorHomeActivity", "Loading favorites for donorId: $donorId, page: $page")
        if (donorId == -1L) {
            isLoading = false
            return
        }

        RetrofitClient.getInstance().getRecipientsWhoFavoritedDonor(donorId, page, pageSize)
            .enqueue(object : Callback<List<RecipientProfile>> {
                override fun onResponse(call: Call<List<RecipientProfile>>, response: Response<List<RecipientProfile>>) {
                    isLoading = false
                    if (response.isSuccessful) {
                        val favorites = response.body() ?: emptyList()
                        currentPage = page
                        isLastPage = favorites.size < pageSize
                        recipientList.clear()
                        recipientList.addAll(favorites)
                        recipientAdapter.notifyDataSetChanged()
                        binding.tvCurrentPage.text = "Page ${currentPage + 1}"
                        Log.d("DonorHomeActivity", "Favorites fetched: ${recipientList.size} items")
                    } else {
                        Toast.makeText(this@DonorHomeActivity, "Error fetching favorites", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onFailure(call: Call<List<RecipientProfile>>, t: Throwable) {
                    isLoading = false
                    Toast.makeText(this@DonorHomeActivity, "Network error", Toast.LENGTH_SHORT).show()
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
                val intent = Intent(this, DonorMatchesActivity::class.java)
                startActivity(intent)
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