package at.mcbabo.calsync.util

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat


fun hasPermission(context: Context, permission: String): Boolean {
    return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
}

fun hasPermissions(context: Context, permissions: Array<String>): Boolean {
    return permissions.all { ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED }
}

fun getAppVersion(context: Context): String {
    return try {
        val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        packageInfo.versionName ?: "1.0"
    } catch (e: Exception) {
        "1.0"
    }
}

fun getAppVersionCode(context: Context): String {
    return try {
        val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        packageInfo.longVersionCode.toString()
    } catch (e: Exception) {
        "1.0"
    }
}
