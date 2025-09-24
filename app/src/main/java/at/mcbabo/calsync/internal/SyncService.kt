package at.mcbabo.calsync.internal

import android.app.Service
import android.content.Intent
import android.os.IBinder


class SyncService : Service() {
    override fun onBind(intent: Intent): IBinder? {
        val syncAdapter = SyncAdapter(this, true)
        return syncAdapter.syncAdapterBinder
    }
}
