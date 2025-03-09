package `is`.hbv601.hugverk2.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import `is`.hbv601.hugverk2.R
import `is`.hbv601.hugverk2.adapter.FavoriteAdapter
import `is`.hbv601.hugverk2.model.MyAppUser



    class FavoriteActivity : AppCompatActivity() {

        private lateinit var recyclerView: RecyclerView
        private lateinit var adapter: FavoriteAdapter
        //private lateinit var viewModel: FavoriteViewModel

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_favorite) //XML file has to exist

            recyclerView = findViewById(R.id.recyclerViewFavorites)
            recyclerView.layoutManager = LinearLayoutManager(this)

            //viewModel = ViewModelProvider(this)[FavoriteViewModel::class.java]

            val userType = getUserType() // Determine if the user is a recipient or donor
            val userId = getUserId()     // Get the currently logged-in user's ID
            val favorites: List<MyAppUser> = loadFavorites(userType, userId) //Fetch favorites list


            if (userType == "recipient") {
                //viewModel.loadFavoriteDonors(userId)
            } else if (userType == "donor") {
                //viewModel.loadFavoritedBy(userId)
            }

            //viewModel.favorites.observe(this) { favorites ->
                adapter = FavoriteAdapter(favorites) { selectedDonor ->
                    Toast.makeText(this, "Clicked: ${selectedDonor.username}", Toast.LENGTH_SHORT).show()
                }
                recyclerView.adapter = adapter
            }
        }

        private fun getUserType(): String {
            // Retrieve from shared preferences, intent, or session
            return "recipient"  // Example: Change to "donor" for testing
        }

        private fun getUserId(): Long {
            // Retrieve from shared preferences, intent, or session
            return 1L  // Example: Replace with actual user ID
        }

private fun loadFavorites(userType: String, userId: Long): List<MyAppUser> {
    //Placeholder function: Fetch favorite donors or favorited-by recipients
    return listOf(
        MyAppUser(1L, "JohnDoe", "john@example.com", false),
        MyAppUser(2L, "JaneSmith", "jane@example.com", true)
    )
    }




