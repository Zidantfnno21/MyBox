package com.example.mybox.data.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.mybox.data.model.DetailModel

@Dao
interface DetailsBoxDao {

    @Query("SELECT * FROM boxDetails WHERE categoryId = :categoryId ")
    suspend fun getDetailsForCategory(categoryId: Int): List<DetailModel>

    @Query("SELECT * FROM boxDetails where id = :boxId")
    fun getDetailsById(boxId: Int) : LiveData<DetailModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDetails(detail : DetailModel)

    @Delete
    fun deleteBox(box: DetailModel)

    @Update
    fun updateBox(box: DetailModel)
}