package at.mcbabo.calsync.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import at.mcbabo.calsync.data.dao.CalendarDao
import at.mcbabo.calsync.data.dao.EventDao
import at.mcbabo.calsync.data.model.Calendar
import at.mcbabo.calsync.data.model.Event
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppDatabaseModule {
    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase = Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "calsync")
        .addCallback(object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
            }
        })
        .build()

    @Provides
    fun calendarDao(database: AppDatabase): CalendarDao = database.calendarDao()

    @Provides
    fun eventDao(database: AppDatabase): EventDao = database.eventDao()
}


@Database(
    entities = [
        Calendar::class,
        Event::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun calendarDao(): CalendarDao
    abstract fun eventDao(): EventDao
}