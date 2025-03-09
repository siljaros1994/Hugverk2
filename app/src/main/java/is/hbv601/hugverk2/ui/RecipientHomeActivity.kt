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
import android.util.Log
import android.os.Build
import android.window.OnBackInvokedDispatcher
import androidx.activity.OnBackPressedCallback
import androidx.room.RoomDatabase
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.widget.SearchView
import com.google.android.material.navigation.NavigationView
import `is`.hbv601.hugverk2.ui.DonorViewActivity
import `is`.hbv601.hbv601.hugverk2.data.api.RetrofitClient
import `is`.hbv601.hugverk2.adapter.DonorAdapter
import `is`.hbv601.hugverk2.databinding.ActivityRecipientHomeBinding
import `is`.hbv601.hugverk2.model.DonorProfile
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RecipientHomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityRecipientHomeBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var donorRecyclerView: RecyclerView
    private lateinit var donorAdapter: DonorAdapter
    private lateinit var searchView: SearchView

    private var donorsList = mutableListOf<DonorProfile>()
    private var filteredList = mutableListOf<DonorProfile>()
    private var currentPage = 0
    private var isLoading = false
    private var isLastPage = false
    private val pageSize = 4

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
        val username = sharedPreferences.getString("username", "Unknown")

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

        donorRecyclerView = findViewById(R.id.rvDonorCards)
        val layoutManager = GridLayoutManager(this, 1)
        donorRecyclerView.layoutManager = layoutManager

        donorAdapter = DonorAdapter(donorsList, object : DonorAdapter.OnDonorClickListener {
            override fun onFavoriteClicked(donor: DonorProfile) {
                // Here comes favorite action, we call API to add favorite
            }

            override fun onViewProfileClicked(donor: DonorProfile) {
                // Launch the DonorViewActivity with the donor's profile ID
                val intent = Intent(this@RecipientHomeActivity, DonorViewActivity::class.java)
                intent.putExtra("donorProfileId", donor.donorProfileId)
                startActivity(intent)
            }
        })

        donorRecyclerView.adapter = donorAdapter

        searchView = findViewById(R.id.searchView)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String?): Boolean {
                filterDonors(newText)
                return true
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                filterDonors(query)
                return true
            }
        })

        // Pagination: button click listeners
        binding.btnPreviousPage.setOnClickListener {
            if (currentPage > 0) {
                loadDonors(currentPage - 1)
            }
        }

        binding.btnNextPage.setOnClickListener {
            if (!isLastPage) {
                loadDonors(currentPage + 1)
            }
        }

        // Here we load the first page
        loadDonors(currentPage)
    }

    private fun loadDonors(page: Int) {
        isLoading = true
        RetrofitClient.getInstance().getDonors(page, pageSize).enqueue(object : Callback<List<DonorProfile>> { //this
            override fun onResponse(call: Call<List<DonorProfile>>, response: Response<List<DonorProfile>>) {
                isLoading = false
                if (response.isSuccessful) {
                    val donors = response.body() ?: emptyList()
                    if (page == 0) {
                        donorsList.clear()
                    }
                    if (donors.isNotEmpty()) {
                        currentPage = page
                        donorsList.clear()
                        donorsList.addAll(donors)
                        filterDonors(searchView.query.toString()) //for search
                        binding.tvCurrentPage.text = "Page ${currentPage + 1}"
                        // If fewer items than pageSize, it's the last page
                        isLastPage = donors.size < pageSize
                    } else {
                        isLastPage = true
                    }
                } else {
                    Toast.makeText(this@RecipientHomeActivity, "Error fetching donor profiles", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<DonorProfile>>, t: Throwable) {
                isLoading = false
                Toast.makeText(this@RecipientHomeActivity, "Network error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun filterDonors(query: String?) {
        filteredList.clear()
        if (query.isNullOrBlank()) {
            filteredList.addAll(donorsList)
        } else {
            filteredList.addAll(donorsList.filter { donor ->
                donor.donorType?.contains(query, ignoreCase = true) == true ||
                        donor.eyeColor?.contains(query, ignoreCase = true) == true ||
                        donor.hairColor?.contains(query, ignoreCase = true) == true ||
                        donor.educationLevel?.contains(query, ignoreCase = true) == true ||
                        donor.race?.contains(query, ignoreCase = true) == true ||
                        donor.ethnicity?.contains(query, ignoreCase = true) == true ||
                        donor.bloodType?.contains(query, ignoreCase = true) == true ||
                        donor.getToKnow?.contains(query, ignoreCase = true) == true ||
                        donor.traits?.contains(query, ignoreCase = true) == true
            })
        }
        donorAdapter.updateList(filteredList)
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
                val intent = Intent(this, MessageListActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_favorites -> {
                Toast.makeText(this, "Favorites clicked", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_matches -> {
                Toast.makeText(this, "Matches clicked", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_booking -> {
                Toast.makeText(this, "Booking clicked", Toast.LENGTH_SHORT).show()
            }
            // Handle other menu items
            R.id.nav_logout -> { //Matches navigation menu ID
                Log.d("RecipientHomeActivity", "Logout button clicked!") //Debugging Log
                //Close the navigation drawer before logging out
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START)
                }
                //Delay logout slightly to prevent UI conflicts
                drawerLayout.postDelayed({
                    val intent = Intent(this, LogoutActivity::class.java)
                    startActivity(intent) //Call logout function
                    finish()
                }, 300) //Small delay ensures smooth UI transition
            }

        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}