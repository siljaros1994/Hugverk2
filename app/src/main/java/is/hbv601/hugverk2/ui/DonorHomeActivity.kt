package `is`.hbv601.hugverk2.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import `is`.hbv601.hugverk2.R

class DonorHomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_donor_home)

        // Retrieve user data
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val userId = sharedPreferences.getLong("user_id", -1)
        val userType = sharedPreferences.getString("user_type", null)
        val token = sharedPreferences.getString("token", null)

        if (userId == -1L || userType == null || token == null) {
            Toast.makeText(this, "User data not found. Please log in again.", Toast.LENGTH_SHORT).show()
            finish() // Close the activity
        } else {
            // Use the user data (e.g., make API calls)
            Toast.makeText(this, "Welcome, Donor! User ID: $userId", Toast.LENGTH_SHORT).show()
        }
    }
}