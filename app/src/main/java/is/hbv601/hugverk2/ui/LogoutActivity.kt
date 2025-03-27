package `is`.hbv601.hugverk2.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import `is`.hbv601.hugverk2.R

class LogoutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("LogoutActivity", "User is logging out...")

        // Perform logout
        logoutUser()
    }

    private fun logoutUser() {
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        sharedPreferences.edit().clear().apply()

        Log.d("LogoutActivity", "Logging out user...")

        // Redirect to LoginActivity
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent) // Fix: Now we can start activity
        finish()

        runOnUiThread{
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show() // Fix: Context is valid
        }
    }
}
