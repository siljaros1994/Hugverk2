package `is`.hbv601.hugverk2.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import `is`.hbv601.hugverk2.R
import `is`.hbv601.hugverk2.model.RecipientProfile

class RecipientAdapter(
    private var recipientList: List<RecipientProfile>,
    private val onItemClick: (RecipientProfile) -> Unit
) : RecyclerView.Adapter<RecipientAdapter.RecipientViewHolder>() {

    class RecipientViewHolder(view: View, private val onItemClick: (RecipientProfile) -> Unit) :
        RecyclerView.ViewHolder(view) {
        private val usernameTextView: TextView = view.findViewById(R.id.usernameTextView)
        private val userTypeTextView: TextView = view.findViewById(R.id.userTypeTextView)

        fun bind(recipient: RecipientProfile) {
            val username = recipient.user?.username ?: "Unknown"  // Extract username safely
            userTypeTextView.text = "Recipient"
            itemView.setOnClickListener { onItemClick(recipient) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipientViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
        return RecipientViewHolder(view, onItemClick)
    }

    override fun onBindViewHolder(holder: RecipientViewHolder, position: Int) {
        holder.bind(recipientList[position])
    }

    override fun getItemCount(): Int = recipientList.size

    fun updateList(newList: List<RecipientProfile>) {
        recipientList = newList
        notifyDataSetChanged()
    }
}