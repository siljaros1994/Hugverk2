package `is`.hbv601.hugverk2.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import `is`.hbv601.hugverk2.R
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.AdapterView
import android.widget.Spinner
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import com.google.android.material.navigation.NavigationView
import `is`.hbv601.hbv601.hugverk2.data.api.RetrofitClient
import `is`.hbv601.hugverk2.adapter.DonorAdapter
import `is`.hbv601.hugverk2.data.db.AppDatabase
import `is`.hbv601.hugverk2.databinding.ActivityRecipientHomeBinding
import `is`.hbv601.hugverk2.model.DonorProfile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

    private val NOTIFICATION_PERMISSION_REQUEST_CODE = 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecipientHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Here we create the notification channel.
        MatchNotificationHelper.createNotificationChannel(this)

        // Request runtime permission for notifications on Android 13+.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this, arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    NOTIFICATION_PERMISSION_REQUEST_CODE
                )
            }
        }

        // Check for match events from Room.
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val recipientId = sharedPreferences.getLong("user_id", -1)
        if (recipientId != -1L) {
            CoroutineScope(Dispatchers.IO).launch {
                val db = AppDatabase.getDatabase(this@RecipientHomeActivity)
                val events = db.matchEventDao().getMatchEventsForRecipient(recipientId)
                if (events.isNotEmpty()) {
                    withContext(Dispatchers.Main) {
                        MatchNotificationHelper.showMatchNotification(
                            context = this@RecipientHomeActivity,
                            matchTitle = "New Match Received!",
                            matchMessage = "A donor has matched with you. Check your matches!"
                        )
                    }
                    db.matchEventDao().deleteEventsForRecipient(recipientId)
                    Log.d("RecipientHome", "Match events processed and cleared for recipient $recipientId")
                }
            }
        }

        // Test button to manually trigger a simple notification.
        binding.btnTestNotification.setOnClickListener {
            Log.d("RecipientHome", "Test notification button clicked")
            MatchNotificationHelper.showSimpleNotification(
                context = this,
                title = "Test Notification",
                message = "This is a test notification."
            )
        }

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
        val username = sharedPreferences.getString("username", "Unknown")
        val locationSpinner: Spinner = findViewById(R.id.spinnerLocation)

        // here we define list of locations
        val locations = listOf("All", "Höfuðborgarsvæðið", "Suðurnes", "Norðurland", "Vesturland", "Austurland", "Suðurland")

        // create adapter for the spinner
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, locations)
        locationSpinner.adapter = adapter

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
        // recyclerview initialize
        donorRecyclerView = findViewById(R.id.rvDonorCards)
        val layoutManager = GridLayoutManager(this, 1)
        donorRecyclerView.layoutManager = layoutManager

        donorAdapter = DonorAdapter(donorsList, object : DonorAdapter.OnDonorClickListener {
            override fun onFavoriteClicked(donor: DonorProfile) {
                Log.d("FavoriteAction", "Calling API to favorite donor with id: ${donor.donorProfileId}")
                RetrofitClient.getInstance().addFavoriteDonor(donor.donorProfileId!!).enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if (response.isSuccessful) {
                            Log.d("FavoriteAction", "Favorite added successfully for donor id: ${donor.donorProfileId}")
                            Toast.makeText(this@RecipientHomeActivity, "Donor added to favorites", Toast.LENGTH_SHORT).show()
                        } else {
                            Log.e("FavoriteAction", "Error adding favorite: ${response.code()}")
                            Toast.makeText(this@RecipientHomeActivity, "Error adding favorite", Toast.LENGTH_SHORT).show()
                        }
                    }
                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        Log.e("FavoriteAction", "Network error when adding favorite", t)
                        Toast.makeText(this@RecipientHomeActivity, "Network error", Toast.LENGTH_SHORT).show()
                    }
                })
            }

            override fun onUnfavoriteClicked(donor: DonorProfile) {
                Log.d("FavoriteAction", "Calling API to unfavorite donor with id: ${donor.donorProfileId}")
                RetrofitClient.getInstance().unfavoriteDonor(donor.donorProfileId!!).enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if (response.isSuccessful) {
                            Toast.makeText(this@RecipientHomeActivity, "Donor removed from favorites", Toast.LENGTH_SHORT).show()
                            // Optionally update donor state and refresh list
                        } else {
                            Toast.makeText(this@RecipientHomeActivity, "Error removing favorite", Toast.LENGTH_SHORT).show()
                        }
                    }
                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        Toast.makeText(this@RecipientHomeActivity, "Network error", Toast.LENGTH_SHORT).show()
                    }
                })
            }

            override fun onViewProfileClicked(donor: DonorProfile) {
                // Launch the DonorViewActivity with the donor's profile ID
                val intent = Intent(this@RecipientHomeActivity, DonorViewActivity::class.java)
                intent.putExtra("donorProfileId", donor.donorProfileId)
                startActivity(intent)
            }
        })

        donorRecyclerView.adapter = donorAdapter
        //search bar setup
        searchView = findViewById(R.id.searchView)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String?): Boolean {
                val selectedLocation = locations[locationSpinner.selectedItemPosition]
                filterDonors(newText, selectedLocation)
                return true
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                val selectedLocation = locations[locationSpinner.selectedItemPosition]
                filterDonors(query, selectedLocation)
                return true
            }
        })

        // Pagination: button click listeners
        binding.btnPreviousPage.setOnClickListener {
            if (currentPage > 0) {
                val selectedLocation = locations[locationSpinner.selectedItemPosition]
                loadDonors(currentPage - 1, selectedLocation)
            }
        }

        binding.btnNextPage.setOnClickListener {
            if (!isLastPage) {
                val selectedLocation = locations[locationSpinner.selectedItemPosition]
                loadDonors(currentPage + 1, selectedLocation)
            }
        }
        // Here we load the first page
        loadDonors(0, null)

        //location selection changes handler
        locationSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedLocation = if (position == 0) null else locations[position]
                loadDonors(0, selectedLocation)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

    }

    private fun loadDonors(page: Int, selectedLocation: String? = null) {
        isLoading = true

        val locationParam = if (selectedLocation == "All" || selectedLocation.isNullOrBlank()) null else selectedLocation

        RetrofitClient.getInstance().getDonors(page, pageSize, locationParam)
            .enqueue(object : Callback<List<DonorProfile>> {
                override fun onResponse(call: Call<List<DonorProfile>>, response: Response<List<DonorProfile>>) {
                    isLoading = false
                    if (response.isSuccessful) {
                        val donors = response.body() ?: emptyList()

                        if (donors.isEmpty()) {
                            donorsList.clear()
                            donorAdapter.updateList(donorsList)
                            binding.tvCurrentPage.text = "No donors found"
                            isLastPage = true
                        } else {
                            if (page == 0) {
                                donorsList.clear()
                            }
                            currentPage = page
                            donorsList.clear()
                            donorsList.addAll(donors)
                            filterDonors(searchView.query.toString(), selectedLocation)
                            binding.tvCurrentPage.text = "Page ${currentPage + 1}"
                            isLastPage = donors.size < pageSize
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


    private fun filterDonors(query: String?, selectedLocation: String?) {
        filteredList.clear()
        filteredList.addAll(donorsList.filter { donor ->
            val matchesSearch = query.isNullOrBlank() || donor.donorType?.contains(query, ignoreCase = true) == true ||
                    donor.eyeColor?.contains(query, ignoreCase = true) == true ||
                    donor.hairColor?.contains(query, ignoreCase = true) == true ||
                    donor.educationLevel?.contains(query, ignoreCase = true) == true ||
                    donor.race?.contains(query, ignoreCase = true) == true ||
                    donor.ethnicity?.contains(query, ignoreCase = true) == true ||
                    donor.bloodType?.contains(query, ignoreCase = true) == true ||
                    donor.getToKnow?.contains(query, ignoreCase = true) == true ||
                    donor.traits?.contains(query, ignoreCase = true) == true

            val matchesLocation = selectedLocation.isNullOrBlank() || selectedLocation == "All" || donor.location?.contains(selectedLocation, ignoreCase = true) == true

            matchesSearch && matchesLocation
        })
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
                // Start Recipient profile activity (or refresh the current one)
                val intent = Intent(this, RecipientHomeActivity::class.java) // Always Recipient profile for RecipientHome
                startActivity(intent)
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