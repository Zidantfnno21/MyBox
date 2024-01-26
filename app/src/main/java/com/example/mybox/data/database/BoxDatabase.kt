package com.example.mybox.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.mybox.data.Converters
import com.example.mybox.data.model.CategoryModel
import com.example.mybox.data.model.DetailModel

@Database(
    entities = [CategoryModel::class, DetailModel::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class BoxDatabase: RoomDatabase() {
    abstract fun boxDao(): BoxDao
    abstract fun detailDao(): DetailsBoxDao

    companion object{
        @Volatile
        private var INSTANCE: BoxDatabase? = null

        @JvmStatic
        fun getDatabase(context : Context): BoxDatabase {
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