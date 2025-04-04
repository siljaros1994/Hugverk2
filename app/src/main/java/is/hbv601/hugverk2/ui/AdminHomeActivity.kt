package `is`.hbv601.hugverk2.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.Button
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import `is`.hbv601.hugverk2.R
import `is`.hbv601.hbv601.hugverk2.data.api.RetrofitClient
import `is`.hbv601.hugverk2.model.DonorProfile

class AdminHomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_home)
        //drawerLayout = findViewById(R.id.drawer_layout)
        //navigationView = findViewById(R.id.nav_view)

        AdminNotificationHelper.createNotificationChannel(this)

        val btnSeeUsers: Button = findViewById(R.id.btnSeeUsers)
        btnSeeUsers.setOnClickListener {
            val intent = Intent(this, UserListActivity::class.java)
            startActivity(intent)
        }


        val checkLimitsButton: Button = findViewById(R.id.btnCheckDonorLimits)
        checkLimitsButton.setOnClickListener {
            fetchDonorsAndNotifyIfLimitReached()
        }



        fetchDonorsAndNotifyIfLimitReached()

        //Logout button
        val btnLogout: Button = findViewById(R.id.btnLogout)
        btnLogout.setOnClickListener {
            val intent = Intent(this, LogoutActivity::class.java)
            startActivity(intent)
            finish()
        }

    }


    private fun fetchDonorsAndNotifyIfLimitReached() {
        val apiService = RetrofitClient.getInstance()

        apiService.getDonors(0, 100).enqueue(object : retrofit2.Callback<List<DonorProfile>> {
            override fun onResponse(
                call: retrofit2.Call<List<DonorProfile>>,
                response: retrofit2.Response<List<DonorProfile>>
            ) {
                if (response.isSuccessful) {
                    val donors = response.body() ?: return
                    for (donor in donors) {
                        if ((donor.donationsCompleted ?: 0) == (donor.donationLimit ?: 0)) {
                            val donorName = donor.user?.username ?: "Donor #${donor.donorProfileId}"
                            AdminNotificationHelper.showDonationLimitNotification(
                                this@AdminHomeActivity,
                                donorName
                            )
                        }
                    }
                } else {
                    Log.e("AdminHome", "Failed to fetch donors: ${response.code()}")
                }
            }

            override fun onFailure(call: retrofit2.Call<List<DonorProfile>>, t: Throwable) {
                Log.e("AdminHome", "Error fetching donors", t)
            }
        })
    }




}
