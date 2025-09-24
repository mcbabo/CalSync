package at.mcbabo.calsync

import android.Manifest
import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import at.mcbabo.calsync.util.hasPermissions
import at.mcbabo.calsync.worker.SyncScheduler
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject


@HiltAndroidApp
class CalSyncApplication : Application(), Configuration.Provider {
    @Inject
    lateinit var syncScheduler: SyncScheduler

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()

        if (hasPermissions(this, arrayOf(Manifest.permission.WRITE_CALENDAR, Manifest.permission.READ_CALENDAR))) {
            syncScheduler.schedulePeriodicSync(this)
        }
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}
