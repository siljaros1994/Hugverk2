package `is`.hbv601.hugverk2.ui

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import `is`.hbv601.hugverk2.R
import `is`.hbv601.hugverk2.model.DonorProfile
import `is`.hbv601.hbv601.hugverk2.data.api.RetrofitClient
import `is`.hbv601.hugverk2.adapter.DonorAdapter
import `is`.hbv601.hugverk2.databinding.ActivityRecipientMatchesBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RecipientMatchesActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, DonorAdapter.OnDonorClickListener {

    private lateinit var binding: ActivityRecipientMatchesBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var matchesAdapter: DonorAdapter
    private var matchesList = mutableListOf<DonorProfile>()
    private var userType: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecipientMatchesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup toolbar
        val toolbar: Toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(android.R.drawable.ic_menu_sort_by_size)

        // Initialize drawer and navigation view
        drawerLayout = binding.drawerLayout
        navigationView = binding.navView
        navigationView.setNavigationItemSelectedListener(this)

        val sharedPrefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val username = sharedPrefs.getString("username", "Guest")
        userType = sharedPrefs.getString("user_type", "recipient")
        if (userType.equals("recipient", ignoreCase = true)) {
            navigationView.menu.clear()
            navigationView.inflateMenu(R.menu.drawer_menu_recipient)
        } else {
            navigationView.menu.clear()
            navigationView.inflateMenu(R.menu.drawer_menu_donor)
        }
        val headerView = navigationView.getHeaderView(0)
        val navHeaderTitle = headerView.findViewById<TextView>(R.id.nav_header_title)
        navHeaderTitle.text = "Welcome, $username!"

        // Setup RecyclerView
        binding.rvMatches.layoutManager = GridLayoutManager(this, 1)
        matchesAdapter = DonorAdapter(matchesList, this)
        binding.rvMatches.adapter = matchesAdapter

        loadMatches()
    }

    private fun loadMatches() {
        RetrofitClient.getInstance().getRecipientMatches().enqueue(object : Callback<List<DonorProfile>> {
            override fun onResponse(call: Call<List<DonorProfile>>, response: Response<List<DonorProfile>>) {
                if (response.isSuccessful) {
                    val matches = response.body() ?: emptyList()
                    matchesList.clear()
                    matchesList.addAll(matches)
                    matchesAdapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(this@RecipientMatchesActivity, "Error fetching matches", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<List<DonorProfile>>, t: Throwable) {
                Toast.makeText(this@RecipientMatchesActivity, "Network error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onFavoriteClicked(donor: DonorProfile) {
        Toast.makeText(this, "Already matched", Toast.LENGTH_SHORT).show()
    }

    override fun onUnfavoriteClicked(donor: DonorProfile) {
        // Retrieve the recipient's primary user id from SharedPreferences.
        val recipientId = getSharedPreferences("user_prefs", MODE_PRIVATE).getLong("user_id", -1)
        if (recipientId == -1L) {
            Toast.makeText(this, "Recipient ID not found", Toast.LENGTH_SHORT).show()
            return
        }
        // Get donor's user id (assuming donor.userId is the primary key)
        val donorId = donor.userId ?: run {
            Toast.makeText(this, "Donor ID not found", Toast.LENGTH_SHORT).show()
            return
        }
        RetrofitClient.getInstance().unmatch(recipientId, donorId)
            .enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@RecipientMatchesActivity, "Unmatched successfully", Toast.LENGTH_SHORT).show()
                        loadMatches()
                    } else {
                        Toast.makeText(this@RecipientMatchesActivity, "Error unmatching", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(this@RecipientMatchesActivity, "Network error", Toast.LENGTH_SHORT).show()
                }
            })
    }

    override fun onViewProfileClicked(donor: DonorProfile) {
        val intent = Intent(this, DonorViewActivity::class.java)
        intent.putExtra("donorProfileId", donor.donorProfileId)
        startActivity(intent)
    }

    override fun onMessageClicked(donor: DonorProfile) {}

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                binding.drawerLayout.openDrawer(GravityCompat.START)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> {
                if (userType.equals("recipient", ignoreCase = true)) {
                    startActivity(Intent(this, RecipientHomeActivity::class.java))
                } else {
                    startActivity(Intent(this, DonorHomeActivity::class.java))
                }
            }
            R.id.nav_profile -> {
                if (userType.equals("recipient", ignoreCase = true)) {
                    startActivity(Intent(this, RecipientProfileActivity::class.java))
                } else {
                    startActivity(Intent(this, DonorProfileActivity::class.java))
                }
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
                // Start Recipient match activity (or refresh the current one)
                if (userType.equals("recipient", ignoreCase = true)) {
                    startActivity(Intent(this, RecipientMatchesActivity::class.java))
                } else {
                    startActivity(Intent(this, DonorMatchesActivity::class.java))
                }
            }
            R.id.nav_booking -> Toast.makeText(this, "Booking clicked", Toast.LENGTH_SHORT).show()
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

}