package com.example.mybox.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.mybox.data.model.CategoryModel
import com.example.mybox.data.model.DetailModel

@Database(
    entities = [CategoryModel::class, DetailModel::class],
    version = 1,
    exportSchema = false
)
abstract class BoxDatabase: RoomDatabase() {
    abstract fun boxDao(): BoxDao
    abstract fun detailDao(): DetailsBoxDao

    companion object{

        @Volatile
        private var INSTANCE: BoxDatabase? = null

        fun getInstance(context : Context): BoxDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BoxDatabase::class.java,
                    "box.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}