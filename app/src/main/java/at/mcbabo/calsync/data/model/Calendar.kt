package at.mcbabo.calsync.data.model

import android.net.Uri
import android.provider.CalendarContract.Calendars
import androidx.core.content.contentValuesOf
import androidx.room.Entity
import androidx.room.PrimaryKey
import at.mcbabo.calsync.util.ACCOUNT_NAME
import at.mcbabo.calsync.util.ACCOUNT_TYPE
import java.util.Date
import java.util.TimeZone

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

    val reminderMinutes: Int? = null,

    val errorMessage: String? = null,
    val lastModified: Date = Date(),
    val lastSync: Date? = null,

    // Optional user agent to use when fetching the calendar
    val userAgent: String? = null,

    // Credentials (if any) associated with this calendar
    val username: String? = null,
    val password: String? = null
) {
    fun toCalendarProperties() =
        contentValuesOf(
            Calendars.NAME to name,
            Calendars.CALENDAR_DISPLAY_NAME to name,
            Calendars.CALENDAR_COLOR to color,

            Calendars.VISIBLE to 1,
            Calendars.SYNC_EVENTS to 1,

            Calendars.CALENDAR_TIME_ZONE to TimeZone.getDefault().id,
            Calendars.CALENDAR_ACCESS_LEVEL to Calendars.CAL_ACCESS_OWNER,

            Calendars.OWNER_ACCOUNT to ACCOUNT_NAME,
            Calendars.ACCOUNT_NAME to ACCOUNT_NAME,
            Calendars.ACCOUNT_TYPE to ACCOUNT_TYPE
        )
}

enum class SyncStrategy {
    MERGE,
    REPLACE
}
