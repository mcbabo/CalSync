package at.mcbabo.calsync.data.viewmodel

import android.content.Context
import android.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.mcbabo.calsync.data.model.Calendar
import at.mcbabo.calsync.data.model.SyncStrategy
import at.mcbabo.calsync.data.repository.CalendarRepository
import at.mcbabo.calsync.util.SystemCalendarEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val repository: CalendarRepository
) : ViewModel() {
    val calendars: StateFlow<List<Calendar>> =
        repository.getAllCalendars()
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _events = MutableStateFlow<List<SystemCalendarEvent>>(emptyList())
    val events: StateFlow<List<SystemCalendarEvent>> = _events

    private val _loadingCount = MutableStateFlow(0)

    val isLoading: StateFlow<Boolean> = _loadingCount
        .map { it > 0 }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun calendarById(id: Long): Flow<Calendar?> {
        return calendars.map { list -> list.firstOrNull { it.id == id } }
    }

    fun getEventsForCalendar(calendarId: Long) {
        runWithLoading(
            block = { repository.getSystemCalendarEvents(calendarId) },
            onSuccess = { _events.value =  it }
        )
    }

    fun addCalendarFromIcs(
        context: Context,
        name: String,
        uriOrUrl: String,
        color: Int = Color.BLUE,
        syncStrategy: SyncStrategy,
        userAgent: String? = null,
        username: String? = null,
        password: String? = null
    ) {
        viewModelScope.launch {

        }
        runWithLoading(
            block = {
                repository.importCalendar(
                    context = context,
                    name = name,
                    uriOrUrl = uriOrUrl,
                    color = color,
                    syncStrategy = syncStrategy,
                    userAgent = userAgent,
                    username = username,
                    password = password
                )
            },
            onSuccess = {}
        )
    }

    fun deleteCalendarCompletely(calendar: Calendar) {
        runWithLoading(
            block = { repository.deleteCalendarCompletely(calendar) },
            onSuccess = {}
        )
    }

    fun syncAllCalendars(context: Context) {
        runWithLoading(
            block = { repository.syncAllCalendars(context) },
            onSuccess = {}
        )
    }

    fun deleteAllSystemCalendars() {
        runWithLoading(
            block = { repository.deleteAllSystemCalendars() },
            onSuccess = {}
        )
    }

    private inline fun <T> runWithLoading(
        crossinline block: suspend () -> Result<T>,
        crossinline onSuccess: (T) -> Unit
    ) {
        viewModelScope.launch {
            _loadingCount.update { it + 1 }
            _error.value = null
            try {
                val result = withContext(Dispatchers.IO) { block() }
                result.fold(onSuccess) { _error.value = it.message }
            } finally {
                _loadingCount.update { it - 1 }
            }
        }
    }
}
