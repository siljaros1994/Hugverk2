package `is`.hbv601.hugverk2.ui

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
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

class RecipientMatchesActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityRecipientMatchesBinding
    private lateinit var rvMatches: RecyclerView
    private lateinit var matchesAdapter: DonorAdapter
    private var matchesList = mutableListOf<DonorProfile>()

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
        binding.navView.setNavigationItemSelectedListener(this)

        // Setup RecyclerView
        rvMatches = binding.rvMatches
        rvMatches.layoutManager = GridLayoutManager(this, 1)
        matchesAdapter = DonorAdapter(matchesList, object : DonorAdapter.OnDonorClickListener {
            override fun onFavoriteClicked(donor: DonorProfile) {
                // Handle favorite action
            }

            override fun onUnfavoriteClicked(donor: DonorProfile) {
                // Handle unfavorite action
            }

            override fun onViewProfileClicked(donor: DonorProfile) {
                // Here we launch the DonorViewActivity with donorProfileId
                val intent = Intent(this@RecipientMatchesActivity, DonorViewActivity::class.java)
                intent.putExtra("donorProfileId", donor.donorProfileId)
                startActivity(intent)
            }
        })
        rvMatches.adapter = matchesAdapter

        // Fetch matches from the API
        loadMatches()
    }

    private fun loadMatches() {
        // Remove the context parameter from getInstance()
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
                val intent = Intent(this, RecipientHomeActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_profile -> {
                val intent = Intent(this, RecipientProfileActivity::class.java)
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
            R.id.nav_booking -> Toast.makeText(this, "Booking clicked", Toast.LENGTH_SHORT).show()
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}