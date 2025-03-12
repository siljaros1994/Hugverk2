package `is`.hbv601.hugverk2.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import `is`.hbv601.hugverk2.R
import `is`.hbv601.hugverk2.model.MessageDTO

class MessageAdapter(private val messages: List<MessageDTO>, private val userId: Long) :
    RecyclerView.Adapter<MessageAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val messageTextView: TextView = view.findViewById(R.id.messageTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_message, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val message = messages[position]
        holder.messageTextView.text = message.content

        // Set different text alignment for sent/received messages
        if (message.senderId == userId) {
            holder.messageTextView.textAlignment = View.TEXT_ALIGNMENT_TEXT_END // Right (sent)
        } else {
            holder.messageTextView.textAlignment = View.TEXT_ALIGNMENT_TEXT_START // Left (received)
        }
    }

    override fun getItemCount(): Int = messages.size
}