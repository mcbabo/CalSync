package at.mcbabo.calsync.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun Avatar(
    text: String,
    modifier: Modifier = Modifier,
    size: Int = 36,
    color: Color = MaterialTheme.colorScheme.primaryContainer
) {
    val firstLetter = text.firstOrNull()?.uppercaseChar()?.toString() ?: "?"

    Box(
        modifier =
            modifier
                .size(size.dp)
                .clip(CircleShape)
                .background(color),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = firstLetter,
            color = Color.White,
            textAlign = TextAlign.Center
        )
    }
}