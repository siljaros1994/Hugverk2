package `is`.hbv601.hugverk2.ui

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import `is`.hbv601.hbv601.hugverk2.data.api.RetrofitClient
import `is`.hbv601.hugverk2.model.MessageDTO
import `is`.hbv601.hugverk2.model.MessageForm
import `is`.hbv601.hugverk2.R
import androidx.recyclerview.widget.RecyclerView
import `is`.hbv601.hugverk2.adapter.MessageAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.widget.Toast

class MessageActivity : AppCompatActivity() {

    private lateinit var recyclerMessages: RecyclerView
    private lateinit var editMessage: EditText
    private lateinit var buttonSend: Button

    private var receiverId: Long? = null
    private var loggedInUserId: Long = -1
    private var userType: String = "recipient"

    private val messages = mutableListOf<MessageDTO>()
    private lateinit var adapter: MessageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)

        // Retrieve extras
        receiverId = intent.getLongExtra("receiverId", -1L)
        if (receiverId == -1L) {
            Toast.makeText(this, "No recipient selected", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Shared preferences
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        loggedInUserId = sharedPreferences.getLong("user_id", -1L)
        userType = sharedPreferences.getString("user_type", "recipient") ?: "recipient"

        if (loggedInUserId == -1L) {
            Toast.makeText(this, "User not logged in properly", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Setup views
        recyclerMessages = findViewById(R.id.recyclerMessages)
        editMessage = findViewById(R.id.editMessage)
        buttonSend = findViewById(R.id.buttonSend)

        adapter = MessageAdapter(messages, loggedInUserId)
        recyclerMessages.layoutManager = LinearLayoutManager(this)
        recyclerMessages.adapter = adapter

        fetchMessages()

        buttonSend.setOnClickListener {
            sendMessage()
        }
    }

    private fun fetchMessages() {
        if (receiverId == null) return

        RetrofitClient.getInstance().getConversationWith(receiverId!!)
            .enqueue(object : Callback<List<MessageDTO>> {
                override fun onResponse(call: Call<List<MessageDTO>>, response: Response<List<MessageDTO>>) {
                    if (response.isSuccessful) {
                        messages.clear()
                        response.body()?.let {
                            messages.addAll(it)
                            Log.d("MessageActivity", "Messages received from API: ${it.size}")
                        }
                        adapter.notifyDataSetChanged()
                        recyclerMessages.scrollToPosition(messages.size - 1)
                    } else {
                        Log.e("MessageActivity", "Failed to fetch messages: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<List<MessageDTO>>, t: Throwable) {
                    Log.e("MessageActivity", "Error fetching messages", t)
                }
            })
    }


    private fun sendMessage() {
        val text = editMessage.text.toString().trim()
        if (text.isBlank() || receiverId == null) return

        Log.d("SendMessage", "Sending message: sender=$loggedInUserId, receiver=$receiverId, text=$text")

        val messageForm = MessageForm(
            senderId = loggedInUserId,
            receiverId = receiverId!!,
            text = text
        )

        RetrofitClient.getInstance().sendMessage(messageForm)
            .enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        editMessage.text.clear()
                        fetchMessages()
                    } else {
                        Log.e("MessageActivity", "Send failed: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Log.e("MessageActivity", "Send error", t)
                }
            })
    }
}
