package at.mcbabo.calsync.data.store

import kotlinx.serialization.Serializable

@Serializable
data class AppSettings(
    val selectedTheme: ThemeMode = ThemeMode.SYSTEM,
    val firstLaunch: Boolean = true,

    val notificationsEnabled: Boolean = true,
    val syncIntervalMinutes: Int = 15,
    val lastSyncTimestamp: Long = 0L,

    // Debug mode for development/testing (can be enabled in prod)
    val debugModeEnabled: Boolean = false
)

@Serializable
enum class ThemeMode(val displayName: String, val symbol: Boolean?) {
    LIGHT("Light", false),
    DARK("Dark", true),
    SYSTEM("System Default", null)
}