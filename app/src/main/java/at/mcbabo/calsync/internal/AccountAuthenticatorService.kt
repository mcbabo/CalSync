package at.mcbabo.calsync.internal

import android.app.Service
import android.content.Intent
import android.os.IBinder


class AccountAuthenticatorService : Service() {
    override fun onBind(intent: Intent): IBinder? {
        return AccountAuthenticator(this).iBinder
    }
}
