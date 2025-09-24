package at.mcbabo.calsync.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import at.mcbabo.calsync.R
import at.mcbabo.calsync.util.SystemCalendarEvent
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun EventListItem(event: SystemCalendarEvent) {
    val formatter = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

    val description = if (event.allDay) {
        stringResource(R.string.all_day_event)
    } else {
        if (isMidnight(event.start ?: 0) && isMidnight(event.end ?: 0)) {
            stringResource(R.string.all_day_event)
        }

        val start = if (event.start != null) {
            formatter.format(event.start)
        } else {
            stringResource(R.string.unknown)
        }

        val end = if (event.end != null) {
            formatter.format(event.end)
        } else {
            stringResource(R.string.unknown)
        }

        stringResource(R.string.start_end, start, end)
    }

    PreferenceItem(
        title = event.title ?: stringResource(R.string.no_title),
        description = description,
        onClick = { }
    )
}

@Preview
@Composable
fun EventListItemPreview() {
    EventListItem(
        event = SystemCalendarEvent(
            id = 1L,
            title = "Sample Event",
            allDay = false,
            start = 1622527200000L,
            end = 1622530800000L,
            timezone = "UTC",
        )
    )

    EventListItem(
        event = SystemCalendarEvent(
            id = 1L,
            title = "Sample Event",
            allDay = false,
            start = 1622527200000L,
            end = 1622530800000L,
            timezone = "UTC",
        )
    )
}

fun isMidnight(timeMillis: Long): Boolean {
    val cal = Calendar.getInstance().apply { timeInMillis = timeMillis }
    return cal.get(Calendar.HOUR_OF_DAY) == 0 &&
            cal.get(Calendar.MINUTE) == 0
}
