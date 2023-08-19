package com.example.mybox.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.example.mybox.data.database.BoxDao
import com.example.mybox.data.database.BoxDatabase
import com.example.mybox.data.model.CategoryModel
import com.example.mybox.data.model.DetailModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.tasks.await
import java.lang.Exception
import java.lang.StringBuilder
import java.util.concurrent.ExecutorService

class BoxRepository(
    private val boxDatabase : BoxDatabase,
    private val executor: ExecutorService
    ) {
    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference

    fun getAllBox(): LiveData<Result<List<CategoryModel>>> = liveData {
        emit(Result.Loading)
        try {
            val snapshot = database.get().await()
            val boxs = snapshot.children.mapNotNull {
                it.getValue(CategoryModel::class.java)
            }
            emit(Result.Success(boxs))
            boxDatabase.boxDao().insertBox(boxs)
        }catch (e: Exception) {
            Log.e("BoxRepository", "getAllBox: ${e.message.toString()}")
            emit(Result.Error(e.message.toString()))
        }
    }

    fun getAllDetailBox(): LiveData<Result<List<DetailModel>>> = liveData {
        emit(Result.Loading)

    }

    fun deleteBox(box : CategoryModel) {
        executor.execute {
            boxDatabase.boxDao().delete(box)
        }
    }

    fun deleteDetailBox(detailBox : DetailModel) {
        executor.execute {
            boxDatabase.detailDao().deleteBox(detailBox)
        }
    }
}