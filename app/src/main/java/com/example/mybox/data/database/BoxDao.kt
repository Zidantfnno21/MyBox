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
import com.example.mybox.data.model.CategoryWithDetail

@Dao
interface BoxDao {

    @Query("SELECT * FROM Category")
    fun getCategories(): LiveData<List<CategoryModel>>

    @Transaction
    @Query("SELECT * FROM Category")
    fun getCategoriesWithDetails(): LiveData<List<CategoryWithDetail>>

    @Transaction
    @Query("SELECT * FROM Category WHERE id = :categoryId")
    fun getCategoryWithDetailsById(categoryId: Int): LiveData<CategoryWithDetail>

    @Query("select * from Category where id = :id")
    fun getCategoriesById(id: Int): LiveData<CategoryModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category : List<CategoryModel>)

    @Delete
    fun delete(box : CategoryModel)

    @Update
    fun update(box : CategoryModel)

}