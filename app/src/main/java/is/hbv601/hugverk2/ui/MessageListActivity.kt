package `is`.hbv601.hugverk2.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import `is`.hbv601.hugverk2.R
import android.widget.Toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import `is`.hbv601.hugverk2.adapter.DonorAdapter
import `is`.hbv601.hugverk2.adapter.RecipientAdapter
import `is`.hbv601.hugverk2.model.DonorProfile
import `is`.hbv601.hugverk2.model.RecipientProfile
import android.view.MenuItem
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import com.google.android.material.navigation.NavigationView
import `is`.hbv601.hbv601.hugverk2.data.api.RetrofitClient
import `is`.hbv601.hugverk2.databinding.ActivityMessageListBinding

class MessageListActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityMessageListBinding
    private lateinit var recipientAdapter: RecipientAdapter
    private lateinit var donorAdapter: DonorAdapter

    private val matchedRecipients = mutableListOf<RecipientProfile>()
    private val matchedDonors = mutableListOf<DonorProfile>()

    private lateinit var userType: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMessageListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar: Toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(android.R.drawable.ic_menu_sort_by_size)

        binding.navView.setNavigationItemSelectedListener(this)
        //usertype
        val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val username = prefs.getString("username", "Guest") ?: "Guest"
        userType = prefs.getString("user_type", "recipient") ?: "recipient"

        val headerView = binding.navView.getHeaderView(0)
        val navHeaderTitle = headerView.findViewById<TextView>(R.id.nav_header_title)
        navHeaderTitle.text = "Welcome, $username!"

        binding.rvMatches.layoutManager = LinearLayoutManager(this)

        if (userType == "donor") {
            setupDonorAdapter()
            loadDonorMatches()
        } else {
            setupRecipientAdapter()
            loadRecipientMatches()
        }

        val footerView = layoutInflater.inflate(R.layout.nav_footer, binding.navView, false)
        val lp = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )
        lp.gravity = Gravity.BOTTOM
        footerView.layoutParams = lp
        binding.navView.addView(footerView)

        footerView.findViewById<Button>(R.id.btnLogout).setOnClickListener {
            // Close the navigation drawer before logging out
            if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                binding.drawerLayout.closeDrawer(GravityCompat.START)
            }
            // Delay logout slightly to prevent UI conflicts
            binding.drawerLayout.postDelayed({
                val intent = Intent(this, LogoutActivity::class.java)
                startActivity(intent) //Call logout function
                finish()
            }, 300)
        }
    }

    private fun setupDonorAdapter() {
        recipientAdapter = RecipientAdapter(matchedRecipients, object : RecipientAdapter.OnRecipientClickListener {
            override fun onMessageClicked(recipient: RecipientProfile) {
                val intent = Intent(this@MessageListActivity, MessageActivity::class.java)
                intent.putExtra("receiverId", recipient.userId)
                intent.putExtra("receiverName", recipient.user?.username ?: "Unknown")
                intent.putExtra("receiverProfileImageUrl", recipient.imagePath)
                startActivity(intent)
            }
            override fun onMatchClicked(recipient: RecipientProfile) {}
            override fun onUnMatchClicked(recipient: RecipientProfile) {}
            override fun onViewProfileClicked(recipient: RecipientProfile) {}
        }, RecipientAdapter.Mode.MESSAGE_ONLY)

        binding.rvMatches.adapter = recipientAdapter
    }

    private fun setupRecipientAdapter() {
        donorAdapter = DonorAdapter(matchedDonors, object : DonorAdapter.OnDonorClickListener {
            override fun onMessageClicked(donor: DonorProfile) {
                val intent = Intent(this@MessageListActivity, MessageActivity::class.java)
                intent.putExtra("receiverId", donor.userId)
                intent.putExtra("receiverName", donor.user?.username ?: "Unknown")
                intent.putExtra("receiverProfileImageUrl", donor.imagePath)
                startActivity(intent)
            }
            override fun onFavoriteClicked(donor: DonorProfile) {}
            override fun onUnfavoriteClicked(donor: DonorProfile) {}
            override fun onUnmatchClicked(donor: DonorProfile) {}
            override fun onViewProfileClicked(donor: DonorProfile) {}
        }, mode = DonorAdapter.Mode.MESSAGE_ONLY)

        binding.rvMatches.adapter = donorAdapter
    }

    private fun loadDonorMatches() {
        RetrofitClient.getInstance().getDonorMatches()
            .enqueue(object : Callback<List<RecipientProfile>> {
                override fun onResponse(call: Call<List<RecipientProfile>>, response: Response<List<RecipientProfile>>) {
                    if (response.isSuccessful) {
                        matchedRecipients.clear()
                        matchedRecipients.addAll(response.body() ?: emptyList())
                        recipientAdapter.notifyDataSetChanged()
                    }
                }
                override fun onFailure(call: Call<List<RecipientProfile>>, t: Throwable) {}
            })
    }

    private fun loadRecipientMatches() {
        RetrofitClient.getInstance().getRecipientMatches()
            .enqueue(object : Callback<List<DonorProfile>> {
                override fun onResponse(call: Call<List<DonorProfile>>, response: Response<List<DonorProfile>>) {
                    if (response.isSuccessful) {
                        matchedDonors.clear()
                        matchedDonors.addAll(response.body() ?: emptyList())
                        donorAdapter.notifyDataSetChanged()
                    }
                }
                override fun onFailure(call: Call<List<DonorProfile>>, t: Throwable) {}
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
                val intent = Intent(this,
                    if (userType == "donor") DonorHomeActivity::class.java
                    else RecipientHomeActivity::class.java
                )
                startActivity(intent)
            }
            R.id.nav_profile -> {
                val intent = Intent(this,
                    if (userType == "donor") DonorProfileActivity::class.java
                    else RecipientProfileActivity::class.java
                )
                startActivity(intent)
            }
            R.id.nav_messages -> {
                val intent = Intent(this, MessageListActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_matches -> {
                val intent = Intent(this,
                    if (userType == "donor") DonorMatchesActivity::class.java
                    else RecipientMatchesActivity::class.java
                )
                startActivity(intent)
            }
            R.id.nav_booking -> {
                startActivity(Intent(this, BookingActivity::class.java))
            }
            R.id.nav_favorites -> {
                if (userType == "recipient") {
                    val intent = Intent(this, FavoriteActivity::class.java)
                    startActivity(intent)
                }
            }
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}

