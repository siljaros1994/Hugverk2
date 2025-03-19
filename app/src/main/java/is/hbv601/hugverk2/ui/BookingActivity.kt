package `is`.hbv601.hugverk2.ui

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import `is`.hbv601.hbv601.hugverk2.data.api.RetrofitClient
import `is`.hbv601.hugverk2.R
import `is`.hbv601.hugverk2.models.BookingDTO
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BookingActivity : AppCompatActivity() {
    //these variables make sure that any recipient and donor can book (after matching)
    private var donorId: Long = -1
    private var recipientId: Long = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking)

        //Retrieve donorId and recipientId from Intent extras
        donorId = intent.getLongExtra("EXTRA_DONOR_ID", -1)
        recipientId = intent.getLongExtra("EXTRA_RECIPIENT_ID", -1)

        if (donorId == -1L || recipientId == -1L) {
            Toast.makeText(this, "Error: Invalid donor or recipient", Toast.LENGTH_LONG).show()
            finish() // Close activity if IDs are missing
            return
        }

        // Initialize UI elements
        val bookButton: Button = findViewById(R.id.bookButton)
        val dateInput: EditText = findViewById(R.id.dateInput)
        val timeInput: EditText = findViewById(R.id.timeInput)

        bookButton.setOnClickListener {
            val date = dateInput.text.toString()
            val time = timeInput.text.toString()
            bookAppointment(date,time)
        }

        //val bookButton: Button = findViewById(R.id.bookButton)
        //bookButton.setOnClickListener {
        //    bookAppointment()
        //}
    }

    private fun bookAppointment(date: String, time: String) {
        val bookingRequest = BookingDTO(
            id = null,
            donorId = donorId,
            recipientId = recipientId,
            date = date,
            time = time,
            confirmed = false,
            status = "Pending"
        )

        RetrofitClient.getInstance().bookAppointment(bookingRequest).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@BookingActivity, "Appointment booked!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@BookingActivity, "Booking failed!", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@BookingActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}