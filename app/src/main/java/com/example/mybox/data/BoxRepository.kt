package com.example.mybox.data

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.example.mybox.data.database.BoxDatabase
import com.example.mybox.data.model.CategoryModel
import com.example.mybox.data.model.DetailModel
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await
import java.lang.Exception
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
        try{
            val categorySnapshot = database.child("Category").get().await()
            val categories = categorySnapshot.children.mapNotNull {
                it.getValue(CategoryModel::class.java)
            }

            val detailItems = mutableListOf<DetailModel>()
            for (category in categories) {
                detailItems.addAll(category.item)
            }

            emit(Result.Success(detailItems))
            boxDatabase.detailDao().insertBox(detailItems)
        }catch (e: Exception) {
            Log.e("BoxRepository", "getAllDetailBox: ${e.message.toString()}")
            emit(Result.Error(e.message.toString()))
        }

    }

    suspend fun addNewCategories(category: CategoryModel) {
        try {
            val categoryRef = database.push()
            categoryRef.setValue(category).await()

            boxDatabase.boxDao().insertBox(listOf(category))

            Log.d("BoxRepository", "New category added successfully.")
        } catch (e: Exception) {
            Log.e("BoxRepository", "addNewCategory: ${e.message.toString()}")
        }
    }

    suspend fun addNewDetailItem(categoryId: Int , newDetailItem: DetailModel) {
        try {
            val detailRef = database.child("Category").child(categoryId.toString())
            val itemRef = detailRef.child("item").push()

            itemRef.setValue(newDetailItem).await()

            boxDatabase.detailDao().insertBox(mutableListOf(newDetailItem))

            Log.d("BoxRepository", "Detail item added successfully")
        }catch (e: Exception){
            Log.e("BoxRepository", "Error adding detail item: ${e.message}")
        }
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

    companion object
}