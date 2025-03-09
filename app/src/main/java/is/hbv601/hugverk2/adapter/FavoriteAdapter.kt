package `is`.hbv601.hugverk2.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import `is`.hbv601.hugverk2.R
import `is`.hbv601.hugverk2.model.MyAppUser


class FavoriteAdapter(
    private val favoriteList: List<MyAppUser>,
    private val onFavoriteClick: (MyAppUser) -> Unit
): RecyclerView.Adapter<FavoriteAdapter.ViewHolder>() {

        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val tvName: TextView = itemView.findViewById(R.id.tvName) //References must match in the XML ID (matches tvName)
            val btnFavorite: Button = itemView.findViewById(R.id.btnFavorite) // Ensure this exists in XML
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_favorite, parent, false) //Ensure correct XML Layout
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val donor = favoriteList[position]
            holder.tvName.text = donor.username //Ensure that MyAppUsers has a 'username' field

            // Set button text dynamically (Favorite / Unfavorite)
            holder.btnFavorite.text = if (donor.isFavorited) "Unfavorite" else "Favorite"

            // Handle click on Favorite button
            holder.btnFavorite.setOnClickListener {
                donor.isFavorited = !donor.isFavorited //Toggle state
                notifyItemChanged(position) //Update UI
                onFavoriteClick(donor) //Call function
        }
            }

        override fun getItemCount(): Int = favoriteList.size
    }

