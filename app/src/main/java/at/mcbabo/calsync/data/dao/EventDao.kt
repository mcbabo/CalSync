package at.mcbabo.calsync.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import at.mcbabo.calsync.data.model.Event
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {
    @Query("SELECT * FROM events WHERE calendarId = :calendarId")
    fun getEventsForCalendar(calendarId: Long): Flow<List<Event>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvents(events: List<Event>)

    @Delete
    suspend fun deleteEvent(event: Event)

    @Query("DELETE FROM events WHERE calendarId = :calendarId")
    suspend fun deleteEventsForCalendar(calendarId: Long)
}