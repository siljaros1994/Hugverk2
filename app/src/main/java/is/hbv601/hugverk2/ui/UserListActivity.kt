package `is`.hbv601.hugverk2.ui

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import `is`.hbv601.hugverk2.R

class UserListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_list)

        val listView: ListView = findViewById(R.id.userListView)

        // TODO: Replace this with actual user data from database/API
        val sampleUsers = listOf("User 1", "User 2", "User 3", "Admin 1", "Recipient 1")

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, sampleUsers)
        listView.adapter = adapter
    }
}
