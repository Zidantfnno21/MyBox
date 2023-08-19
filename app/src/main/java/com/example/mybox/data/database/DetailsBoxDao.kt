package com.example.mybox.data.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.example.mybox.data.model.DetailModel

@Dao
interface DetailsBoxDao {

    @Query("SELECT * FROM boxDetails WHERE categoryId = :categoryId ")
    fun getAllDetailsBox(categoryId: Int): LiveData<DetailModel>

    @Query("SELECT * FROM boxDetails where id = :boxId")
    fun getBoxById(boxId: Int) : LiveData<DetailModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNewBox(newBox: DetailModel) : Long

    @Delete
    fun deleteBox(box: DetailModel)
}