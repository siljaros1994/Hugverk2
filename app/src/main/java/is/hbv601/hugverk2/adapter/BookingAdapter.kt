package `is`.hbv601.hugverk2.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import `is`.hbv601.hugverk2.R
import `is`.hbv601.hugverk2.models.BookingDTO

class BookingAdapter (private val bookings: List<BookingDTO>) :
    RecyclerView.Adapter<BookingAdapter.ViewHolder>(){
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val bookingDetails: TextView = itemView.findViewById(R.id.bookingDetailsTextView)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_booking, parent, false)
        return ViewHolder(view)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val booking = bookings[position]
        holder.bookingDetails.text = "Donor ID: ${booking.donorId} - Date: ${booking.date} - Time: ${booking.time}"
    }
    override fun getItemCount(): Int {
        return bookings.size
    }


}