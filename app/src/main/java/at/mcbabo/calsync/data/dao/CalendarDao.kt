package at.mcbabo.calsync.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import at.mcbabo.calsync.data.model.Calendar
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface CalendarDao {
    @Query("SELECT * FROM calendars")
    fun getAllCalendars(): Flow<List<Calendar>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCalendar(calendar: Calendar): Long

    @Update
    suspend fun updateCalendar(calendar: Calendar)

    @Delete
    suspend fun deleteCalendar(calendar: Calendar)

    @Query("UPDATE calendars SET calendarId = :calendarId WHERE id = :id")
    suspend fun updateCalendarId(id: Long, calendarId: Long)

    @Query("UPDATE calendars SET lastSync = :lastSync WHERE id = :id")
    suspend fun updateLastSync(id: Long, lastSync: Date)
}