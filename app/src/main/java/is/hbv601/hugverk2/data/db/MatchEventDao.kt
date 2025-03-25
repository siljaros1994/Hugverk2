package `is`.hbv601.hugverk2.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MatchEventDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(matchEvent: MatchEvent): Long

    @Query("SELECT * FROM match_events WHERE recipientId = :recipientId")
    suspend fun getMatchEventsForRecipient(recipientId: Long): List<MatchEvent>

    @Query("DELETE FROM match_events WHERE recipientId = :recipientId")
    suspend fun deleteEventsForRecipient(recipientId: Long): Int
}
