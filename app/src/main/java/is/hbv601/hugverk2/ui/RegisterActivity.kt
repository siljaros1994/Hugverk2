package `is`.hbv601.hugverk2.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import `is`.hbv601.hbv601.hugverk2.data.api.RetrofitClient
import `is`.hbv601.hugverk2.data.model.RegisterRequest
import `is`.hbv601.hugverk2.data.model.RegisterResponse
import `is`.hbv601.hugverk2.databinding.ActivityRegisterBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.registerButton.setOnClickListener {
            val username = binding.usernameEditText.text.toString()
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            val confirmPassword = binding.confirmPasswordEditText.text.toString()
            val userType = if (binding.donorRadioButton.isChecked) "donor" else "recipient"

            if (username.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()) {
                if (password == confirmPassword) {
                    register(username, email, password, confirmPassword, userType)
                } else {
                    Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun register(username: String, email: String, password: String, confirmPassword: String, userType: String) {
        val registerRequest = RegisterRequest(username, email, password, confirmPassword, userType)
        RetrofitClient.getInstance(this).register(registerRequest).enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                if (response.isSuccessful) {
                    val registerResponse = response.body()
                    if (registerResponse != null) {
                        // Save user data in SharedPreferences
                        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
                        val editor = sharedPreferences.edit()
                        editor.putLong("user_id", registerResponse.userId)
                        editor.putString("user_type", registerResponse.userType)
                        editor.putString("username", username) // Save the username
                        editor.putString("token", registerResponse.message)
                        editor.apply()

                        Toast.makeText(this@RegisterActivity, registerResponse.message, Toast.LENGTH_SHORT).show()
                        finish() // Go back to LoginActivity
                    } else {
                        Toast.makeText(this@RegisterActivity, "Registration failed: Empty response", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@RegisterActivity, "Registration failed", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                Toast.makeText(this@RegisterActivity, "Network error", Toast.LENGTH_SHORT).show()
            }
        })
    }
}