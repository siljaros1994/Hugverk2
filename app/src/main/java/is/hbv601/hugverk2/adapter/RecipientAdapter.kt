package `is`.hbv601.hugverk2.adapter

import android.content.Intent
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.view.LayoutInflater
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import `is`.hbv601.hugverk2.R
import `is`.hbv601.hugverk2.model.RecipientProfile
import `is`.hbv601.hugverk2.ui.MessageActivity

class RecipientAdapter(
    private var recipients: List<RecipientProfile>,
    private val listener: OnRecipientClickListener,
    private val mode: Mode = Mode.DEFAULT
) : RecyclerView.Adapter<RecipientAdapter.RecipientViewHolder>() {

    enum class Mode {
        DEFAULT, MESSAGE_ONLY
    }

    interface OnRecipientClickListener {
        fun onMatchClicked(recipient: RecipientProfile)
        fun onUnMatchClicked(recipient: RecipientProfile)
        fun onViewProfileClicked(recipient: RecipientProfile)
        fun onMessageClicked(recipient: RecipientProfile)
        // Here we can add more actions
    }

    inner class RecipientViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val recipientImage: ImageView = itemView.findViewById(R.id.recipientImage)
        val tvEyeColor: TextView = itemView.findViewById(R.id.tvEyeColor)
        val tvHairColor: TextView = itemView.findViewById(R.id.tvHairColor)
        val tvRace: TextView = itemView.findViewById(R.id.tvRace)
        val tvBloodType: TextView = itemView.findViewById(R.id.tvBloodType)
        val tvRecipientType: TextView = itemView.findViewById(R.id.tvRecipientType)
        val btnMatch: Button = itemView.findViewById(R.id.btnMatch)
        val btnUnMatch: Button = itemView.findViewById(R.id.btnUnMatch)
        val btnViewProfile: Button = itemView.findViewById(R.id.btnViewProfile)
        val btnMessage: Button = itemView.findViewById(R.id.btnMessage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipientViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recipient_card, parent, false)
        return RecipientViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecipientViewHolder, position: Int) {
        val recipient = recipients[position]
        // Here we load the image using Glide
        if (!recipient.imagePath.isNullOrEmpty()) {
            Glide.with(holder.itemView.context)
                .load(recipient.imagePath)
                .into(holder.recipientImage)
        } else {
            holder.recipientImage.setImageResource(R.drawable.default_avatar)
        }
        holder.tvEyeColor.text = "Eye Color: ${recipient.eyeColor ?: "N/A"}"
        holder.tvHairColor.text = "Hair Color: ${recipient.hairColor ?: "N/A"}"
        holder.tvRace.text = "Race: ${recipient.race ?: "N/A"}"
        holder.tvBloodType.text = "Blood Type: ${recipient.bloodType ?: "N/A"}"
        holder.tvRecipientType.text = "Recipient Type: ${recipient.recipientType ?: "N/A"}"

        // Set up button click listeners
        holder.btnMatch.setOnClickListener {
            Log.d("RecipientAdapter", "Match button clicked for recipient: $recipient")
            listener.onMatchClicked(recipient)
        }

        holder.btnUnMatch.setOnClickListener {
            Log.d("RecipientAdapter", "Unmatch button clicked for recipient: $recipient")
            listener.onUnMatchClicked(recipient)
        }

        holder.btnViewProfile.setOnClickListener {
            listener.onViewProfileClicked(recipient)

        }

        holder.btnMessage.setOnClickListener {
            val intent = Intent(holder.itemView.context, MessageActivity::class.java).apply {
                putExtra("receiverId", recipient.userId ?: -1L)
                putExtra("receiverName", recipient.username ?: "Unknown")
                putExtra("receiverProfileImageUrl", recipient.imagePath)
            }
            holder.itemView.context.startActivity(intent)
        }

        when (mode) {
            Mode.DEFAULT -> {
                holder.btnMatch.visibility = View.VISIBLE
                holder.btnUnMatch.visibility = View.VISIBLE
                holder.btnViewProfile.visibility = View.VISIBLE
                holder.btnMessage.visibility = View.GONE
            }
            Mode.MESSAGE_ONLY -> {
                holder.btnMatch.visibility = View.GONE
                holder.btnUnMatch.visibility = View.GONE
                holder.btnViewProfile.visibility = View.GONE
                holder.btnMessage.visibility = View.VISIBLE
            }
        }
    }

    override fun getItemCount(): Int = recipients.size

    fun updateList(newList: List<RecipientProfile>) {
        recipients = newList
        notifyDataSetChanged()
    }
}