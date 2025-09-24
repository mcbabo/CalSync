package at.mcbabo.calsync.ui.screen

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import at.mcbabo.calsync.R
import at.mcbabo.calsync.data.viewmodel.CalendarViewModel
import at.mcbabo.calsync.ui.component.BackButton
import at.mcbabo.calsync.ui.component.DeleteDialog
import at.mcbabo.calsync.ui.component.EventListItem
import at.mcbabo.calsync.ui.component.PreferenceSubtitle
import at.mcbabo.calsync.util.SystemCalendarEvent
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarDetailScreen(
    calendarId: Long,
    onBack: () -> Unit,
    viewModel: CalendarViewModel = hiltViewModel()
) {
    val coroutineScope = rememberCoroutineScope()

    val calendar by viewModel.calendarById(calendarId).collectAsState(null)
    viewModel.getEventsForCalendar(calendar?.calendarId ?: -1L)
    val events by viewModel.events.collectAsState(emptyList())

    val formatter = SimpleDateFormat("dd.MM.yyy HH:mm:ss", Locale.getDefault())

    val lastSync = if (calendar?.lastSync != null) {
        formatter.format(calendar?.lastSync!!)
    } else {
        stringResource(R.string.unknown)
    }

    val tabs = listOf(stringResource(R.string.details), stringResource(R.string.events))
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { tabs.size })

    var showDialog by remember { mutableStateOf(false) }

    val blurRadius by animateDpAsState(
        targetValue = if (showDialog) 6.dp else 0.dp,
        animationSpec = tween(durationMillis = 100),
        label = "BlurAnimation"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(calendar?.name ?: stringResource(R.string.calendar_details)) },
                navigationIcon = {
                    BackButton { onBack() }
                },
                actions = {
                    TextButton(
                        onClick = { showDialog = true }
                    ) {
                        Text(stringResource(R.string.delete_calendar))
                    }
                }
            )
        },
        modifier = Modifier.blur(blurRadius)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            calendar?.let {
                SecondaryTabRow(
                    selectedTabIndex = pagerState.currentPage,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    tabs.forEachIndexed { index, destination ->
                        Tab(
                            selected = pagerState.currentPage == index,
                            onClick = {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(index)
                                }
                            },
                            text = {
                                Text(
                                    text = destination,
                                    color = if (pagerState.currentPage == index) MaterialTheme.colorScheme.primary else Color.Unspecified
                                )
                            }
                        )
                    }
                }

                HorizontalPager(
                    state = pagerState,
                    flingBehavior = PagerDefaults.flingBehavior(state = pagerState)
                ) { page ->
                    when (page) {
                        0 -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(vertical = 8.dp)
                            ) {
                                var textFieldHeight by remember { mutableIntStateOf(0) }
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(IntrinsicSize.Min)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .weight(2f)
                                            .fillMaxHeight()
                                    ) {
                                        PreferenceSubtitle(stringResource(R.string.name))
                                        OutlinedTextField(
                                            value = calendar?.name.toString(),
                                            onValueChange = {},
                                            label = { Text(stringResource(R.string.name)) },
                                            enabled = true,
                                            readOnly = true,
                                            modifier = Modifier
                                                .padding(16.dp, 4.dp)
                                                .onSizeChanged { textFieldHeight = it.height }
                                        )
                                    }

                                    Column(
                                        modifier = Modifier
                                            .weight(1f)
                                            .fillMaxHeight(),
                                        verticalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        PreferenceSubtitle(stringResource(R.string.color))
                                        Box(
                                            modifier = Modifier
                                                .height(with(LocalDensity.current) { textFieldHeight.toDp() })
                                                .fillMaxWidth()
                                                .padding(horizontal = 16.dp)
                                                .padding(bottom = 4.dp)
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(Color(calendar?.color ?: 0))
                                        )
                                    }
                                }

                                PreferenceSubtitle("URI")
                                OutlinedTextField(
                                    value = calendar?.uri.toString(),
                                    onValueChange = {},
                                    label = { Text("URI") },
                                    enabled = true,
                                    readOnly = true,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp, 4.dp)
                                )

                                PreferenceSubtitle(stringResource(R.string.sync_strategy))
                                OutlinedTextField(
                                    value = calendar?.syncStrategy.toString(),
                                    onValueChange = {},
                                    label = { Text(stringResource(R.string.sync_strategy)) },
                                    enabled = true,
                                    readOnly = true,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp, 4.dp)
                                )

                                PreferenceSubtitle(stringResource(R.string.last_sync))
                                OutlinedTextField(
                                    value = lastSync,
                                    onValueChange = {},
                                    label = { Text(stringResource(R.string.last_sync)) },
                                    enabled = true,
                                    readOnly = true,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp, 4.dp)
                                )

                                if (calendar?.username != null && calendar?.password != null) {
                                    PreferenceSubtitle(stringResource(R.string.username))
                                    OutlinedTextField(
                                        value = calendar?.username.toString(),
                                        onValueChange = {},
                                        label = { Text(stringResource(R.string.username)) },
                                        enabled = true,
                                        readOnly = true,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp, 4.dp)
                                    )
                                    PreferenceSubtitle(stringResource(R.string.password))
                                    OutlinedTextField(
                                        value = calendar?.password.toString(),
                                        onValueChange = {},
                                        label = { Text(stringResource(R.string.password)) },
                                        enabled = true,
                                        readOnly = true,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp, 4.dp)
                                    )
                                }

                                if (calendar?.userAgent != null) {
                                    PreferenceSubtitle(stringResource(R.string.user_agent))
                                    OutlinedTextField(
                                        value = calendar?.userAgent.toString(),
                                        onValueChange = {},
                                        label = { Text(stringResource(R.string.user_agent)) },
                                        enabled = true,
                                        readOnly = true,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp, 4.dp)
                                    )
                                }
                            }
                        }

                        1 -> {
                            val grouped = remember(events) { groupEventsByDay(events) }
                            val formatter = remember {
                                SimpleDateFormat("EEEE dd.MM.yyy", Locale.getDefault())
                            }
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(vertical = 8.dp)
                            ) {
                                grouped.toSortedMap().forEach { (date, dayEvents) ->
                                    item(key = "header_$date") {
                                        PreferenceSubtitle(
                                            text = formatter.format(date),
                                            contentPadding = PaddingValues(start = 16.dp, top = 8.dp, bottom = 8.dp)
                                        )
                                    }
                                    items(dayEvents, key = { it.id }) { event ->
                                        EventListItem(event)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        DeleteDialog(
            onDismiss = { showDialog = false },
            onConfirm = {
                calendar?.let { viewModel.deleteCalendarCompletely(it) }
                showDialog = false
                onBack()
            }
        )
    }
}


fun groupEventsByDay(
    events: List<SystemCalendarEvent>
): Map<Date, List<SystemCalendarEvent>> {
    val cal = Calendar.getInstance()
    return events.groupBy { event ->
        val millis = event.start ?: 0L
        cal.timeInMillis = millis
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        cal.time
    }
}
