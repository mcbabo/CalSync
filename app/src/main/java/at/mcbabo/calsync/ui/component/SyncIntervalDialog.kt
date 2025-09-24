package at.mcbabo.calsync.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Sync
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import at.mcbabo.calsync.R
import at.mcbabo.calsync.ui.theme.CalSyncTheme

val predefinedSyncIntervals = listOf(15, 30, 60, 120, 240, 360, 720, 1440)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SyncIntervalDialog(
    selectedInterval: Int,
    onDismiss: () -> Unit,
    onSyncInterval: (Int) -> Unit,
) {
    BasicAlertDialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectableGroup(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .padding(2.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Sync,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = stringResource(R.string.sync_interval),
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                HorizontalDivider(modifier = Modifier.fillMaxWidth())

                predefinedSyncIntervals.forEach { interval ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 8.dp)
                            .selectable(
                                selected = (selectedInterval == interval),
                                onClick = { onSyncInterval(interval) },
                                role = Role.RadioButton
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (selectedInterval == interval),
                            onClick = null
                        )
                        Text(
                            text = showTime(interval),
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                }
            }
        }

    }
}

@Composable
fun showTime(minutes: Int, short: Boolean = false): String {
    val hours = minutes / 60
    val mins = minutes % 60
    return if (hours > 0) {
        if (mins > 0) {
            if (short) stringResource(
                R.string.hours_minutes_short,
                hours,
                mins
            ) else stringResource(R.string.hours_minutes, hours, mins)
        } else {
            if (short) stringResource(R.string.hours_short, hours) else stringResource(R.string.hours, hours)
        }
    } else {
        if (short) stringResource(R.string.min_short, mins) else stringResource(R.string.minutes, mins)
    }
}

@Preview
@Composable
fun SyncDialogPreview() {
    CalSyncTheme {
        SyncIntervalDialog(
            selectedInterval = 60,
            onDismiss = {}
        ) {}
    }
}
