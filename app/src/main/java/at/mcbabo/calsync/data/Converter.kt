package at.mcbabo.calsync.data

import android.net.Uri
import androidx.room.TypeConverter
import java.util.Date

class Converters {
    /** Converts an [Uri] to a [String]. */
    @TypeConverter
    fun fromUri(value: Uri?): String? = value?.toString()

    /** Converts a [String] to an [Uri]. */
    @TypeConverter
    fun toUri(value: String?): Uri? = value?.let { Uri.parse(it) }

    /** Converts a timestamp [Long] to a [Date]. */
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? = value?.let { Date(it) }

    /** Converts a [Date] to a timestamp [Long]. */
    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? = date?.time
}
