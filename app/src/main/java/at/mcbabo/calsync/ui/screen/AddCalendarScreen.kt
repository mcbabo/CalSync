package at.mcbabo.calsync.ui.screen

import android.content.Context
import android.content.Intent
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Alarm
import androidx.compose.material.icons.outlined.CellTower
import androidx.compose.material.icons.outlined.DeleteSweep
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Key
import androidx.compose.material.icons.outlined.Merge
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import at.mcbabo.calsync.R
import at.mcbabo.calsync.data.model.SyncStrategy
import at.mcbabo.calsync.data.viewmodel.CalendarViewModel
import at.mcbabo.calsync.ui.component.BackButton
import at.mcbabo.calsync.ui.component.ColorPicker
import at.mcbabo.calsync.ui.component.PreferenceSubtitle
import at.mcbabo.calsync.ui.component.PreferenceSwitch
import at.mcbabo.calsync.ui.theme.CalSyncTheme
import at.mcbabo.calsync.ui.theme.colors
import java.net.URI

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCalendarScreen(
    viewModel: CalendarViewModel = hiltViewModel(),
    onCalendarAdded: () -> Unit,
) {
    val context = LocalContext.current
    AddCalendarContent(
        context = context,
        onCalendarAdded = { name, url, color, syncStrategy, reminderMinutes, userAgent, username, password ->
            viewModel.addCalendarFromIcs(
                context = context,
                name = name,
                uriOrUrl = url,
                color = color,
                syncStrategy = syncStrategy,
                reminderMinutes = reminderMinutes?.toIntOrNull(),
                userAgent = userAgent,
                username = username,
                password = password
            )
            onCalendarAdded()
        },
        onBack = { onCalendarAdded() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCalendarContent(
    context: Context,
    onCalendarAdded: (String, String, Int, SyncStrategy, String?, String?, String?, String?) -> Unit = { _, _, _, _, _, _, _, _ -> },
    onBack: () -> Unit = {}
) {
    var name by remember { mutableStateOf("") }
    var url by remember { mutableStateOf("") }
    var color by remember { mutableStateOf(colors.first()) }
    var syncStrategy by remember { mutableStateOf(SyncStrategy.REPLACE) }

    var showReminder by remember { mutableStateOf(false) }
    var reminderMinutes by remember { mutableStateOf<String?>(null) }

    var showUserAgent by remember { mutableStateOf(false) }
    var userAgent by remember { mutableStateOf<String?>(null) }

    var showAuthentication by remember { mutableStateOf(false) }
    var username by remember { mutableStateOf<String?>(null) }
    var password by remember { mutableStateOf<String?>(null) }

    var showColorDialog by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    val fileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            context.contentResolver.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )

            url = uri.toString()
        }
    }

    var invalidUri by remember { mutableStateOf(false) }

    fun isValidHttpOrContent(str: String): Boolean {
        return try {
            val uri = URI(str)
            when (uri.scheme?.lowercase()) {
                "http", "https" -> uri.host != null
                "content" -> true
                else -> false
            }
        } catch (_: Exception) {
            false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.add_calendar)) },
                navigationIcon = {
                    BackButton { onBack() }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (name.isBlank() || url.isBlank()) {
                        error = "Name and URL/file required"
                        return@FloatingActionButton
                    }
                    onCalendarAdded(
                        name,
                        url,
                        color.toArgb(),
                        syncStrategy,
                        cleanInput(reminderMinutes),
                        cleanInput(userAgent),
                        cleanInput(username),
                        cleanInput(password)
                    )
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = stringResource(R.string.add_calendar)
                )
            }
        },
        modifier = Modifier.blur(if (showColorDialog) 4.dp else 0.dp)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
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
                    PreferenceSubtitle(stringResource(R.string.calendar_name))
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text(stringResource(R.string.calendar_name)) },
                        singleLine = true,
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
                            .background(color)
                            .clickable(
                                onClick = {
                                    showColorDialog = true
                                }
                            )
                    )
                }
            }

            PreferenceSubtitle("Source")
            OutlinedTextField(
                value = url,
                onValueChange = {
                    url = it
                    invalidUri = !isValidHttpOrContent(it)
                },
                isError = invalidUri,
                label = { Text("URL or File") },
                singleLine = true,
                trailingIcon = {
                    IconButton(onClick = { fileLauncher.launch(arrayOf("*/*")) }) {
                        Icon(
                            imageVector = Icons.Outlined.Folder,
                            contentDescription = stringResource(R.string.pick_file)
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp, 4.dp)
            )

            PreferenceSubtitle(stringResource(R.string.other_options))

            PreferenceSwitch(
                title = stringResource(R.string.sync_strategy),
                description =
                    if (syncStrategy == SyncStrategy.REPLACE)
                        stringResource(R.string.sync_strategy_replace_desc)
                    else
                        stringResource(R.string.sync_strategy_sync_desc),
                icon = if (syncStrategy == SyncStrategy.REPLACE) Icons.Outlined.DeleteSweep else Icons.Outlined.Merge,
                isChecked = (syncStrategy == SyncStrategy.REPLACE)
            ) {
                syncStrategy = if (syncStrategy == SyncStrategy.REPLACE) SyncStrategy.MERGE else SyncStrategy.REPLACE
            }

            PreferenceSwitch(
                title = stringResource(R.string.event_reminder),
                description = stringResource(R.string.event_reminder_desc),
                icon = Icons.Outlined.Alarm,
                isChecked = showReminder
            ) {
                showReminder = !showReminder
            }

            AnimatedVisibility(
                visible = showReminder
            ) {
                OutlinedTextField(
                    value = reminderMinutes ?: "",
                    onValueChange = { reminderMinutes = it },
                    label = { Text(stringResource(R.string.event_reminder)) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp, 4.dp)
                )
            }

            PreferenceSwitch(
                title = stringResource(R.string.user_agent),
                description = stringResource(R.string.user_agent_desc),
                icon = Icons.Outlined.CellTower,
                isChecked = showUserAgent
            ) {
                showUserAgent = !showUserAgent
            }

            AnimatedVisibility(
                visible = showUserAgent
            ) {
                OutlinedTextField(
                    value = userAgent ?: "",
                    onValueChange = {},
                    label = { Text(stringResource(R.string.user_agent)) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp, 4.dp)
                )
            }

            PreferenceSwitch(
                title = stringResource(R.string.authentication),
                description = stringResource(R.string.authentication_desc),
                icon = Icons.Outlined.Key,
                isChecked = showAuthentication
            ) {
                showAuthentication = !showAuthentication
            }

            AnimatedVisibility(
                visible = showAuthentication
            ) {
                Column {
                    OutlinedTextField(
                        value = username ?: "",
                        onValueChange = {},
                        label = { Text(stringResource(R.string.username)) },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp, 4.dp)
                    )

                    OutlinedTextField(
                        value = password ?: "",
                        onValueChange = {},
                        label = { Text(stringResource(R.string.password)) },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp, 4.dp)
                    )
                }
            }

            if (error != null) {
                Text((error as Any).toString(), color = Color.Red)
            }
        }
    }

    if (showColorDialog) {
        ColorPicker(color, { showColorDialog = false }) {
            showColorDialog = false
            color = it
        }
    }
}

fun cleanInput(input: String?): String? {
    if (input == null) return null
    val trimmed = input.trim()
    return trimmed.ifBlank { null }
}

@Composable
@Preview(uiMode = UI_MODE_NIGHT_YES)
fun AddCalendarScreenPreview() {
    CalSyncTheme {
        AddCalendarContent(
            context = LocalContext.current,
            onCalendarAdded = { _, _, _, _, _, _, _, _ -> }
        )
    }
}
