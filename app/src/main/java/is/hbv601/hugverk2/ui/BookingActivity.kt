package `is`.hbv601.hugverk2.ui

import android.content.Intent
import android.widget.*
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import `is`.hbv601.hbv601.hugverk2.data.api.RetrofitClient
import `is`.hbv601.hugverk2.R
import `is`.hbv601.hugverk2.model.DonorProfile
import `is`.hbv601.hugverk2.models.BookingDTO
import `is`.hbv601.hugverk2.adapter.BookingAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class BookingActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private var userType: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        userType = sharedPreferences.getString("user_type", "unknown") ?: "unknown"

        if (userType == "recipient") {
            setContentView(R.layout.activity_booking_recipient)
            setupRecipientBooking()
        } else if (userType == "donor") {
            setContentView(R.layout.activity_booking_donor)
            setupDonorBooking()
        } else {
            Toast.makeText(this, "Unknown user type", Toast.LENGTH_LONG).show()
            finish()
            //return
        }

        //Initialize Navigation Drawer
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        // Initialize Toolbar and Drawer Toggle
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        //navigationView.setNavigationItemSelectedListener(this)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> startActivity(Intent(this, RecipientHomeActivity::class.java))
            R.id.nav_favorites -> startActivity(Intent(this, FavoriteActivity::class.java))
            R.id.nav_messages -> startActivity(Intent(this, MessageListActivity::class.java))
            R.id.nav_matches -> startActivity(Intent(this, RecipientMatchesActivity::class.java))
            R.id.nav_booking -> {
                Toast.makeText(this, "Booking clicked", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, BookingActivity::class.java))
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun setupDonorBooking() {
        val recyclerView: RecyclerView = findViewById(R.id.appointmentsRecyclerView)
        loadPendingAppointments(recyclerView)
    }

    private fun setupRecipientBooking() {
        val donorSpinner: Spinner = findViewById(R.id.donorSpinner)
        val dateInput: EditText = findViewById(R.id.dateInput)
        val timeInput: EditText = findViewById(R.id.timeInput)
        val bookButton: Button = findViewById(R.id.bookButton)

        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val recipientId = sharedPreferences.getLong("user_id", -1)

        if (recipientId == -1L) {
            Toast.makeText(this, "Recipient ID not found!", Toast.LENGTH_SHORT).show()
            return
        }

        RetrofitClient.getInstance().getRecipientMatches()
            .enqueue(object : Callback<List<DonorProfile>> {
                override fun onResponse(
                    call: Call<List<DonorProfile>>,
                    response: Response<List<DonorProfile>>
                ) {
                    if (response.isSuccessful) {
                        val donors = response.body() ?: emptyList()
                        if (donors.isEmpty()) {
                            Toast.makeText(
                                this@BookingActivity,
                                "No matched donors found",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            val donorNames = donors.map { "Donor ${it.donorProfileId}" }
                            val donorIds = donors.map { it.donorProfileId!! }

                            val adapter = ArrayAdapter(
                                this@BookingActivity,
                                android.R.layout.simple_spinner_dropdown_item,
                                donorNames
                            )
                            donorSpinner.adapter = adapter

                            donorSpinner.onItemSelectedListener =
                                object : AdapterView.OnItemSelectedListener {
                                    override fun onItemSelected(
                                        parent: AdapterView<*>?,
                                        view: View?,
                                        position: Int,
                                        id: Long
                                    ) {
                                        donorSpinner.tag = donorIds[position]
                                    }

                                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                                }
                        }
                    }
                }

                override fun onFailure(call: Call<List<DonorProfile>>, t: Throwable) {
                    Toast.makeText(this@BookingActivity, "Error: ${t.message}", Toast.LENGTH_SHORT)
                        .show()
                }
            })

        bookButton.setOnClickListener {
            val date = dateInput.text.toString()
            val time = timeInput.text.toString()
            val selectedDonorId = donorSpinner.tag as? Long

            if (selectedDonorId != null) {
                bookAppointment(selectedDonorId, date, time)
            } else {
                Toast.makeText(this, "Please select a donor", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun bookAppointment(donorId: Long, date: String, time: String) {
        val recipientId = getSharedPreferences("user_prefs", MODE_PRIVATE).getLong("user_id", -1)
        if (recipientId == -1L) {
            Toast.makeText(this, "Recipient ID not found!", Toast.LENGTH_SHORT).show()
            return
        }

        val bookingRequest = BookingDTO(null, donorId, recipientId, date, time, false, "Pending")

        RetrofitClient.getInstance().bookAppointment(bookingRequest)
            .enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Toast.makeText(
                            this@BookingActivity,
                            "Appointment booked!",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(this@BookingActivity, "Booking failed!", Toast.LENGTH_SHORT)
                            .show()
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(this@BookingActivity, "Error: ${t.message}", Toast.LENGTH_SHORT)
                        .show()
                }
            })
    }

    private fun loadPendingAppointments(recyclerView: RecyclerView) {
        val donorId = getSharedPreferences("user_prefs", MODE_PRIVATE).getLong("user_id", -1)
        if (donorId == -1L) {
            Toast.makeText(this, "Donor ID not found!", Toast.LENGTH_SHORT).show()
            return
        }

        RetrofitClient.getInstance().getPendingAppointments(donorId)
            .enqueue(object : Callback<List<BookingDTO>> {
                override fun onResponse(
                    call: Call<List<BookingDTO>>,
                    response: Response<List<BookingDTO>>
                ) {
                    if (response.isSuccessful) {
                        val bookings = response.body() ?: emptyList()
                        recyclerView.layoutManager = LinearLayoutManager(this@BookingActivity)
                        recyclerView.adapter = BookingAdapter(
                            bookings,
                            object : BookingAdapter.OnBookingClickListener {
                                override fun onConfirmClicked(booking: BookingDTO) {
                                    confirmBooking(booking.id!!)
                                }

                                override fun onCancelClicked(booking: BookingDTO) {
                                    cancelBooking(booking.id!!)
                                }
                            })
                    }
                }

                override fun onFailure(call: Call<List<BookingDTO>>, t: Throwable) {
                    Toast.makeText(this@BookingActivity, "Error: ${t.message}", Toast.LENGTH_SHORT)
                        .show()
                }
            })
    }
    private fun confirmBooking(bookingId: Long) {
        RetrofitClient.getInstance().confirmAppointment(bookingId).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@BookingActivity, "Appointment confirmed!", Toast.LENGTH_SHORT).show()
                    loadPendingAppointments(findViewById(R.id.appointmentsRecyclerView)) // Refresh the list
                } else {
                    Toast.makeText(this@BookingActivity, "Failed to confirm appointment", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@BookingActivity, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun cancelBooking(bookingId: Long) {
        RetrofitClient.getInstance().cancelAppointment(bookingId).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@BookingActivity, "Appointment canceled!", Toast.LENGTH_SHORT).show()
                    loadPendingAppointments(findViewById(R.id.appointmentsRecyclerView)) // Refresh the list
                } else {
                    Toast.makeText(this@BookingActivity, "Failed to cancel appointment", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@BookingActivity, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


}

/*
class BookingActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private var userType: String = ""
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Retrieve userType from SharedPreferences
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        userType = sharedPreferences.getString("user_type", "unknown") ?: "unknown"

        // Load the correct XML file based on user type

        if (userType == "recipient") {
            setContentView(R.layout.activity_booking_recipient)
            setupRecipientBooking()
        } else if (userType == "donor") {
            setContentView(R.layout.activity_booking_donor)
            setupDonorBooking()
        } else {
            Toast.makeText(this, "Unknown user type", Toast.LENGTH_LONG).show()
            finish()
        }



        // Initialize Navigation Drawer
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        // Initialize Toolbar and Drawer Toggle
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

    }

    //The settings for the navigation menu
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
            when (item.itemId) {
                R.id.nav_home -> {
                    val intent = Intent(this, RecipientHomeActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                R.id.nav_favorites -> {
                    val intent = Intent(this, FavoriteActivity::class.java)
                    startActivity(intent)
                }
                R.id.nav_messages -> {
                    val intent = Intent(this, MessageListActivity::class.java)
                    startActivity(intent)
                }
                R.id.nav_matches -> {
                    val intent = Intent(this, RecipientMatchesActivity::class.java)
                    startActivity(intent)
                }
                R.id.nav_booking -> {
                    Toast.makeText(this, "Booking clicked", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, BookingActivity::class.java)
                    startActivity(intent)
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            return true
    }

       //Setup UI for recipient users
        private fun setupRecipientBooking() {
        val donorSpinner: Spinner = findViewById(R.id.donorSpinner)
        val dateInput: EditText = findViewById(R.id.dateInput)
        val timeInput: EditText = findViewById(R.id.timeInput)
        val bookButton: Button = findViewById(R.id.bookButton)

        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val recipientId = sharedPreferences.getLong("user_id", -1)

        if (recipientId == -1L) {
            Toast.makeText(this, "Recipient ID not found!", Toast.LENGTH_SHORT).show()
            return
        }

        //Fetch matched donors using getRecipientMatches()
        RetrofitClient.getInstance().getRecipientMatches().enqueue(object : Callback<List<DonorProfile>> {
            override fun onResponse(call: Call<List<DonorProfile>>, response: Response<List<DonorProfile>>) {
                if (response.isSuccessful) {
                    val donors = response.body() ?: emptyList()
                    if (donors.isEmpty()) {
                        Toast.makeText(this@BookingActivity, "No matched donors found", Toast.LENGTH_SHORT).show()
                    } else {
                        val donorNames = donors.map { "Donor ${it.donorProfileId}" }
                        val donorIds = donors.map { it.donorProfileId!! }

                        val adapter = ArrayAdapter(this@BookingActivity, android.R.layout.simple_spinner_dropdown_item, donorNames)
                        donorSpinner.adapter = adapter

                        donorSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                                val selectedDonorId = donorIds[position]
                                donorSpinner.tag = selectedDonorId
                            }

                            override fun onNothingSelected(parent: AdapterView<*>?) {}
                        }
                    }
                } else {
                    Toast.makeText(this@BookingActivity, "Failed to fetch matched donors", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<DonorProfile>>, t: Throwable) {
                Toast.makeText(this@BookingActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
        bookButton.setOnClickListener {
            val date = dateInput.text.toString()
            val time = timeInput.text.toString()
            val selectedDonorId = donorSpinner.tag as? Long

            if (selectedDonorId != null) {
                Log.d("BookingActivity", "Calling API to book an appointment with donorId: $selectedDonorId and recipientId: $recipientId")
                bookAppointment(selectedDonorId, date, time)
            } else {
                Log.e("BookingActivity", "No donor selected for booking")
                Toast.makeText(this, "Please select a donor", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun bookAppointment(donorId: Long, date: String, time: String) {
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val recipientId = sharedPreferences.getLong("user_id", -1)

        if (recipientId == -1L) {
            Toast.makeText(this, "Recipient ID not found!", Toast.LENGTH_SHORT).show()
            return
        }

        // Log the cookies before making the API request
        val cookies = RetrofitClient.getCookieString()
        Log.d("BookingActivity", "Cookies before booking request: $cookies")


        val bookingRequest = BookingDTO(
            id = null, //Set id to recipientId if needed, else null
            donorId = donorId,
            recipientId = recipientId,
            date = date,
            time = time,
            confirmed = false,
            status = "Pending"
        )

        //Log before making the API call (debugging 401 authorization)
        Log.d("BookingActivity", "Sending booking request: $bookingRequest")
        //Log.d("BookingActivity", "Stored cookies before request: ${RetrofitClient.getCookieString()}")


        RetrofitClient.getInstance().bookAppointment(bookingRequest).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                Log.d("BookingActivity", "Response received. Code: ${response.code()}")
                if (response.isSuccessful) {
                    Toast.makeText(this@BookingActivity, "Appointment booked!", Toast.LENGTH_SHORT).show()
                } else {
                    Log.d("BookingActivity", "Booking failed. Response code: ${response.code()}")
                    Log.d("BookingActivity", "Response body: ${response.errorBody()?.string()}")
                    Toast.makeText(this@BookingActivity, "Booking failed!", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("BookingActivity", "Error: ${t.message}", t)
                Toast.makeText(this@BookingActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    //Setup UI for Donor users
    private fun setupDonorBooking() {
        val recyclerView: RecyclerView = findViewById(R.id.appointmentsRecyclerView)
        loadPendingAppointments(recyclerView)
    }


    private fun loadPendingAppointments(recyclerView: RecyclerView) {
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val donorId = sharedPreferences.getLong("user_id", -1)
        if (donorId == -1L) {
            Toast.makeText(this, "Donor ID not found!", Toast.LENGTH_SHORT).show()
            return
        }

        RetrofitClient.getInstance().getPendingAppointments(donorId)
            .enqueue(object : Callback<List<BookingDTO>> {
                override fun onResponse(call: Call<List<BookingDTO>>, response: Response<List<BookingDTO>>) {
                    if (response.isSuccessful) {
                        val bookings = response.body() ?: emptyList()

                        if (bookings.isEmpty()) {
                            Toast.makeText(this@BookingActivity, "No pending bookings found", Toast.LENGTH_SHORT).show()
                        }
                        recyclerView.layoutManager = LinearLayoutManager(this@BookingActivity)
                        recyclerView.adapter = BookingAdapter(bookings, object : BookingAdapter.OnBookingClickListener {
                            override fun onConfirmClicked(booking: BookingDTO) {
                                confirmBooking(booking.id!!)
                            }

                            override fun onCancelClicked(booking: BookingDTO) {
                                cancelBooking(booking.id!!)
                            }
                        })
                    } else {
                        Toast.makeText(this@BookingActivity, "Failed to load appointments", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<List<BookingDTO>>, t: Throwable) {
                    Toast.makeText(this@BookingActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }


    private fun confirmBooking(bookingId: Long) {
        RetrofitClient.getInstance().confirmBooking(bookingId).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@BookingActivity, "Appointment confirmed!", Toast.LENGTH_SHORT).show()
                    loadPendingAppointments(findViewById(R.id.appointmentsRecyclerView))
                } else {
                    Toast.makeText(this@BookingActivity, "Failed to confirm", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@BookingActivity, "Network error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun cancelBooking(bookingId: Long) {
        RetrofitClient.getInstance().cancelBooking(bookingId).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@BookingActivity, "Appointment canceled!", Toast.LENGTH_SHORT).show()
                    loadPendingAppointments(findViewById(R.id.appointmentsRecyclerView))
                } else {
                    Toast.makeText(this@BookingActivity, "Failed to cancel", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@BookingActivity, "Network error", Toast.LENGTH_SHORT).show()
            }
        })
    }

}

*/




