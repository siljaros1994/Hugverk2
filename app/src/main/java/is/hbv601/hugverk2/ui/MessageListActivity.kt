package `is`.hbv601.hugverk2.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import `is`.hbv601.hugverk2.R
import `is`.hbv601.hugverk2.adapter.MatchedUserAdapter
import `is`.hbv601.hugverk2.model.MatchedUser

class MessageListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message_list)

        recyclerView = findViewById(R.id.recyclerMatchedUsers)

        // Dummy matched users
        val dummyUsers = listOf(
            MatchedUser(1, "Lexa"),
            MatchedUser(2, "Thrandur"),
            MatchedUser(3, "Silja"),
            MatchedUser(4, "Gudrun")
        )

        val adapter = MatchedUserAdapter(dummyUsers) { user ->
            val intent = Intent(this, MessageActivity::class.java)
            intent.putExtra("receiverId", user.id)
            intent.putExtra("receiverName", user.name)
            startActivity(intent)
        }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }
}

