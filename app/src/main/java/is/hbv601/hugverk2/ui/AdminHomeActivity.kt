package `is`.hbv601.hugverk2.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import `is`.hbv601.hugverk2.databinding.ActivityAdminHomeBinding

class AdminHomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // TODO: Add admin dashboard features here
    }
}
