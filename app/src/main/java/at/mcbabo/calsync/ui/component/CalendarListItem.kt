package at.mcbabo.calsync.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import at.mcbabo.calsync.data.model.Calendar
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun CalendarListItem(calendar: Calendar, onClick: (Calendar) -> Unit) {
    val formatter = SimpleDateFormat("dd.MM.yyy HH:mm:ss", Locale.getDefault())

    val lastSync = if (calendar.lastSync != null) {
        formatter.format(calendar.lastSync)
    } else {
        "Never"
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                onClick = { onClick(calendar) }
            )
            .padding(vertical = 8.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(Color(calendar.color))
        )

        Column {
            Text(
                text = calendar.name,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = calendar.uri.toString(),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = lastSync,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}