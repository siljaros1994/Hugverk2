package `is`.hbv601.hugverk2.ui

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
import `is`.hbv601.hbv601.hugverk2.data.api.RetrofitClient
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
                //Toast.makeText(this@FavoriteActivity, "Unfavoriting ${donor.username}", Toast.LENGTH_SHORT).show()
                removeFavorite(donor)
            }

            override fun onViewProfileClicked(donor: DonorProfile) {
                // Handle viewing profile
                Toast.makeText(this@FavoriteActivity, "Viewing ${donor.donorProfileId}", Toast.LENGTH_SHORT).show()
            }
        })

        recyclerView.adapter = donorAdapter

        loadFavoriteDonors()
    }
    private fun getAuthToken(): String? {
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        return sharedPreferences.getString("token", null)
    }


    private fun loadFavoriteDonors() {
        val recipientId = getUserId()
        val authToken = getAuthToken() ?: ""// Retrieve token

        Log.d("AuthToken", "Token being sent in FavoriteActivity: $authToken") // Debugging

        RetrofitClient.getInstance().getFavoriteDonors(recipientId, authToken).enqueue(object : Callback<List<DonorProfile>> {
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
        val authToken = getAuthToken() ?: ""

        RetrofitClient.getInstance().removeFavorite(recipientId, donorId, authToken).enqueue(object : Callback<FavoriteResponse> {
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

    private fun getUserId(): Long {
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        return sharedPreferences.getLong("user_id", -1)
    }
}
