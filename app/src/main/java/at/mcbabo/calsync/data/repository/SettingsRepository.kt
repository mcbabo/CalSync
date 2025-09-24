package at.mcbabo.calsync.data.repository

import at.mcbabo.calsync.data.store.AppSettings
import at.mcbabo.calsync.data.store.SettingsDataStore
import at.mcbabo.calsync.data.store.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepository @Inject constructor(private val settingsDataStore: SettingsDataStore) {
    val settingsFlow: Flow<AppSettings> = settingsDataStore.settingsFlow

    suspend fun getCurrentSettings(): AppSettings = settingsFlow.first()

    suspend fun updateSettings(settings: AppSettings) {
        settingsDataStore.updateSettings(settings)
    }

    suspend fun updateSettings(update: (AppSettings) -> AppSettings) {
        settingsDataStore.updateSettings(update)
    }

    suspend fun toggleTheme() {
        val currentTheme = settingsFlow.first().selectedTheme
        val newTheme = when (currentTheme) {
            ThemeMode.LIGHT -> ThemeMode.DARK
            ThemeMode.DARK -> ThemeMode.LIGHT
            ThemeMode.SYSTEM -> ThemeMode.LIGHT
        }
        updateSettings {
            it.copy(
                selectedTheme = newTheme
            )
        }
    }

    suspend fun setSyncInterval(minutes: Int) {
        updateSettings {
            it.copy(
                syncIntervalMinutes = minutes
            )
        }
    }
}