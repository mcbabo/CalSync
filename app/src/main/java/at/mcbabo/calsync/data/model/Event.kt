package at.mcbabo.calsync.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "events",
    indices = [Index(value = ["icsId", "calendarId"], unique = true)]
)
data class Event(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val calendarId: Long,
    val icsId: String,
    val eventId: Long,
    val createdAt: Date,
    val updatedAt: Date
)