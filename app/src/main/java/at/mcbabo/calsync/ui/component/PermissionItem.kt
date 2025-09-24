package at.mcbabo.calsync.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import at.mcbabo.calsync.R

@Composable
fun PermissionItem(
    title: String,
    description: String,
    granted: Boolean,
    onRequestPermission: () -> Unit
) {
    if (granted) {
        return
    }

    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .clip(MaterialTheme.shapes.extraLarge)
                .background(MaterialTheme.colorScheme.secondary)
                .clickable { onRequestPermission() }
                .padding(horizontal = 12.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Lock,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSecondary,
            modifier =
                Modifier
                    .padding(start = 8.dp, end = 16.dp)
                    .size(24.dp)
        )

        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSecondary,
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSecondary
            )
        }
    }
}

@Preview
@Composable
fun PermissionItemPreview() {
    MaterialTheme {
        PermissionItem(
            title = stringResource(R.string.permission_calendar),
            description = stringResource(R.string.permission_calendar_desc),
            granted = false,
            onRequestPermission = {}
        )
    }
}