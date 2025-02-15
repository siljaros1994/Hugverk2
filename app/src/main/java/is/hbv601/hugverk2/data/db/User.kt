package `is`.hbv601.hugverk2.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey val id: Long,
    val username: String,
    val email: String,
    val password: String,
    val userType: String
)