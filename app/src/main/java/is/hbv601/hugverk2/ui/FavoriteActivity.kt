package `is`.hbv601.hugverk2.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.MenuItem
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import `is`.hbv601.hbv601.hugverk2.data.api.RetrofitClient
import `is`.hbv601.hugverk2.R
import `is`.hbv601.hugverk2.adapter.DonorAdapter
import `is`.hbv601.hugverk2.model.DonorProfile
import androidx.appcompat.widget.Toolbar
import `is`.hbv601.hugverk2.R.id.btnLogout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FavoriteActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toolbar: Toolbar
    private lateinit var navView: NavigationView
    private lateinit var rvFavorites: RecyclerView
    private lateinit var donorAdapter: DonorAdapter
    private var favoritesList = mutableListOf<DonorProfile>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite)

        // Initialize views using findViewById
        drawerLayout = findViewById(R.id.drawer_layout)
        toolbar = findViewById(R.id.toolbar)
        navView = findViewById(R.id.nav_view)
        rvFavorites = findViewById(R.id.rvFavorites)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(android.R.drawable.ic_menu_sort_by_size)
        navView.setNavigationItemSelectedListener(this)

        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val username = sharedPreferences.getString("username", "Guest")

        val headerView = navView.getHeaderView(0)
        val navHeaderTitle = headerView.findViewById<TextView>(R.id.nav_header_title)
        navHeaderTitle.text = "Welcome, $username!"

        rvFavorites.layoutManager = GridLayoutManager(this, 1)
        donorAdapter = DonorAdapter(favoritesList, object : DonorAdapter.OnDonorClickListener {
            override fun onFavoriteClicked(donor: DonorProfile) {
                // Optionally handle the "favorite" click if needed.
            }
            override fun onUnfavoriteClicked(donor: DonorProfile) {
                Log.d("FavoriteActivity", "Unfavorite button clicked for donor id: ${donor.donorProfileId}")
                RetrofitClient.getInstance().unfavoriteDonor(donor.donorProfileId!!).enqueue(object : Callback<Void> {
                        override fun onResponse(call: Call<Void>, response: Response<Void>) {
                            if (response.isSuccessful) {
                                Toast.makeText(this@FavoriteActivity, "Donor removed from favorites", Toast.LENGTH_SHORT).show()
                                loadFavorites() // Refresh the list after removal
                            } else {
                                Toast.makeText(this@FavoriteActivity, "Error removing favorite", Toast.LENGTH_SHORT).show()
                            }
                        }
                        override fun onFailure(call: Call<Void>, t: Throwable) {
                            Toast.makeText(this@FavoriteActivity, "Network error", Toast.LENGTH_SHORT).show()
                        }
                    })
            }
            override fun onUnmatchClicked(donor: DonorProfile) {}
            override fun onViewProfileClicked(donor: DonorProfile) {
                val intent = Intent(this@FavoriteActivity, DonorViewActivity::class.java)
                intent.putExtra("donorProfileId", donor.donorProfileId)
                startActivity(intent)
            }
            override fun onMessageClicked(donor: DonorProfile) {}
        })
        rvFavorites.adapter = donorAdapter

        loadFavorites()

        val footerView = layoutInflater.inflate(R.layout.nav_footer, navView, false)
        val lp = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )
        lp.gravity = Gravity.BOTTOM
        footerView.layoutParams = lp
        navView.addView(footerView)

        footerView.findViewById<Button>(R.id.btnLogout).setOnClickListener {
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

    private fun loadFavorites() {
        RetrofitClient.getInstance().getFavoriteDonors().enqueue(object : Callback<List<DonorProfile>> {
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
                drawerLayout.openDrawer(GravityCompat.START)
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
                // Start recipient favorite activity (or refresh the current one)
                val intent = Intent(this, FavoriteActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_matches -> {
                val intent = Intent(this, RecipientMatchesActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_booking -> {
                val intent = Intent(this, BookingActivity::class.java)
                startActivity(intent)
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}