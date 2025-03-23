package `is`.hbv601.hugverk2.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "match_events")
data class MatchEvent(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val donorId: Long,
    val recipientId: Long,
    val timestamp: Long = System.currentTimeMillis()
)
