package at.mcbabo.calsync.ui.component

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberSliderState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import at.mcbabo.calsync.R
import at.mcbabo.calsync.ui.theme.CalSyncTheme
import at.mcbabo.calsync.ui.theme.colors
import at.mcbabo.calsync.ui.theme.toHex

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun ColorPicker(
    selectedColor: Color,
    onDismiss: () -> Unit,
    onColorSelected: (Color) -> Unit
) {
    var tab by remember { mutableIntStateOf(0) }

    val red = rememberSliderState(
        valueRange = 0f..255f
    )
    val green = rememberSliderState(
        valueRange = 0f..255f
    )
    val blue = rememberSliderState(
        valueRange = 0f..255f
    )

    LaunchedEffect(selectedColor) {
        red.value = selectedColor.red * 255f
        green.value = selectedColor.green * 255f
        blue.value = selectedColor.blue * 255f
    }

    BasicAlertDialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .clickable(
                            onClick = {
                                tab = (tab + 1) % 2
                            }
                        )
                        .padding(2.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (tab == 0) Icons.Outlined.Palette else Icons.Outlined.Tune,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = if (tab == 0) stringResource(R.string.color_palette) else stringResource(R.string.custom_color),
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                HorizontalDivider(modifier = Modifier.fillMaxWidth())

                AnimatedContent(
                    targetState = tab,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(200)).togetherWith(fadeOut(animationSpec = tween(200)))
                    },
                    label = "tab_animation"
                ) { currentTab ->
                    when (currentTab) {
                        0 -> {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color(
                                        red = red.value / 255f,
                                        green = green.value / 255f,
                                        blue = blue.value / 255f
                                    )
                                ) {
                                    Text(
                                        text = Color(
                                            red = red.value / 255f, green = green.value / 255f, blue = blue.value / 255f
                                        ).toHex(),
                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 24.dp),
                                        style = MaterialTheme.typography.titleMedium,
                                        textAlign = TextAlign.Center
                                    )
                                }

                                HorizontalDivider(modifier = Modifier.fillMaxWidth())

                                Slider(state = red, modifier = Modifier.fillMaxWidth())

                                Slider(state = green, modifier = Modifier.fillMaxWidth())

                                Slider(state = blue, modifier = Modifier.fillMaxWidth())
                            }
                        }

                        1 -> {
                            ColorGrid(colors, columns = 5) { color ->
                                red.value = color.red * 255f
                                green.value = color.green * 255f
                                blue.value = color.blue * 255f

                                tab = 0
                            }
                        }
                    }
                }

                HorizontalDivider(modifier = Modifier.fillMaxWidth())

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = {
                            onColorSelected(
                                Color(
                                    red = red.value / 255f,
                                    green = green.value / 255f,
                                    blue = blue.value / 255f
                                )
                            )
                        }
                    ) {
                        Text(stringResource(R.string.save))
                    }
                }
            }
        }
    }
}

@Composable
fun ColorGrid(
    colors: List<Color>,
    columns: Int,
    onColorClick: (Color) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(colors.size) { index ->
            val color = colors[index]
            Box(
                modifier = Modifier
                    .aspectRatio(1f)
                    .background(color, shape = RoundedCornerShape(8.dp))
                    .clickable { onColorClick(color) }
            )
        }
    }
}

@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
fun ColorPickerPreview() {
    CalSyncTheme {
        ColorPicker(
            selectedColor = Color.Cyan,
            onDismiss = {}
        ) {}
    }
}
