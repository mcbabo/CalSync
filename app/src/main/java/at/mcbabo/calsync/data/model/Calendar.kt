package at.mcbabo.calsync.data.model

import android.net.Uri
import android.provider.CalendarContract.Calendars
import androidx.core.content.contentValuesOf
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "calendars")
data class Calendar(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val calendarId: Long? = null,
    val name: String,
    val uri: Uri,
    val eTag: String? = null,
    val syncStrategy: SyncStrategy,
    val color: Int,
    val errorMessage: String? = null,
    val lastModified: Date = Date(),
    val lastSync: Date? = null,
    val userAgent: String? = null,

    // Credentials (if any) associated with this calendar
    val username: String? = null,
    val password: String? = null
) {
    fun toCalendarProperties() = contentValuesOf(
        Calendars.NAME to name,
        Calendars.CALENDAR_DISPLAY_NAME to name,
        Calendars.CALENDAR_COLOR to color,
        Calendars.CALENDAR_ACCESS_LEVEL to Calendars.CAL_ACCESS_READ,
        Calendars.VISIBLE to 1,
        Calendars.SYNC_EVENTS to 1,
        Calendars.CALENDAR_TIME_ZONE to "UTC",
        Calendars.OWNER_ACCOUNT to "CalSync"
    )
}

enum class SyncStrategy {
    MERGE,
    REPLACE
}