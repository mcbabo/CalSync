package at.mcbabo.calsync.data.store

import android.content.Context
import at.mcbabo.calsync.data.repository.SettingsRepository
import at.mcbabo.calsync.util.IcsFetcher
import at.mcbabo.calsync.util.SystemCalendarService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    @Provides
    @Singleton
    fun provideSettingsDataStore(@ApplicationContext context: Context): SettingsDataStore = SettingsDataStore(context)

    @Provides
    @Singleton
    fun provideSettingsRepository(settingsDataStore: SettingsDataStore): SettingsRepository =
        SettingsRepository(settingsDataStore)

    @Provides
    fun provideSystemCalendarService(@ApplicationContext context: Context): SystemCalendarService {
        return SystemCalendarService(context)
    }

    @Provides
    fun provideIcsFetcher(okHttpClient: OkHttpClient): IcsFetcher {
        return IcsFetcher(okHttpClient)
    }

    @Provides
    fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder().build()
}