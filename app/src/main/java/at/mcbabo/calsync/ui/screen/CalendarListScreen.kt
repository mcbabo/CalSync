package at.mcbabo.calsync.ui.screen

import android.Manifest
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Sync
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.LoadingIndicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import at.mcbabo.calsync.R
import at.mcbabo.calsync.data.viewmodel.CalendarViewModel
import at.mcbabo.calsync.ui.component.AddButton
import at.mcbabo.calsync.ui.component.CalendarListItem
import at.mcbabo.calsync.ui.component.PermissionItem
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CalendarListScreen(
    navController: NavController,
    onAddCalendar: () -> Unit,
    viewModel: CalendarViewModel = hiltViewModel(),
) {
    val context = LocalContext.current

    val pullToRefreshState = rememberPullToRefreshState()
    val calendars by viewModel.calendars.collectAsState()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    val listState = rememberLazyListState()
    val expandedFab by remember { derivedStateOf { listState.firstVisibleItemIndex == 0 } }

    val calendarPermissionState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.READ_CALENDAR,
            Manifest.permission.WRITE_CALENDAR
        )
    )

    val (fabIcon, fabTextRes) = if (calendars.isEmpty()) {
        Icons.Outlined.Add to R.string.add_calendar
    } else {
        Icons.Outlined.Sync to R.string.sync
    }

    fun onFABClick() {
        if (calendars.isEmpty()) {
            onAddCalendar()
        } else {
            viewModel.syncAllCalendars(context)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.subscribed_calendars)) },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navController.navigate("settings")
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Settings,
                            contentDescription = null
                        )
                    }
                },
                actions = {
                    if (calendars.isNotEmpty()) {
                        AddButton { onAddCalendar() }
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { onFABClick() },
                icon = { Icon(fabIcon, contentDescription = stringResource(fabTextRes)) },
                text = { Text(stringResource(fabTextRes)) },
                expanded = expandedFab
            )
        }
    ) { innerPadding ->
        PullToRefreshBox(
            isRefreshing = isLoading,
            state = pullToRefreshState,
            onRefresh = {
                viewModel.syncAllCalendars(context)
            },
            indicator = {
                LoadingIndicator(
                    state = pullToRefreshState,
                    isRefreshing = isLoading,
                    modifier = Modifier.align(Alignment.TopCenter),
                )
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            if (calendars.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    state = listState
                ) {
                    item(key = "permissions") {
                        PermissionItem(
                            title = stringResource(R.string.permission_calendar),
                            description = stringResource(R.string.permission_calendar_desc),
                            granted = calendarPermissionState.permissions.all { it.status.isGranted }
                        ) {
                            calendarPermissionState.launchMultiplePermissionRequest()
                        }
                    }

                    items(calendars) { calendar ->
                        CalendarListItem(calendar) { navController.navigate("calendar/${calendar.id}") }
                    }
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = stringResource(R.string.add_calendar),
                        modifier = Modifier
                            .size(64.dp)
                            .padding(bottom = 16.dp)
                    )
                    Text(
                        text = stringResource(R.string.no_calendars_found),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}
