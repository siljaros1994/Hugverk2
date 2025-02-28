package `is`.hbv601.hugverk2.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import `is`.hbv601.hbv601.hugverk2.data.api.RetrofitClient
import `is`.hbv601.hugverk2.databinding.ActivityLoginBinding
import `is`.hbv601.hugverk2.model.LoginRequest
import `is`.hbv601.hugverk2.model.LoginResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Handle login button click
        binding.loginButton.setOnClickListener {
            val username = binding.usernameEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            if (username.isNotEmpty() && password.isNotEmpty()) {
                login(username, password)
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }

        // Handle register TextView click
        binding.registerTextView.setOnClickListener {
            // Navigate to RegisterActivity
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun login(username: String, password: String) {
        val loginRequest = LoginRequest(username, password)
        Log.d("LoginActivity", "Logging in with username: $username, password: $password")
        RetrofitClient.getInstance(this).login(loginRequest).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                Log.d("LoginActivity", "Response code: ${response.code()}")
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    if (loginResponse != null) {
                        Log.d("LoginActivity", "Login successful. User ID: ${loginResponse.userId}, User Type: ${loginResponse.userType}")
                        saveUserData(loginResponse.userId, loginResponse.userType, loginResponse.message)
                        val intent = when (loginResponse.userType) {
                            "donor" -> Intent(this@LoginActivity, DonorHomeActivity::class.java)
                            "recipient" -> Intent(this@LoginActivity, RecipientHomeActivity::class.java)
                            else -> null
                        }
                        if (intent != null) {
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this@LoginActivity, "Unknown user type", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@LoginActivity, "Login failed: Empty response", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.d("LoginActivity", "Login failed. Response code: ${response.code()}")
                    Toast.makeText(this@LoginActivity, "Invalid credentials", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Log.e("LoginActivity", "Login failed", t)
                Toast.makeText(this@LoginActivity, "Network error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun saveUserData(userId: Long, userType: String, token: String) {
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putLong("user_id", userId)
        editor.putString("user_type", userType)
        editor.putString("token", token) // Save the token
        editor.apply()
    }
}