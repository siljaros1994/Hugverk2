package `is`.hbv601.hugverk2.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import `is`.hbv601.hugverk2.R
import `is`.hbv601.hugverk2.model.DonorProfile

class DonorAdapter(
    private var donors: List<DonorProfile>,
    private val listener: OnDonorClickListener

) : RecyclerView.Adapter<DonorAdapter.DonorViewHolder>() {

    interface OnDonorClickListener {
        fun onFavoriteClicked(donor: DonorProfile)
        fun onUnfavoriteClicked(donor: DonorProfile)
        fun onViewProfileClicked(donor: DonorProfile)
    }

    inner class DonorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val donorImage: ImageView = itemView.findViewById(R.id.donorImage)
        val tvEyeColor: TextView = itemView.findViewById(R.id.tvEyeColor)
        val tvHairColor: TextView = itemView.findViewById(R.id.tvHairColor)
        val tvRace: TextView = itemView.findViewById(R.id.tvRace)
        val tvBloodType: TextView = itemView.findViewById(R.id.tvBloodType)
        val tvDonorType: TextView = itemView.findViewById(R.id.tvDonorType)
        val btnFavorite: Button = itemView.findViewById(R.id.btnFavorite)
        val btnUnfavorite: Button = itemView.findViewById(R.id.btnUnfavorite)
        val btnViewProfile: Button = itemView.findViewById(R.id.btnViewProfile)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DonorViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.donor_card, parent, false)
        return DonorViewHolder(view)
    }

    override fun onBindViewHolder(holder: DonorViewHolder, position: Int) {
        val donor = donors[position]
        // Load donor image using Glide (or set default)
        if (!donor.imagePath.isNullOrEmpty()) {
            Glide.with(holder.itemView.context)
                .load(donor.imagePath)
                .into(holder.donorImage)
        } else {
            holder.donorImage.setImageResource(R.drawable.default_avatar)
        }
        holder.tvEyeColor.text = "Eye Color: ${donor.eyeColor ?: "N/A"}"
        holder.tvHairColor.text = "Hair Color: ${donor.hairColor ?: "N/A"}"
        holder.tvRace.text = "Race: ${donor.race ?: "N/A"}"
        holder.tvBloodType.text = "Blood Type: ${donor.bloodType ?: "N/A"}"
        holder.tvDonorType.text = "Donor Type: ${donor.donorType ?: "N/A"}"

        holder.btnFavorite.setOnClickListener {
            Log.d("FavoriteButton", "Favorite button clicked for donor id: ${donor.donorProfileId}")
            listener.onFavoriteClicked(donor)
        }

        holder.btnUnfavorite.setOnClickListener {
            Log.d("UnFavoriteButton", "UnFavorite button clicked for donor id: ${donor.donorProfileId}")
            listener.onUnfavoriteClicked(donor)
        }

        holder.btnViewProfile.setOnClickListener {
            listener.onViewProfileClicked(donor)
        }
    }

    override fun getItemCount(): Int = donors.size

    fun updateList(newList: List<DonorProfile>) {
        donors = newList
        notifyDataSetChanged()
    }
}
