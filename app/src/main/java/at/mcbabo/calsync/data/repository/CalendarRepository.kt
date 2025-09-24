package at.mcbabo.calsync.data.repository

import android.accounts.Account
import android.content.Context
import android.graphics.Color
import androidx.core.net.toUri
import at.mcbabo.calsync.data.dao.CalendarDao
import at.mcbabo.calsync.data.dao.EventDao
import at.mcbabo.calsync.data.model.Calendar
import at.mcbabo.calsync.data.model.Event
import at.mcbabo.calsync.data.model.SyncStrategy
import at.mcbabo.calsync.util.IcsFetcher
import at.mcbabo.calsync.util.SystemCalendar
import at.mcbabo.calsync.util.SystemCalendarEvent
import at.mcbabo.calsync.util.SystemCalendarService
import at.mcbabo.calsync.worker.SyncScheduler
import biweekly.Biweekly
import biweekly.component.VEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.util.Date
import javax.inject.Inject

class CalendarRepository @Inject constructor(
    private val calendarDao: CalendarDao,
    private val eventDao: EventDao,
    private val syncScheduler: SyncScheduler,
    private val icsFetcher: IcsFetcher,
    private val systemCalendarService: SystemCalendarService
) {
    fun getAllCalendars(): Flow<List<Calendar>> = calendarDao.getAllCalendars()

    suspend fun updateCalendar(calendar: Calendar) {
        calendarDao.updateCalendar(calendar)
        systemCalendarService.updateCalendar(calendar)
    }

    fun getEvents(calendarId: Long): Flow<List<Event>> {
        return eventDao.getEventsForCalendar(calendarId)
    }

    fun getSystemCalendarEvents(calendarId: Long): Result<List<SystemCalendarEvent>> {
        return try {
            val result = systemCalendarService.getSystemCalendarEvents(calendarId)
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getSystemCalendars(): Result<List<SystemCalendar>> {
        return try {
            val result = systemCalendarService.getSystemCalendars()
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun mergeEvents(calendarId: Long, newEvents: List<VEvent>) {
        val localEvents = eventDao.getEventsForCalendar(calendarId).first()
        val localMap = localEvents.associateBy { it.icsId }

        val seen = mutableSetOf<String>()
        val eventsToAdd = mutableListOf<VEvent>()
        val eventsToUpdate = mutableListOf<Pair<Event, VEvent>>()

        for (remote in newEvents) {
            val uid = remote.uid.toString()
            val local = localMap[uid]
            seen += uid

            if (local == null) {
                eventsToAdd.add(remote)
            } else if (remote.dateTimeStamp.value.after(local.updatedAt)) {
                eventsToUpdate.add(local to remote)
            }
        }

        systemCalendarService.insertEvents(calendarId, eventsToAdd)
        systemCalendarService.updateEvents(calendarId, eventsToUpdate)

        val toDelete = localEvents.filter { it.icsId !in seen }
        systemCalendarService.deleteEvents(calendarId, toDelete.map { it.eventId })
    }

    suspend fun syncCalendar(context: Context, account: Account, calendar: Calendar): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                var calId = calendar.calendarId
                if (calId == null) {
                    calId = systemCalendarService.createCalendar(account, calendar)
                    calendarDao.updateCalendarId(calendar.id, calId)
                }

                val inputStream = icsFetcher.fetch(context, calendar)

                val ical = Biweekly.parse(inputStream).first()
                    ?: throw IllegalArgumentException("Invalid ICS file")

                inputStream.close()

                when (calendar.syncStrategy) {
                    SyncStrategy.REPLACE -> {
                        systemCalendarService.deleteEvents(calId)
                        systemCalendarService.insertEvents(calId, ical.events)
                    }

                    SyncStrategy.MERGE -> {
                        mergeEvents(calId, ical.events)
                    }
                }

                calendarDao.updateLastSync(calendar.id, Date())

                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    suspend fun syncAllCalendars(context: Context): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val account = systemCalendarService.getAccount()
            val calendars = calendarDao.getAllCalendars().first()

            for (calendar in calendars) {
                syncCalendar(context, account, calendar)
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun importCalendar(
        context: Context,
        name: String,
        uriOrUrl: String,
        color: Int = Color.BLUE,
        syncStrategy: SyncStrategy,
        userAgent: String? = null,
        username: String? = null,
        password: String? = null
    ): Result<Unit> {
        return try {
            val calendar = Calendar(
                name = name,
                uri = uriOrUrl.toUri(),
                color = color,
                syncStrategy = syncStrategy,
                userAgent = userAgent,
                lastModified = Date(),
                username = username,
                password = password
            )

            calendarDao.insertCalendar(calendar)
            syncScheduler.scheduleOneTimeSync(context)

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteCalendarCompletely(calendar: Calendar): Result<Unit> {
        try {
            systemCalendarService.deleteCalendar(calendar.calendarId ?: -1L)

            val events = eventDao.getEventsForCalendar(calendar.id).first()
            events.forEach { eventDao.deleteEvent(it) }

            calendarDao.deleteCalendar(calendar)

            return Result.success(Unit)
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    fun deleteCalendarCompletely(calendarId: Long): Result<Unit> {
        try {
            systemCalendarService.deleteCalendar(calendarId)
            return Result.success(Unit)
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    fun deleteAllSystemCalendars(): Result<Unit> {
        return try {
            systemCalendarService.deleteAllCalendars()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
