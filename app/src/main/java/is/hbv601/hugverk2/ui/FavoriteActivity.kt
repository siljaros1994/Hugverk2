package `is`.hbv601.hugverk2.ui

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.navigation.NavigationView
import `is`.hbv601.hbv601.hugverk2.data.api.RetrofitClient
import `is`.hbv601.hugverk2.R
import `is`.hbv601.hugverk2.adapter.DonorAdapter
import `is`.hbv601.hugverk2.model.DonorProfile
import `is`.hbv601.hugverk2.databinding.ActivityFavoriteBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FavoriteActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityFavoriteBinding
    private lateinit var donorAdapter: DonorAdapter
    private var favoritesList = mutableListOf<DonorProfile>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup toolbar and drawer
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(android.R.drawable.ic_menu_sort_by_size)
        binding.navView.setNavigationItemSelectedListener(this)

        // Setup RecyclerView (using one column)
        binding.rvFavorites.layoutManager = GridLayoutManager(this, 1)
        donorAdapter = DonorAdapter(favoritesList, object : DonorAdapter.OnDonorClickListener {
            // In favorites context, clicking "Favorite" means "Unfavorite"
            override fun onFavoriteClicked(donor: DonorProfile) {
                RetrofitClient.getInstance(this@FavoriteActivity)
                    .unfavoriteDonor(donor.donorProfileId!!)
                    .enqueue(object : Callback<Void> {
                        override fun onResponse(call: Call<Void>, response: Response<Void>) {
                            if (response.isSuccessful) {
                                Toast.makeText(this@FavoriteActivity, "Donor removed from favorites", Toast.LENGTH_SHORT).show()
                                loadFavorites() // Refresh list after removal
                            } else {
                                Toast.makeText(this@FavoriteActivity, "Error removing favorite", Toast.LENGTH_SHORT).show()
                            }
                        }
                        override fun onFailure(call: Call<Void>, t: Throwable) {
                            Toast.makeText(this@FavoriteActivity, "Network error", Toast.LENGTH_SHORT).show()
                        }
                    })
            }

            override fun onViewProfileClicked(donor: DonorProfile) {
                val intent = Intent(this@FavoriteActivity, DonorViewActivity::class.java)
                intent.putExtra("donorProfileId", donor.donorProfileId)
                startActivity(intent)
            }
        })
        binding.rvFavorites.adapter = donorAdapter

        loadFavorites()
    }

    private fun loadFavorites() {
        RetrofitClient.getInstance(this).getFavoriteDonors().enqueue(object : Callback<List<DonorProfile>> {
            override fun onResponse(call: Call<List<DonorProfile>>, response: Response<List<DonorProfile>>) {
                if (response.isSuccessful) {
                    val favorites = response.body() ?: emptyList()
                    favoritesList.clear()
                    favoritesList.addAll(favorites)
                    donorAdapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(this@FavoriteActivity, "Error fetching favorites", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<List<DonorProfile>>, t: Throwable) {
                Toast.makeText(this@FavoriteActivity, "Network error", Toast.LENGTH_SHORT).show()
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
            R.id.nav_home -> startActivity(Intent(this, RecipientHomeActivity::class.java))
            R.id.nav_profile -> startActivity(Intent(this, RecipientProfileActivity::class.java))
            R.id.nav_messages -> Toast.makeText(this, "Messages clicked", Toast.LENGTH_SHORT).show()
            R.id.nav_matches -> startActivity(Intent(this, RecipientMatchesActivity::class.java))
            R.id.nav_booking -> Toast.makeText(this, "Booking clicked", Toast.LENGTH_SHORT).show()
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}