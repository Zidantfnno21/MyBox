package com.example.mybox.utils

import android.content.Context
import com.example.mybox.data.database.BoxDatabase
import com.example.mybox.data.repository.BoxRepository
import java.util.concurrent.Executors

//object Injection {
//    fun provideRepository(context : Context): BoxRepository {
//        val database = BoxDatabase.getDatabase(context)
//        val executor = Executors.newSingleThreadExecutor()
//        val fileUtils = FileUtils(context)
//        return BoxRepository(fileUtils,database, executor)
//    }
//}