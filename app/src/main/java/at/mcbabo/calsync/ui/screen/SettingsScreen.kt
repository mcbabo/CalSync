package at.mcbabo.calsync.ui.screen

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BugReport
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Sync
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults.exitUntilCollapsedScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import at.mcbabo.calsync.R
import at.mcbabo.calsync.data.store.ThemeMode
import at.mcbabo.calsync.data.viewmodel.CalendarViewModel
import at.mcbabo.calsync.data.viewmodel.SettingsViewModel
import at.mcbabo.calsync.ui.component.BackButton
import at.mcbabo.calsync.ui.component.PreferenceItem
import at.mcbabo.calsync.ui.component.PreferenceSubtitle
import at.mcbabo.calsync.ui.component.PreferenceSwitch
import at.mcbabo.calsync.ui.component.PreferenceText
import at.mcbabo.calsync.ui.component.PreferencesCautionCard
import at.mcbabo.calsync.ui.component.SyncIntervalDialog
import at.mcbabo.calsync.ui.component.showTime
import at.mcbabo.calsync.ui.theme.CalSyncTheme
import at.mcbabo.calsync.util.getAppVersion
import at.mcbabo.calsync.util.getAppVersionCode
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    settingsViewModel: SettingsViewModel = hiltViewModel(),
    calendarViewModel: CalendarViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollBehavior = exitUntilCollapsedScrollBehavior()

    val settings by settingsViewModel.settings.collectAsStateWithLifecycle()

    val clickThreshold = 5
    val timeLimit = 2000L
    val debugClickCount = remember { mutableIntStateOf(0) }
    val coroutineScope = rememberCoroutineScope()

    val deleteMessage = stringResource(R.string.all_system_calendars_removed)

    fun debugOnClick() {
        debugClickCount.intValue++
        if (debugClickCount.intValue >= clickThreshold) {
            calendarViewModel.deleteAllSystemCalendars()
            coroutineScope.launch {
                snackbarHostState.showSnackbar(deleteMessage)
            }
            debugClickCount.intValue = 0
        }

        coroutineScope.launch {
            delay(timeLimit)
            debugClickCount.intValue = 0
        }
    }

    var showDialog by remember { mutableStateOf(false) }

    val blurRadius by animateDpAsState(
        targetValue = if (showDialog) 6.dp else 0.dp,
        animationSpec = tween(durationMillis = 100),
        label = "BlurAnimation"
    )

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = { Text(text = stringResource(R.string.settings)) },
                navigationIcon = {
                    BackButton { onBack() }
                },
                scrollBehavior = scrollBehavior
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        modifier =
            Modifier
                .blur(blurRadius)
                .nestedScroll(scrollBehavior.nestedScrollConnection),
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
        ) {
            PreferenceSubtitle(stringResource(R.string.general))
            PreferenceText(
                title = stringResource(R.string.sync_interval),
                description = stringResource(R.string.sync_interval_desc),
                icon = Icons.Outlined.Sync,
                displayText = showTime(settings.syncIntervalMinutes, false)
            ) {
                showDialog = true
            }

            PreferenceSubtitle(stringResource(R.string.appearance))
            PreferenceSwitch(
                title = stringResource(R.string.dark_theme),
                description = when (settings.selectedTheme) {
                    ThemeMode.DARK -> stringResource(R.string.on)
                    ThemeMode.LIGHT -> stringResource(R.string.off)
                    ThemeMode.SYSTEM -> stringResource(R.string.system)
                },
                icon = when (settings.selectedTheme) {
                    ThemeMode.DARK -> Icons.Outlined.DarkMode
                    ThemeMode.LIGHT -> Icons.Outlined.LightMode
                    ThemeMode.SYSTEM -> Icons.Outlined.Palette
                },
                isChecked = settings.selectedTheme != ThemeMode.LIGHT,
                onClick = { settingsViewModel.toggleTheme() }
            )

            PreferenceSubtitle(stringResource(R.string.developer_options))
            PreferenceItem(
                title = stringResource(R.string.app_name),
                description = "Version Name: ${getAppVersion(context)} | Version Code: ${getAppVersionCode(context)}",
                icon = Icons.Outlined.BugReport,
            )
            PreferencesCautionCard(
                title = stringResource(R.string.remove_all_system_calendars),
                icon = Icons.Outlined.Warning,
                description = stringResource(
                    R.string.remove_all_system_calendars_desc,
                    stringResource(R.string.app_name)
                )
            ) {
                debugOnClick()
            }
        }
    }

    if (showDialog) {
        SyncIntervalDialog(
            selectedInterval = settings.syncIntervalMinutes,
            onDismiss = { showDialog = false }
        ) { interval ->
            settingsViewModel.setSyncInterval(interval)
            showDialog = false
        }
    }
}

@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
fun SettingsScreenPreview() {
    CalSyncTheme {
        SettingsScreen(onBack = {})
    }
}
