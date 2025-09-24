package at.mcbabo.calsync.util

import android.content.Context
import android.os.Build
import at.mcbabo.calsync.data.model.Calendar
import biweekly.Biweekly
import biweekly.ICalendar
import biweekly.component.VEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Credentials
import okhttp3.OkHttpClient
import okhttp3.Request
import javax.inject.Inject

class IcsFetcher @Inject constructor(
    private val okHttpClient: OkHttpClient
) {
    suspend fun fetch(context: Context, calendar: Calendar): ICalendar? =
        withContext(Dispatchers.IO) {
            if (calendar.uri.toString().startsWith("http")) {
                val requestBuilder = Request.Builder()
                    .url(calendar.uri.toString())
                    .get()
                    .header(
                        "User-Agent",
                        calendar.userAgent
                            ?: "$ACCOUNT_NAME/${getAppVersion(context)} (Android ${Build.VERSION.RELEASE})"
                    )

                calendar.username?.let { username ->
                    calendar.password?.let { password ->
                        val credential = Credentials.basic(username, password)
                        requestBuilder.header("Authorization", credential)
                    }
                }

                okHttpClient.newCall(requestBuilder.build()).execute().use { response ->
                    if (!response.isSuccessful) {
                        throw IllegalStateException("Request failed: ${response.code}")
                    }
                    response.body.byteStream().use { stream ->
                        Biweekly.parse(stream).first()
                    }
                }
            } else {
                context.contentResolver.openInputStream(calendar.uri)?.use { stream ->
                    Biweekly.parse(stream).first()
                } ?: throw IllegalArgumentException("Cannot open file: ${calendar.uri}")
            }
        }
}

fun VEvent.isAllDay(): Boolean {
    val start = this.dateStart?.value ?: return false
    val cal = java.util.Calendar.getInstance().apply { time = start }
    val isMidnight = cal.get(java.util.Calendar.HOUR_OF_DAY) == 0 &&
            cal.get(java.util.Calendar.MINUTE) == 0 &&
            cal.get(java.util.Calendar.SECOND) == 0 &&
            cal.get(java.util.Calendar.MILLISECOND) == 0
    return isMidnight
}
