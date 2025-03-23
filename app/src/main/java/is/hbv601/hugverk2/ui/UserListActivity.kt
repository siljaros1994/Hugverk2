package `is`.hbv601.hugverk2.ui

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import `is`.hbv601.hugverk2.R
import `is`.hbv601.hbv601.hugverk2.data.api.RetrofitClient
import `is`.hbv601.hugverk2.adapter.UserAdapter
import `is`.hbv601.hugverk2.model.UserDTO
import `is`.hbv601.hugverk2.model.DeleteResponseDTO
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserListActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var userAdapter: UserAdapter
    private lateinit var userList: MutableList<UserDTO>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_list)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        fetchUsersFromApi()
    }

    private fun fetchUsersFromApi() {
        val apiService = RetrofitClient.getInstance()

        apiService.getAllUsers().enqueue(object : Callback<List<UserDTO>> {
            override fun onResponse(call: Call<List<UserDTO>>, response: Response<List<UserDTO>>) {
                if (response.isSuccessful) {
                    userList = response.body()?.toMutableList() ?: mutableListOf()
                    userAdapter = UserAdapter(userList, ::deleteUser)
                    recyclerView.adapter = userAdapter
                } else {
                    showToast("Failed to fetch users")
                }
            }

            override fun onFailure(call: Call<List<UserDTO>>, t: Throwable) {
                showToast("Error fetching users")
            }
        })
    }


    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun deleteUser(username: String, position: Int) {
        val apiService = RetrofitClient.getInstance()

        // Fetch stored cookies (same as fetching users)
        val cookies = RetrofitClient.getCookieString()

        Log.d("CookieDebug", "Cookies sent with DELETE request: $cookies")

        apiService.deleteUser(username, cookies).enqueue(object : Callback<DeleteResponseDTO> {
            override fun onResponse(call: Call<DeleteResponseDTO>, response: Response<DeleteResponseDTO>) {
                if (response.isSuccessful) {
                    val message = response.body()?.message ?: "User deleted"
                    userList.removeAt(position)
                    userAdapter.notifyItemRemoved(position)
                    showToast(message)
                } else if (response.code() == 401) {
                    showToast("Session expired. Please log in again.")
                } else {
                    showToast("Failed to delete user")
                }
            }

            override fun onFailure(call: Call<DeleteResponseDTO>, t: Throwable) {
                showToast("Error deleting user")
            }
        })
    }





}
