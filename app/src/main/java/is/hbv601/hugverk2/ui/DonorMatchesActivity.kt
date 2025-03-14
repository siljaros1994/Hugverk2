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
import `is`.hbv601.hugverk2.model.RecipientProfile
import `is`.hbv601.hbv601.hugverk2.data.api.RetrofitClient
import `is`.hbv601.hugverk2.adapter.RecipientAdapter
import `is`.hbv601.hugverk2.databinding.ActivityDonorMatchesBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DonorMatchesActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityDonorMatchesBinding
    private lateinit var rvMatches: RecyclerView
    private lateinit var matchesAdapter: RecipientAdapter
    private var matchesList = mutableListOf<RecipientProfile>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDonorMatchesBinding.inflate(layoutInflater)
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
        matchesAdapter = RecipientAdapter(matchesList, object : RecipientAdapter.OnRecipientClickListener {
            override fun onMatchClicked(recipient: RecipientProfile) {
                // Handle match action if needed
            }
            override fun onViewProfileClicked(recipient: RecipientProfile) {
                // Launch RecipientViewActivity with recipientProfileId
                val intent = Intent(this@DonorMatchesActivity, RecipientViewActivity::class.java)
                intent.putExtra("recipientProfileId", recipient.recipientProfileId)
                startActivity(intent)
            }
        })
        rvMatches.adapter = matchesAdapter

        // Load matches
        loadMatches()
    }

    private fun loadMatches() {
        // Remove the context parameter here!
        RetrofitClient.getInstance().getDonorMatches().enqueue(object : Callback<List<RecipientProfile>> {
            override fun onResponse(
                call: Call<List<RecipientProfile>>,
                response: Response<List<RecipientProfile>>
            ) {
                if (response.isSuccessful) {
                    val matches = response.body() ?: emptyList()
                    matchesList.clear()
                    matchesList.addAll(matches)
                    matchesAdapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(this@DonorMatchesActivity, "Error fetching matches", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<List<RecipientProfile>>, t: Throwable) {
                Toast.makeText(this@DonorMatchesActivity, "Network error", Toast.LENGTH_SHORT).show()
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
                val intent = Intent(this, DonorHomeActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_profile -> {
                val intent = Intent(this, DonorProfileActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_messages -> Toast.makeText(this, "Messages clicked", Toast.LENGTH_SHORT).show()
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}