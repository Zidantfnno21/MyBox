package com.example.mybox.data.database


import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.mybox.data.model.CategoryModel
import com.example.mybox.data.model.CategoryWithDetails

@Dao
interface BoxDao {

    @Transaction
    @Query("SELECT * FROM boxCategory")
    suspend fun getCategoriesWithDetails(): List<CategoryWithDetails>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: LiveData<CategoryModel>)

    @Delete
    fun delete(box : CategoryModel)

    @Update
    fun update(box : CategoryModel)

}