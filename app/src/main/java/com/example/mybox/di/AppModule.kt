package com.example.mybox.di

import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import androidx.work.WorkManager
import com.example.mybox.data.database.BoxDatabase
import com.example.mybox.data.repository.BoxRepository
import com.example.mybox.utils.FileUtils
import com.example.mybox.utils.NetworkUtils
import com.example.mybox.utils.PREF_NAME
import com.example.mybox.utils.PreferencesHelper
import com.example.mybox.utils.SyncScheduler
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideFileUtils(@ApplicationContext context: Context): FileUtils {
        return FileUtils(context)
    }

    @Provides
    @Singleton
    fun provideExecutor(): Executor {
        return Executors.newSingleThreadExecutor()
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): BoxDatabase {
        return BoxDatabase.getDatabase(context)
    }

    @Provides
    @Singleton
    fun provideConnectivityManager(@ApplicationContext context: Context): ConnectivityManager {
        return context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    @Provides
    @Singleton
    fun provideWorkManager(@ApplicationContext context: Context): WorkManager {
        return WorkManager.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideSyncScheduler(workManager: WorkManager): SyncScheduler {
        return SyncScheduler(workManager)
    }

    @Provides
    @Singleton
    fun provideBoxRepository(
        database: BoxDatabase,
        executor: Executor,
        fileUtils: FileUtils,
        networkUtils: NetworkUtils,
        syncScheduler: SyncScheduler,
        preferencesHelper: PreferencesHelper
    )
    : BoxRepository {
        return BoxRepository(
            fileUtils,
            networkUtils ,
            database,
            executor,
            syncScheduler,
            preferencesHelper,
        )
    }
}

@Module
@InstallIn(SingletonComponent::class)
object StorageModule {

    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun providePreferencesHelper(sharedPreferences: SharedPreferences): PreferencesHelper {
        return PreferencesHelper(sharedPreferences)
    }
}