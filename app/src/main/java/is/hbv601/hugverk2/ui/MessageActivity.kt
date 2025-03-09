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



class MessageActivity : AppCompatActivity() {

    private lateinit var recyclerMessages: RecyclerView
    private lateinit var editMessage: EditText
    private lateinit var buttonSend: Button
    private var receiverId: Long? = null
    private val messages = mutableListOf<MessageDTO>()
    private lateinit var adapter: MessageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)

        receiverId = intent.getLongExtra("receiverId", -1L)
        if (receiverId == -1L) {
            receiverId = null
            Log.e("MessageActivity", "No receiver ID found! But opening UI.")
        }

        recyclerMessages = findViewById(R.id.recyclerMessages)
        editMessage = findViewById(R.id.editMessage)
        buttonSend = findViewById(R.id.buttonSend)

        recyclerMessages.layoutManager = LinearLayoutManager(this)
        adapter = MessageAdapter(messages, receiverId ?: -1L)
        recyclerMessages.adapter = adapter

        fetchMessages()

        buttonSend.setOnClickListener {
            sendMessage()
        }
    }

    private fun fetchMessages() {
        if (receiverId == null) {
            Log.e("MessageActivity", "Cannot fetch messages: No receiver selected.")
            return
        }

        RetrofitClient.getInstance(this).getMessages("recipient", receiverId!!)
            .enqueue(object : Callback<List<MessageDTO>> {
                override fun onResponse(call: Call<List<MessageDTO>>, response: Response<List<MessageDTO>>) {
                    if (response.isSuccessful) {
                        messages.clear()
                        response.body()?.let { messages.addAll(it) }
                        adapter.notifyDataSetChanged()
                        Log.d("MessageActivity", "Messages fetched successfully.")
                    } else {
                        Log.e("MessageActivity", "Failed to fetch messages: ${response.code()} - ${response.errorBody()?.string()}")
                    }
                }

                override fun onFailure(call: Call<List<MessageDTO>>, t: Throwable) {
                    Log.e("MessageActivity", "Error fetching messages: ${t.message}")
                }
            })
    }

    private fun sendMessage() {
        val messageText: String = editMessage.text?.toString() ?: ""

        if (receiverId == null) {
            Log.e("MessageActivity", "Cannot send message, no receiver selected!")
            return
        }

        if (messageText.isBlank()) {
            Log.e("MessageActivity", "Cannot send an empty message!")
            return
        }

        // Retrieve senderId (assuming it's stored in SharedPreferences)
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val senderId = sharedPreferences.getLong("user_id", -1L)

        if (senderId == -1L) {
            Log.e("MessageActivity", "Error: Sender ID not found!")
            return
        }

        val messageForm = MessageForm(senderId, receiverId!!, messageText)

        RetrofitClient.getInstance(this).sendMessage(messageForm)
            .enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Log.d("MessageActivity", "Message sent successfully!")
                        editMessage.text.clear()
                        fetchMessages()
                    } else {
                        Log.e("MessageActivity", "Failed to send message: ${response.code()} - ${response.errorBody()?.string()}")
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Log.e("MessageActivity", "Error sending message: ${t.message}")
                }
            })
    }


}