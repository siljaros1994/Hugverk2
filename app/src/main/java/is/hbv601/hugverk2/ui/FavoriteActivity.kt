package `is`.hbv601.hugverk2.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView


class FavoriteActivity {
    class FavoriteActivity : AppCompatActivity() {

        private lateinit var recyclerView: RecyclerView
        private lateinit var adapter: FavoriteAdapter
        private lateinit var viewModel: FavoriteViewModel

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_favorite)

            recyclerView = findViewById(R.id.recyclerViewFavorites)
            recyclerView.layoutManager = LinearLayoutManager(this)

            viewModel = ViewModelProvider(this)[FavoriteViewModel::class.java]

            val userType = getUserType() // Determine if the user is a recipient or donor
            val userId = getUserId()     // Get the currently logged-in user's ID

            if (userType == "recipient") {
                viewModel.loadFavoriteDonors(userId)
            } else if (userType == "donor") {
                viewModel.loadFavoritedBy(userId)
            }

            viewModel.favorites.observe(this) { favorites ->
                adapter = FavoriteAdapter(favorites)
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
    }

}