package at.mcbabo.calsync.util

import android.accounts.Account
import android.accounts.AccountManager
import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.provider.CalendarContract
import android.util.Log
import at.mcbabo.calsync.data.model.Calendar
import at.mcbabo.calsync.data.model.Event
import biweekly.component.VEvent
import java.util.TimeZone
import javax.inject.Inject

const val ACCOUNT_NAME = "CalSync"
const val ACCOUNT_TYPE = "at.mcbabo.calsync"

data class SystemCalendarEvent(
    val id: Long,
    val title: String?,
    val allDay: Boolean,
    val start: Long?,
    val end: Long?,
    val timezone: String?
)

data class SystemCalendar(
    val id: Long,
    val displayName: String,
    val accountName: String,
    val accountType: String
)

class SystemCalendarService @Inject constructor(
    private val context: Context
) {
    fun getAccount(): Account {
        val am = AccountManager.get(context)
        var existingAccount = am.getAccountsByType(ACCOUNT_TYPE).firstOrNull()

        if (existingAccount == null) {
            val newAccount = Account(ACCOUNT_NAME, ACCOUNT_TYPE)
            val added = am.addAccountExplicitly(newAccount, null, null)
            if (added) {
                ContentResolver.setIsSyncable(newAccount, CalendarContract.AUTHORITY, 1)
                ContentResolver.setSyncAutomatically(newAccount, CalendarContract.AUTHORITY, true)
            }
            existingAccount = newAccount
        }

        return existingAccount
    }

    fun getSystemCalendars(): List<SystemCalendar> {
        val projection = arrayOf(
            CalendarContract.Calendars._ID,
            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
            CalendarContract.Calendars.ACCOUNT_NAME,
            CalendarContract.Calendars.ACCOUNT_TYPE
        )

        val selection = "${CalendarContract.Calendars.ACCOUNT_TYPE} = ?"
        val selectionArgs = arrayOf(ACCOUNT_TYPE)

        val uri = CalendarContract.Calendars.CONTENT_URI
        val calendars = mutableListOf<SystemCalendar>()

        context.contentResolver.query(uri, projection, selection, selectionArgs, null)?.use { cursor ->
            val idCol = cursor.getColumnIndexOrThrow(CalendarContract.Calendars._ID)
            val nameCol = cursor.getColumnIndexOrThrow(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME)
            val accNameCol = cursor.getColumnIndexOrThrow(CalendarContract.Calendars.ACCOUNT_NAME)
            val accTypeCol = cursor.getColumnIndexOrThrow(CalendarContract.Calendars.ACCOUNT_TYPE)

            while (cursor.moveToNext()) {
                calendars.add(
                    SystemCalendar(
                        id = cursor.getLong(idCol),
                        displayName = cursor.getString(nameCol),
                        accountName = cursor.getString(accNameCol),
                        accountType = cursor.getString(accTypeCol)
                    )
                )
            }
        }

        return calendars
    }

    fun createCalendar(calendar: Calendar): Long {
        val values = calendar.toCalendarProperties()

        val insertUri = CalendarContract.Calendars.CONTENT_URI.buildUpon()
            .appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
            .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, ACCOUNT_NAME)
            .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, ACCOUNT_TYPE)
            .build()

        val resultUri = context.contentResolver.insert(insertUri, values)
            ?: throw IllegalStateException("Failed to insert system calendar")

        return ContentUris.parseId(resultUri)
    }

    fun updateCalendar(calendar: Calendar): Int {
        try {
            val values = ContentValues().apply {
                put(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, calendar.name)
                put(CalendarContract.Calendars.CALENDAR_COLOR, calendar.color)
            }

            val updateUri = ContentUris.withAppendedId(
                CalendarContract.Calendars.CONTENT_URI,
                calendar.calendarId!!
            ).buildUpon()
                .appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, ACCOUNT_NAME)
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, ACCOUNT_TYPE)
                .build()

            return context.contentResolver.update(
                updateUri,
                values,
                null,
                null
            )
        } catch (_: Exception) {
            return 0
        }
    }

    fun deleteEvents(calendarId: Long) {
        val selection = "${CalendarContract.Events.CALENDAR_ID} = ?"
        val selectionArgs = arrayOf(calendarId.toString())

        context.contentResolver.delete(
            CalendarContract.Events.CONTENT_URI,
            selection,
            selectionArgs
        )
    }

    fun deleteEvents(calendarId: Long, eventIds: List<Long>) {
        if (eventIds.isEmpty()) return

        val placeholders = eventIds.joinToString(",") { "?" }
        val selection =
            "${CalendarContract.Events.CALENDAR_ID} = ? AND ${CalendarContract.Events._ID} IN ($placeholders)"
        val selectionArgs = arrayOf(calendarId.toString()) + eventIds.map { it.toString() }

        context.contentResolver.delete(
            CalendarContract.Events.CONTENT_URI,
            selection,
            selectionArgs
        )
    }

    fun updateEvents(calendarId: Long, updates: List<Pair<Event, VEvent>>) {
        for ((local, vevent) in updates) {
            val eventId = local.eventId
            val uri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventId)

            val isAllDay = vevent.isAllDay()
            val startMillis = vevent.dateStart?.value?.time ?: continue
            val endMillis = vevent.dateEnd?.value?.time ?: continue

            val values = ContentValues().apply {
                put(CalendarContract.Events.CALENDAR_ID, calendarId)
                put(CalendarContract.Events.TITLE, vevent.summary?.value ?: "Untitled Event")
                if (isAllDay) {
                    put(CalendarContract.Events.ALL_DAY, 1)
                    put(CalendarContract.Events.DTSTART, startMillis)
                    put(CalendarContract.Events.DTEND, endMillis)
                    put(CalendarContract.Events.EVENT_TIMEZONE, "UTC")
                } else {
                    put(CalendarContract.Events.ALL_DAY, 0)
                    put(CalendarContract.Events.DTSTART, startMillis)
                    put(CalendarContract.Events.DTEND, endMillis)
                    put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().id)
                }
            }

            context.contentResolver.update(uri, values, null, null)
        }
    }

    fun insertEvents(calendarId: Long, events: List<VEvent>) {
        for (vevent in events) {
            try {
                val isAllDay = vevent.isAllDay()

                val startMillis = vevent.dateStart?.value?.time ?: continue
                val endMillis = vevent.dateEnd?.value?.time ?: continue

                val valuesEvent = ContentValues().apply {
                    put(CalendarContract.Events.CALENDAR_ID, calendarId)
                    put(CalendarContract.Events.TITLE, vevent.summary?.value ?: "Untitled Event")
                    put(CalendarContract.Events.EVENT_LOCATION, vevent.location?.value)
                    put(CalendarContract.Events.DESCRIPTION, vevent.description?.value)
                    put(CalendarContract.Events.ALL_DAY, isAllDay)
                    put(CalendarContract.Events.DTSTART, startMillis)
                    put(CalendarContract.Events.DTEND, endMillis)
                    put(
                        CalendarContract.Events.EVENT_TIMEZONE,
                        TimeZone.getTimeZone(vevent.dateStart.parameters?.timezoneId ?: TimeZone.getDefault().id).id
                    )
                }

                context.contentResolver.insert(CalendarContract.Events.CONTENT_URI, valuesEvent)
            } catch (_: Exception) {
            }
        }
    }

    fun getSystemCalendarEvents(calendarId: Long): List<SystemCalendarEvent> {
        val projection = arrayOf(
            CalendarContract.Events._ID,
            CalendarContract.Events.TITLE,
            CalendarContract.Events.ALL_DAY,
            CalendarContract.Events.DTSTART,
            CalendarContract.Events.DTEND,
            CalendarContract.Events.EVENT_TIMEZONE
        )

        val selection = "${CalendarContract.Events.CALENDAR_ID} = ?"
        val selectionArgs = arrayOf(calendarId.toString())

        val cursor = context.contentResolver.query(
            CalendarContract.Events.CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            null
        )

        val events = mutableListOf<SystemCalendarEvent>()
        cursor?.use {
            val idIndex = it.getColumnIndexOrThrow(CalendarContract.Events._ID)
            val titleIndex = it.getColumnIndexOrThrow(CalendarContract.Events.TITLE)
            val allDay = it.getColumnIndexOrThrow(CalendarContract.Events.ALL_DAY)
            val dtStartIndex = it.getColumnIndexOrThrow(CalendarContract.Events.DTSTART)
            val dtEndIndex = it.getColumnIndexOrThrow(CalendarContract.Events.DTEND)
            val tzIndex = it.getColumnIndexOrThrow(CalendarContract.Events.EVENT_TIMEZONE)

            while (it.moveToNext()) {
                events.add(
                    SystemCalendarEvent(
                        id = it.getLong(idIndex),
                        title = it.getString(titleIndex),
                        allDay = it.getInt(allDay) != 0,
                        start = if (!it.isNull(dtStartIndex)) it.getLong(dtStartIndex) else null,
                        end = if (!it.isNull(dtEndIndex)) it.getLong(dtEndIndex) else null,
                        timezone = it.getString(tzIndex)
                    )
                )
            }
        }
        return events
    }

    fun deleteCalendar(calendarId: Long) {
        try {
            var deleteUri = ContentUris.withAppendedId(
                CalendarContract.Calendars.CONTENT_URI,
                calendarId
            ).buildUpon()
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, ACCOUNT_NAME)
                .build()

            context.contentResolver.delete(deleteUri, null, null)

            deleteUri = ContentUris.withAppendedId(
                CalendarContract.Calendars.CONTENT_URI,
                calendarId
            ).buildUpon()
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, ACCOUNT_NAME)
                .build()

            context.contentResolver.delete(deleteUri, null, null)
            Result.success(Unit)
        } catch (e: Exception) {
            Log.d("CalendarRepository", "Error deleting calendar", e)
            Result.failure(e)
        }
    }

    fun deleteAllCalendars() {
        var where = "${CalendarContract.Calendars.ACCOUNT_NAME}=?"
        var args = arrayOf(ACCOUNT_NAME)

        context.contentResolver.delete(
            CalendarContract.Calendars.CONTENT_URI,
            where,
            args
        )

        where = "${CalendarContract.Calendars.ACCOUNT_TYPE}=?"
        args = arrayOf(ACCOUNT_TYPE)

        context.contentResolver.delete(
            CalendarContract.Calendars.CONTENT_URI,
            where,
            args
        )
    }
}
