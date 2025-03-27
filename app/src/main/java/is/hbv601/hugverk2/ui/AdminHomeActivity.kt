package `is`.hbv601.hugverk2.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import `is`.hbv601.hugverk2.R

class AdminHomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_home)

        AdminNotificationHelper.createNotificationChannel(this)

        val btnSeeUsers: Button = findViewById(R.id.btnSeeUsers)
        btnSeeUsers.setOnClickListener {
            val intent = Intent(this, UserListActivity::class.java)
            startActivity(intent)
        }

        val testNotificationButton: Button = findViewById(R.id.btnTestNotification)
        testNotificationButton.setOnClickListener {
            AdminNotificationHelper.showDonationLimitNotification(
                this,
                donorName = "Test Donor"
            )
        }
    }
}
