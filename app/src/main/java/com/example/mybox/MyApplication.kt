package com.example.mybox

import android.app.Application
import com.example.mybox.utils.SyncScheduler
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MyApplication : Application() {
    @Inject
    lateinit var syncScheduler: SyncScheduler

    override fun onCreate() {
        super.onCreate()

        syncScheduler.scheduleCategorySync()
    }
}

