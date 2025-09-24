package at.mcbabo.calsync.data.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.mcbabo.calsync.data.repository.SettingsRepository
import at.mcbabo.calsync.data.store.AppSettings
import at.mcbabo.calsync.worker.SyncScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SettingsViewModel @Inject constructor(
    application: Application,
    private val settingsRepository: SettingsRepository,
    private val syncScheduler: SyncScheduler
) : ViewModel() {
    private val appContext = application.applicationContext

    val settings: StateFlow<AppSettings> =
        settingsRepository.settingsFlow
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = AppSettings()
            )

    fun toggleTheme() {
        viewModelScope.launch {
            settingsRepository.toggleTheme()
        }
    }

    fun setSyncInterval(minutes: Int) {
        viewModelScope.launch {
            settingsRepository.setSyncInterval(minutes)
            syncScheduler.reschedulePeriodicSync(appContext)
        }
    }
}
