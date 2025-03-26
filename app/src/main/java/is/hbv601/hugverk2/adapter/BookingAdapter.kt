package `is`.hbv601.hugverk2.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import `is`.hbv601.hugverk2.R
import `is`.hbv601.hugverk2.models.BookingDTO


class BookingAdapter (
    private val bookings: List<BookingDTO>,
    private val listener: OnBookingClickListener
    ):RecyclerView.Adapter<BookingAdapter.ViewHolder>() {

        interface OnBookingClickListener {
        fun onConfirmClicked(booking: BookingDTO)
        fun onCancelClicked(booking: BookingDTO)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val bookingDetails: TextView = itemView.findViewById(R.id.bookingDetailsTextView)
        val confirmButton: Button = itemView.findViewById(R.id.btn_confirm)
        val cancelButton: Button = itemView.findViewById(R.id.btn_cancel)

        fun bind(booking: BookingDTO, listener: OnBookingClickListener) {
            bookingDetails.text = "Donor ID: ${booking.donorId} - Date: ${booking.date} - Time: ${booking.time}"
            confirmButton.setOnClickListener { listener.onConfirmClicked(booking) }
            cancelButton.setOnClickListener { listener.onCancelClicked(booking) }
        }

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_booking, parent, false)
        return ViewHolder(view)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val booking = bookings[position]

        holder.bind(booking, listener)
        //holder.bookingDetails.text = "Donor ID: ${booking.donorId} - Date: ${booking.date} - Time: ${booking.time}"

    }
    override fun getItemCount(): Int {
        return bookings.size
    }

}