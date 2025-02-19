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

    @Query("SELECT * FROM Details WHERE categoryId = :categoryId")
    fun getDetailsByCategoryId(categoryId: Int): LiveData<List<DetailModel>>

    @Query("SELECT * FROM Details where id = :boxId")
    fun getDetailsById(boxId: Int) : LiveData<DetailModel>

    @Query("SELECT * FROM Details where id = :boxId AND categoryId = :categoryId")
    fun getDetailsByCategoryIdAndIds(categoryId: Int, boxId: Int) : LiveData<DetailModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDetails(detail : List<DetailModel>)

    @Delete
    fun deleteBox(box: DetailModel)

    @Update
    fun updateBox(box: DetailModel)
}