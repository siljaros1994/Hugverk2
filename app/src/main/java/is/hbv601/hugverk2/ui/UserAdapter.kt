package `is`.hbv601.hugverk2.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import `is`.hbv601.hugverk2.R
import `is`.hbv601.hugverk2.model.UserDTO

class UserAdapter(
    private val userList: MutableList<UserDTO>,
    private val onDeleteClick: (String, Int) -> Unit
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val usernameTextView: TextView = view.findViewById(R.id.usernameTextView)
        val userTypeTextView: TextView = view.findViewById(R.id.userTypeTextView)
        val deleteButton: Button = view.findViewById(R.id.deleteUserButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = userList[position]
        holder.usernameTextView.text = user.username
        holder.userTypeTextView.text = user.userType


        holder.deleteButton.setOnClickListener {
            onDeleteClick(user.username, position)
        }
    }

    override fun getItemCount(): Int = userList.size
}
