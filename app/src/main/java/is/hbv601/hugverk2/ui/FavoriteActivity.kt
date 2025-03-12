package `is`.hbv601.hugverk2.ui

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import `is`.hbv601.hugverk2.R
import `is`.hbv601.hugverk2.adapter.DonorAdapter
import `is`.hbv601.hugverk2.model.DonorProfile
import `is`.hbv601.hugverk2.data.api.RetrofitClient
//import `is`.hbv601.hbv601.hugverk2.data.api.RetrofitClient
import `is`.hbv601.hugverk2.model.FavoriteResponse
import retrofit2.Callback
import retrofit2.Call
import retrofit2.Response
//import `is`.hbv601.hugverk2.adapter.FavoriteAdapter
import `is`.hbv601.hugverk2.model.MyAppUser


class FavoriteActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var donorAdapter: DonorAdapter
    private var favoriteDonors = mutableListOf<DonorProfile>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite)

        recyclerView = findViewById(R.id.recyclerViewFavorites)
        recyclerView.layoutManager = LinearLayoutManager(this)

        donorAdapter = DonorAdapter(favoriteDonors, object : DonorAdapter.OnDonorClickListener {
            override fun onFavoriteClicked(donor: DonorProfile) {
                if (favoriteDonors.contains(donor)){
                    removeFavorite(donor)
                } else {
                    addFavorite(donor)
                }
                //Toast.makeText(this@FavoriteActivity, "Unfavoriting ${donor.username}", Toast.LENGTH_SHORT).show
            }

            override fun onViewProfileClicked(donor: DonorProfile) {
                // Handle viewing profile
                Toast.makeText(this@FavoriteActivity, "Viewing ${donor.donorProfileId}", Toast.LENGTH_SHORT).show()
            }
        })

        recyclerView.adapter = donorAdapter

        loadFavoriteDonors()
    }
    //private fun getAuthToken(): String {
    //    val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
    //    val token = sharedPreferences.getString("auth_token", "").orEmpty()  // Use correct key
        //val token = sharedPreferences.getString("token", "") ?: ""
        //val token = sharedPreferences.getString("token", null)
        //Log.d("AuthToken", "Retrieved Token: $token") // Debugging

        //return token //sharedPreferences.getString("token", null)
        //if (token.isBlank()) {
        //if (token.isEmpty()) {
        //    //if (token.isNullOrEmpty()) {
        //    Log.e("AuthToken", "Auth token is missing!")
        //    //return null//return ""
        //} else {
        //Log.d("AuthToken", "Retrieved Token: $token") //Debugging Log
       // }

        //return token //Ensure proper formatting


    private fun loadFavoriteDonors() {
        val recipientId = getUserId()
        //val authToken = getAuthToken() ?: ""// Retrieve token

        //Log.d("AuthToken", "Token being sent in FavoriteActivity: $authToken") // Debugging

        //val authToken = getAuthToken() //Retrieve the authentication token
        //if (authToken.isEmpty()) {
            //if (authToken.isNullOrEmpty()) {
        //    Log.e("FavoriteActivity", "Auth token is missing!")
        //Toast.makeText(this, "Authentication required. Please log in again.", Toast.LENGTH_SHORT).show()
        //    return
        //}

        //Log.d("FavoriteActivity", "Token being sent: $authToken") // Debugging

        RetrofitClient.getInstance().getFavoriteDonors(recipientId).enqueue(object : Callback<List<DonorProfile>> { //(this)
            override fun onResponse(call: Call<List<DonorProfile>>, response: Response<List<DonorProfile>>) {
                if (response.isSuccessful) {
                    favoriteDonors.clear()
                    response.body()?.let { favoriteDonors.addAll(it) }
                    donorAdapter.notifyDataSetChanged()
                } else {
                    Log.e("FavoriteActivity", "Failed to load favorite donors: ${response.code()}")
                    Toast.makeText(this@FavoriteActivity, "Error loading favorites", Toast.LENGTH_SHORT).show()
                }
            }


                override fun onFailure(call: Call<List<DonorProfile>>, t: Throwable) {
                    Log.e("FavoriteActivity", "Network error: ${t.message}")
                    Toast.makeText(this@FavoriteActivity, "Network error", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun removeFavorite(donor: DonorProfile) {
        val recipientId = getUserId()
        val donorId = donor.donorProfileId ?: return
        //val authToken = getAuthToken() ?: ""

        RetrofitClient.getInstance().removeFavorite(recipientId, donorId).enqueue(object : Callback<FavoriteResponse> {
            override fun onResponse(call: Call<FavoriteResponse>, response: Response<FavoriteResponse>) {
                if (response.isSuccessful) {
                    favoriteDonors.remove(donor)
                    donorAdapter.notifyDataSetChanged()
                    Toast.makeText(this@FavoriteActivity, "Removed from favorites", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@FavoriteActivity, "Error removing favorite", Toast.LENGTH_SHORT).show()
                }
            }


            override fun onFailure(call: Call<FavoriteResponse>, t: Throwable) {
                Toast.makeText(this@FavoriteActivity, "Network error", Toast.LENGTH_SHORT).show()
            }
            })
    }

    private fun addFavorite(donor: DonorProfile) {
        val recipientId = getUserId()
        val donorId = donor.donorProfileId ?: return
        //val authToken = getAuthToken()

        //if (authToken.isNullOrEmpty()) {
        //    Log.e("FavoriteActivity", "Auth token is missing!")
        //    Toast.makeText(this, "Authentication required. Please log in again.", Toast.LENGTH_SHORT).show()
        //    return
        //}

        //Log.d("FavoriteActivity", "Adding favorite for donor ID: $donorId with token: $authToken")

        RetrofitClient.getInstance().addFavorite(recipientId, donorId).enqueue(object : Callback<FavoriteResponse> {
            override fun onResponse(call: Call<FavoriteResponse>, response: Response<FavoriteResponse>) {
                if (response.isSuccessful) {
                    favoriteDonors.add(donor)
                    donorAdapter.notifyDataSetChanged()
                    Toast.makeText(this@FavoriteActivity, "Added to favorites", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@FavoriteActivity, "Error adding favorite", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<FavoriteResponse>, t: Throwable) {
                Log.e("FavoriteActivity", "Network error adding favorite: ${t.message}")
                Toast.makeText(this@FavoriteActivity, "Network error", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun getUserId(): Long {
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        return sharedPreferences.getLong("user_id", -1)
    }

}



