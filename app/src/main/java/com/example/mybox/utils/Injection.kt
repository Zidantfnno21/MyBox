package com.example.mybox.utils

import android.content.Context
import com.example.mybox.data.BoxRepository
import com.example.mybox.data.database.BoxDatabase
import java.util.concurrent.Executors

object Injection {
    fun provideRepository(context : Context): BoxRepository {
        val database = BoxDatabase.getDatabase(context)
        val executor = Executors.newSingleThreadExecutor()
        return BoxRepository(database, executor)
    }
}