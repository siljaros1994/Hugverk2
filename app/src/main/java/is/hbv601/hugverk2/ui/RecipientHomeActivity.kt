package `is`.hbv601.hugverk2.ui

import `is`.hbv601.hugverk2.R
import `is`.hbv601.hugverk2.model.Donor
import `is`.hbv601.hbv601.hugverk2.data.api.RetrofitClient
import `is`.hbv601.hugverk2.model.PaginatedResponse
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import `is`.hbv601.hugverk2.databinding.ActivityRecipientHomeBinding
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class RecipientHomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecipientHomeBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var recyclerView: RecyclerView
    private lateinit var donorAdapter: DonorAdapter
    private var donorList = mutableListOf<Donor>()

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
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    Toast.makeText(this, "Home clicked", Toast.LENGTH_SHORT).show()
                }
            }
            drawerLayout.closeDrawers()
            true
        }
        //Recyclear view setup
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        donorAdapter = DonorAdapter(donorList)
        recyclerView.adapter = donorAdapter

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
        fetchDonors()
    }
    private fun fetchDonors() {
        Log.d("RecipientHome", "fetchDonors() called")

        RetrofitClient.getInstance(this).getDonors().enqueue(object : Callback<PaginatedResponse<Donor>> {
            override fun onResponse(call: Call<PaginatedResponse<Donor>>, response: Response<PaginatedResponse<Donor>>) {
                Log.d("RecipientHome", "API Response Code: ${response.code()}")

                if (response.isSuccessful) {
                    response.body()?.let {
                        Log.d("RecipientHome", "API Response: ${it.content}")
                        donorList.clear()
                        donorList.addAll(it.content)
                        donorAdapter.notifyDataSetChanged()
                    }
                } else {
                    Log.e("RecipientHome", "API Error: ${response.errorBody()?.string()}")
                    Toast.makeText(this@RecipientHomeActivity, "Failed to load donors", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<PaginatedResponse<Donor>>, t: Throwable) {
                Log.e("RecipientHome", "Network Error: ${t.message}")
                Toast.makeText(this@RecipientHomeActivity, "Network error", Toast.LENGTH_SHORT).show()
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
}