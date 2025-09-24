package at.mcbabo.calsync.worker

import android.Manifest
import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import at.mcbabo.calsync.data.repository.CalendarRepository
import at.mcbabo.calsync.data.repository.SettingsRepository
import at.mcbabo.calsync.util.hasPermissions
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton


@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val calendarRepository: CalendarRepository
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result = try {
        if (hasPermissions(
                applicationContext,
                arrayOf(Manifest.permission.WRITE_CALENDAR, Manifest.permission.READ_CALENDAR)
            )
        ) {
            calendarRepository.syncAllCalendars(applicationContext)
        }

        Result.success()
    } catch (e: Exception) {
        Result.retry()
    }
}


@Singleton
class SyncScheduler @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    companion object {
        private const val WORK_NAME = "calendar_sync_work"
    }

    fun schedulePeriodicSync(context: Context) {
        val scope = CoroutineScope(Dispatchers.IO)
        scope.launch {
            val settings = settingsRepository.settingsFlow.first()
            val intervalMinutes = settings.syncIntervalMinutes.coerceAtLeast(15)

            val request = PeriodicWorkRequestBuilder<SyncWorker>(
                intervalMinutes.toLong(), TimeUnit.MINUTES
            )
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                )
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
        }
    }

    fun reschedulePeriodicSync(context: Context) {
        val scope = CoroutineScope(Dispatchers.IO)
        scope.launch {
            WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
            schedulePeriodicSync(context)
        }
    }

    fun scheduleOneTimeSync(context: Context) {
        val request = OneTimeWorkRequestBuilder<SyncWorker>().build()
        WorkManager.getInstance(context)
            .enqueueUniqueWork("calendar_sync_one_time", ExistingWorkPolicy.REPLACE, request)
    }
}
