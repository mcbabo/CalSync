package at.mcbabo.calsync.ui.theme

import androidx.compose.ui.graphics.Color

val primaryLight = Color(0xFF4E5B92)
val onPrimaryLight = Color(0xFFFFFFFF)
val primaryContainerLight = Color(0xFFDCE1FF)
val onPrimaryContainerLight = Color(0xFF364479)
val secondaryLight = Color(0xFF5A5D72)
val onSecondaryLight = Color(0xFFFFFFFF)
val secondaryContainerLight = Color(0xFFDEE1F9)
val onSecondaryContainerLight = Color(0xFF424659)
val tertiaryLight = Color(0xFF75546F)
val onTertiaryLight = Color(0xFFFFFFFF)
val tertiaryContainerLight = Color(0xFFFFD7F4)
val onTertiaryContainerLight = Color(0xFF5C3D56)
val errorLight = Color(0xFFBA1A1A)
val onErrorLight = Color(0xFFFFFFFF)
val errorContainerLight = Color(0xFFFFDAD6)
val onErrorContainerLight = Color(0xFF93000A)
val backgroundLight = Color(0xFFFAF8FF)
val onBackgroundLight = Color(0xFF1A1B21)
val surfaceLight = Color(0xFFFAF8FF)
val onSurfaceLight = Color(0xFF1A1B21)
val surfaceVariantLight = Color(0xFFE2E1EC)
val onSurfaceVariantLight = Color(0xFF45464F)
val outlineLight = Color(0xFF767680)
val outlineVariantLight = Color(0xFFC6C5D0)
val scrimLight = Color(0xFF000000)
val inverseSurfaceLight = Color(0xFF2F3036)
val inverseOnSurfaceLight = Color(0xFFF2F0F7)
val inversePrimaryLight = Color(0xFFB7C4FF)
val surfaceDimLight = Color(0xFFDBD9E0)
val surfaceBrightLight = Color(0xFFFAF8FF)
val surfaceContainerLowestLight = Color(0xFFFFFFFF)
val surfaceContainerLowLight = Color(0xFFF4F2FA)
val surfaceContainerLight = Color(0xFFEFEDF4)
val surfaceContainerHighLight = Color(0xFFE9E7EF)
val surfaceContainerHighestLight = Color(0xFFE3E1E9)

val primaryDark = Color(0xFFB7C4FF)
val onPrimaryDark = Color(0xFF1E2D61)
val primaryContainerDark = Color(0xFF364479)
val onPrimaryContainerDark = Color(0xFFDCE1FF)
val secondaryDark = Color(0xFFC2C5DD)
val onSecondaryDark = Color(0xFF2B3042)
val secondaryContainerDark = Color(0xFF424659)
val onSecondaryContainerDark = Color(0xFFDEE1F9)
val tertiaryDark = Color(0xFFE4BADA)
val onTertiaryDark = Color(0xFF43273F)
val tertiaryContainerDark = Color(0xFF5C3D56)
val onTertiaryContainerDark = Color(0xFFFFD7F4)
val errorDark = Color(0xFFFFB4AB)
val onErrorDark = Color(0xFF690005)
val errorContainerDark = Color(0xFF93000A)
val onErrorContainerDark = Color(0xFFFFDAD6)
val backgroundDark = Color(0xFF121318)
val onBackgroundDark = Color(0xFFE3E1E9)
val surfaceDark = Color(0xFF121318)
val onSurfaceDark = Color(0xFFE3E1E9)
val surfaceVariantDark = Color(0xFF45464F)
val onSurfaceVariantDark = Color(0xFFC6C5D0)
val outlineDark = Color(0xFF90909A)
val outlineVariantDark = Color(0xFF45464F)
val scrimDark = Color(0xFF000000)
val inverseSurfaceDark = Color(0xFFE3E1E9)
val inverseOnSurfaceDark = Color(0xFF2F3036)
val inversePrimaryDark = Color(0xFF4E5B92)
val surfaceDimDark = Color(0xFF121318)
val surfaceBrightDark = Color(0xFF38393F)
val surfaceContainerLowestDark = Color(0xFF0D0E13)
val surfaceContainerLowDark = Color(0xFF1A1B21)
val surfaceContainerDark = Color(0xFF1E1F25)
val surfaceContainerHighDark = Color(0xFF292A2F)
val surfaceContainerHighestDark = Color(0xFF34343A)

val colors = listOf(
    Color(0xFFE57373), // Red
    Color(0xFFF06292), // Pink
    Color(0xFFBA68C8), // Purple
    Color(0xFF9575CD), // Deep Purple
    Color(0xFF7986CB), // Indigo
    Color(0xFF64B5F6), // Blue
    Color(0xFF4FC3F7), // Light Blue
    Color(0xFF4DD0E1), // Cyan
    Color(0xFF4DB6AC), // Teal
    Color(0xFF81C784), // Green
    Color(0xFFAED581), // Light Green
    Color(0xFFDCE775), // Lime
    Color(0xFFFFD54F), // Amber
    Color(0xFFFFB74D), // Orange
    Color(0xFFA1887F), // Brown
    Color(0xFFE0E0E0), // Grey
    Color(0xFF90A4AE), // Blue Grey
    Color(0xFFF48FB1), // Light Pink
    Color(0xFFCE93D8), // Light Purple
    Color(0xFF80CBC4)  // Turquoise
)

fun Color.toHex(includeAlpha: Boolean = true): String {
    val alpha = (alpha * 255).toInt().coerceIn(0, 255)
    val red = (red * 255).toInt().coerceIn(0, 255)
    val green = (green * 255).toInt().coerceIn(0, 255)
    val blue = (blue * 255).toInt().coerceIn(0, 255)

    return if (includeAlpha) {
        String.format("#%02X%02X%02X%02X", alpha, red, green, blue)
    } else {
        String.format("#%02X%02X%02X", red, green, blue)
    }
}
