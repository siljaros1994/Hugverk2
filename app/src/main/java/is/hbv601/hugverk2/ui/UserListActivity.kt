package `is`.hbv601.hugverk2.ui

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import `is`.hbv601.hugverk2.R
import `is`.hbv601.hbv601.hugverk2.data.api.RetrofitClient
import `is`.hbv601.hugverk2.model.UserDTO
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserListActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var userAdapter: UserAdapter // Make sure UserAdapter is updated to accept real data

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_list)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        fetchUsersFromApi()
    }

    private fun fetchUsersFromApi() {
        val apiService = RetrofitClient.getInstance() //(this)

        apiService.getAllUsers().enqueue(object : Callback<List<UserDTO>> {
            override fun onResponse(call: Call<List<UserDTO>>, response: Response<List<UserDTO>>) {
                if (response.isSuccessful) {
                    val users = response.body() ?: emptyList()
                    userAdapter = UserAdapter(users)
                    recyclerView.adapter = userAdapter
                } else {
                    showToast("Failed to fetch users: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<UserDTO>>, t: Throwable) {
                Log.e("UserListActivity", "API call failed", t)
                showToast("Error fetching users")
            }
        })
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
