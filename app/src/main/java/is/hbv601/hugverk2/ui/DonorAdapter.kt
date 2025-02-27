package `is`.hbv601.hugverk2.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import `is`.hbv601.hugverk2.R
import `is`.hbv601.hugverk2.model.Donor

class DonorAdapter(private val donors: List<Donor>) : RecyclerView.Adapter<DonorAdapter.DonorViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DonorViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_donor, parent, false)
        return DonorViewHolder(view)
    }

    override fun onBindViewHolder(holder: DonorViewHolder, position: Int) {
        val donor = donors[position]
        holder.name.text = "Donor Type: ${donor.donorType}"
        holder.age.text = "Age: ${donor.age}"
        holder.eyeColor.text = "Eye Color: ${donor.eyeColor}"
        holder.hairColor.text = "Hair Color: ${donor.hairColor}"
    }

    override fun getItemCount(): Int = donors.size

    class DonorViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.donorType)
        val age: TextView = view.findViewById(R.id.donorAge)
        val eyeColor: TextView = view.findViewById(R.id.donorEyeColor)
        val hairColor: TextView = view.findViewById(R.id.donorHairColor)
    }
}