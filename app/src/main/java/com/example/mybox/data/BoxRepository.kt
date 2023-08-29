package com.example.mybox.data

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.example.mybox.data.database.BoxDatabase
import com.example.mybox.data.model.CategoryModel
import com.example.mybox.data.model.DetailModel
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.tasks.await
import java.util.concurrent.ExecutorService

class BoxRepository(
    private val boxDatabase : BoxDatabase,
    private val executor: ExecutorService
    ) {
    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun logIn(email: String, password: String) : LiveData<Result<FirebaseUser?>> = liveData {
        emit(Result.Loading)
        try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val user = authResult.user
            if (authResult.user != null) {
                emit(Result.Success(user))
            } else {
                emit(Result.Error("Authentication failed"))
            }
        }catch (e: Exception){
            emit(Result.Error(handleAuthException(e)))
        }
    }

    fun register(email: String, password: String) : LiveData<Result<FirebaseUser?>> = liveData {
        emit(Result.Loading)
        try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val user = authResult.user
            if (authResult.user != null) {
                emit(Result.Success(user))
            } else {
                emit(Result.Error("Registration Failed"))
            }
        }catch (e: Exception){
            emit(Result.Error(handleAuthException(e)))
        }
    }

    fun logOut(): LiveData<Result<Unit>> = liveData {
        emit(Result.Loading)
        try {
            auth.signOut()
            emit(Result.Success(Unit))
        }catch (e: Exception){
            emit(Result.Error(e.message.toString()))
        }
    }

    private fun handleAuthException(exception: Exception): String {
        return when (exception) {
            is FirebaseAuthInvalidUserException -> "Invalid user"
            is FirebaseAuthInvalidCredentialsException -> "Invalid credentials"
            is FirebaseAuthUserCollisionException -> "User already exists"
            is FirebaseNetworkException -> "Network error"
            else -> "Authentication failed: ${exception.message}"
        }
    }

    fun getAllBox(): LiveData<Result<List<CategoryModel>>> = liveData {
        emit(Result.Loading)
        try {
            val snapshot = database.child("Category").get().await()
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

    fun getAllDetailBox(categoryId : Int): LiveData<Result<List<DetailModel>>> = liveData {
        emit(Result.Loading)
        try{
            val categorySnapshot = database.child("Category").child(categoryId.toString()).get().await()

            val detailItems = categorySnapshot.children.mapNotNull {
                it.getValue(DetailModel::class.java)
            }

            emit(Result.Success(detailItems))
            boxDatabase.detailDao().insertBox(detailItems as MutableList<DetailModel>)
        }catch (e: Exception) {
            Log.e("BoxRepository", "getAllDetailBox: ${e.message.toString()}")
            emit(Result.Error(e.message.toString()))
        }
    }

    fun getDetailItem(
        id: Int,
        categoryId : Int
    ) : LiveData<Result<DetailModel>> = liveData {
        emit(Result.Loading)
        try {
            val itemSnapshot = database.child("Category").child(categoryId.toString()).child(id.toString()).get().await()

            if (itemSnapshot.exists()) {
                val item = itemSnapshot.getValue(DetailModel::class.java)

                val imgUrl = item?.ImageURL
                val storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(imgUrl.toString())

            }

        }catch (e: Exception){
            emit(Result.Error(e.message.toString()))
        }
    }

   suspend fun addNewCategories(category: CategoryModel, fileImage: Uri) {
        try {
            val categoryRef = database.push()
            categoryRef.setValue(category).await()

            val fileRef : StorageReference = FirebaseStorage.getInstance().getReference("image_category/${categoryRef.key}.jpg")
            fileRef.putFile(fileImage).await()

            val imageUrl = fileRef.downloadUrl.await().toString()
            val updatedCategory = category.copy(ImageURL = imageUrl)

            categoryRef.setValue(updatedCategory).await()
            boxDatabase.boxDao().insertBox(listOf(category))

            Log.d("BoxRepository", "New category added successfully.")
        } catch (e: Exception) {
            Log.e("BoxRepository", "addNewCategory: ${e.message.toString()}")
        }
    }

    suspend fun addNewDetailItem(categoryId: Int , newDetailItem: DetailModel, fileImage : Uri) {
        try {
            val detailRef = database.child("Category").child(categoryId.toString())
            val itemRef = detailRef.child("item").push()
            itemRef.setValue(newDetailItem).await()

            val fileRef: StorageReference = FirebaseStorage.getInstance().getReference("image_category/${itemRef.key}.jpg")
            fileRef.putFile(fileImage).await()

            val imageUrl = fileRef.downloadUrl.await().toString()
            val updateDetailItem = newDetailItem.copy(ImageURL = imageUrl)

            itemRef.setValue(updateDetailItem).await()
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
}