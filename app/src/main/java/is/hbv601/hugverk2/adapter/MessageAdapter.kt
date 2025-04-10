package `is`.hbv601.hugverk2.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import `is`.hbv601.hugverk2.R
import `is`.hbv601.hugverk2.model.MessageDTO

class MessageAdapter(
    private val messages: List<MessageDTO>,
    private val userId: Long
) : RecyclerView.Adapter<MessageAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val messageTextView: TextView = view.findViewById(R.id.messageTextView)
        val profileImageView: ImageView = view.findViewById(R.id.profileImageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_message, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val message = messages[position]
        holder.messageTextView.text = message.content

        val isSent = message.senderId == userId

        if (isSent) {
            // sent message: right aligned
            holder.profileImageView.visibility = View.GONE

            val params = holder.messageTextView.layoutParams as ConstraintLayout.LayoutParams
            params.startToEnd = ConstraintLayout.LayoutParams.UNSET
            params.startToStart = ConstraintLayout.LayoutParams.UNSET
            params.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
            holder.messageTextView.layoutParams = params

        } else {
            // received message: left aligned with (now default)profile picture
            holder.profileImageView.visibility = View.VISIBLE

            val params = holder.messageTextView.layoutParams as ConstraintLayout.LayoutParams
            params.endToEnd = ConstraintLayout.LayoutParams.UNSET
            params.startToEnd = R.id.profileImageView
            holder.messageTextView.layoutParams = params

            Glide.with(holder.itemView.context)
                .load(message.senderProfilePictureUrl ?: R.drawable.default_avatar)
                .placeholder(R.drawable.default_avatar)
                .error(R.drawable.default_avatar)
                .into(holder.profileImageView)
        }
    }

    override fun getItemCount(): Int = messages.size
}