package com.example.mybox.data.database


import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.RawQuery
import androidx.room.Update
import androidx.sqlite.db.SupportSQLiteQuery
import com.example.mybox.data.model.CategoryModel

@Dao
interface BoxDao {

    @RawQuery(observedEntities = [CategoryModel::class])
    fun getAllBox(query : SupportSQLiteQuery): LiveData<CategoryModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBox(box: CategoryModel): Long

    @Delete
    fun delete(box : CategoryModel)

    @Update
    fun update(box : CategoryModel)

}