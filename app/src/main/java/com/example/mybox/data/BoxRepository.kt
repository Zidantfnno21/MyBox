package com.example.mybox.data

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.example.mybox.data.database.BoxDatabase
import com.example.mybox.data.model.CategoryModel
import com.example.mybox.data.model.DetailModel
import com.example.mybox.data.model.UserModel
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.tasks.await
import java.util.concurrent.ExecutorService

class BoxRepository(
    private val boxDatabase : BoxDatabase,
    private val executor: ExecutorService
) {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val storage = FirebaseStorage.getInstance().reference
    private val usersReference: DatabaseReference = Firebase.database.getReference("Users")
    private fun uidRef(uid: String) = usersReference.child(uid)
    private val _uploadProgress = MutableLiveData<Int>()
    val uploadProgress: LiveData<Int>
        get() = _uploadProgress

    //register
    
    fun logIn(
        email : String ,
        password : String
    ) : LiveData<Result<FirebaseUser?>> = liveData {
        emit(Result.Loading)
        try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val user = authResult.user

            if (user != null) {
                emit(Result.Success(user))
            }
        }catch (e: Exception){
            emit(Result.Error(handleAuthException(e)))
            Log.e("BoxRepository" , "logIn: ${e.message.toString()}", e)
        }
    }

    fun register(
        users : String ,
        email : String ,
        password : String
    ) : LiveData<Result<FirebaseUser?>> = liveData {
        emit(Result.Loading)
        try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val user = authResult.user

            user?.let { currentUser->
                emit(Result.Success(user))

                val uid = currentUser.uid
                val userRef = usersReference.child(uid)
                val userData = UserModel(
                    email = email,
                    name = users,
                )

                userRef.child("personal_data").setValue(userData).await()
            }
        }catch (e: Exception){
            emit(Result.Error(handleAuthException(e)))
            Log.e("BoxRepository", handleAuthException(e))
        }
    }

    fun logOut() {
        auth.signOut()
    }

    fun getUserDataByUID(uid: String): LiveData<Result<UserModel>> = liveData {
        emit(Result.Loading)
        try {
            val userDataRef = usersReference.child(uid)
            val snapshot = userDataRef.child("personal_data").get().await()

            val dataByUID = snapshot.getValue(UserModel::class.java)

            if (dataByUID != null) {
                emit(Result.Success(dataByUID))
            }else{
                emit(Result.Error("User data not found for UID: $uid"))
            }
        } catch (e: Exception) {
            emit(Result.Error("Failed to fetch user data: ${e.message}"))
            Log.e("UserDataRepository", "getUserDataByUID: ${e.message}", e)
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

    //read
    fun getAllBox(uid: String): LiveData<Result<List<CategoryModel>>> = liveData {
        emit(Result.Loading)
        try {
            val uidRef = usersReference.child(uid)

            val snapshot = uidRef.child("Category").get().await()

            val boxes = snapshot.children.mapNotNull { categorySnapshot->
                categorySnapshot.getValue(CategoryModel::class.java)
            }
            boxDatabase.boxDao().insertCategory(boxes)
            emit(Result.Success(boxes))
        }catch (e: Exception) {
            Log.e("BoxRepository", "getAllBox: ${e.message.toString()}")
            emit(Result.Error(e.message.toString()))
        }
    }

    fun getBoxById(
        uid : String ,
        id : Int
    ): LiveData<Result<CategoryModel>> = liveData {
        emit(Result.Loading)
        try {
            val snapshot = uidRef(uid)
                .child("Category")
                .child(id.toString())
                .get()
                .await()

            val boxById = snapshot.getValue(CategoryModel::class.java)

            if (boxById != null) {
                boxDatabase.boxDao().getCategoriesById(id)
                emit(Result.Success(boxById))
            } else {
                emit(Result.Error("Failed to parse data"))
            }
        }catch (e: Exception) {
            emit(Result.Error(e.message ?: "An error occurred"))
            Log.e("BoxRepository", "getBoxById: ${e.message.toString()}")
        }
    }

    fun getAllDetailBox(
        uid: String,
        categoryId : Int
    ): LiveData<Result<List<DetailModel>>> = liveData {
        emit(Result.Loading)
        try{
            val categorySnapshot = uidRef(uid)
                .child("Category")
                .child(categoryId.toString())
                .child("items")
                .get()
                .await()

            val detailItems = categorySnapshot.children.mapNotNull {
                it.getValue(DetailModel::class.java)
            }

            boxDatabase.detailDao().insertDetails(detailItems)
            emit(Result.Success(detailItems))
        }catch (e: Exception) {
            Log.e("BoxRepository", "getAllDetailBox: ${e.message.toString()}")
            emit(Result.Error(e.message ?: "An error occurred"))
        }
    }

    fun getDetailBoxById(
        uid: String,
        id: Int,
        categoryId : Int
    ) : LiveData<Result<DetailModel>> = liveData {
        emit(Result.Loading)
        try {
            val itemSnapshot = uidRef(uid)
                .child("Category")
                .child(categoryId.toString())
                .child("items")
                .child(id.toString())
                .get()
                .await()

            if (itemSnapshot.exists()) {
                val detailItem = itemSnapshot.getValue(DetailModel::class.java)

                detailItem?.let {
                    emit(Result.Success(detailItem))
                } ?: kotlin.run {
                    emit(Result.Error("Something not right :("))
                }
            } else {
                emit(Result.Error("Something not right :("))
            }

        }catch (e: Exception){
            Log.e("BoxRepository", "getDetailItem: ${e.message.toString()}")
            emit(Result.Error(e.message.toString()))
        }
    }

    //create-update
    suspend fun addNewCategories(
        uid : String ,
        category : CategoryModel ,
        fileImage : Uri
    ): CategoryModel {
        try {
            val snapshot = uidRef(uid).child("Category").get().await()

            val nextKey = if (!snapshot.exists() || category.id == 0 ) {
                val maxKey = snapshot.children.maxOfOrNull { it.key?.toInt() ?: 0 } ?: 0
                maxKey + 1
            } else {
                category.id //if item edit will returned the previous id
            }

            val newCategoryRef = uidRef(uid).child(
                "Category"
            ).child(nextKey.toString())

            val categoryWithKey = category.copy(
                id = nextKey
            )

//            newCategoryRef.setValue(
//                categoryWithKey
//            ).await()

            val imageUrl = if (fileImage.scheme == "file") {
                val fileRef: StorageReference = FirebaseStorage
                    .getInstance()
                    .getReference("user_$uid/image_category/category_${nextKey}/image_${nextKey}.jpg")

                fileRef.putFile(fileImage)
                    .addOnProgressListener { taskSnapshot ->
                        val progress = ((100.0 * taskSnapshot.bytesTransferred) / taskSnapshot.totalByteCount).toInt()
                        _uploadProgress.postValue(progress)
                    }
                    .await()

                fileRef.downloadUrl.await().toString()
            } else {
                fileImage.toString()
            }

            val updatedCategory = categoryWithKey.copy(imageURL = imageUrl)
            val updatedMap = mapOf(
                "id" to updatedCategory.id,
                "name" to updatedCategory.name,
                "description" to updatedCategory.description,
                "imageURL" to updatedCategory.imageURL
            )

            newCategoryRef.updateChildren(updatedMap).await()

            boxDatabase.boxDao().insertCategory(listOf(updatedCategory))

            Log.d("BoxRepository", "New category added successfully.")

            return updatedCategory
        } catch (e: Exception) {
            Log.e("BoxRepository", "addNewCategory: ${e.message.toString()}")
            throw  e
        }
    }

    suspend fun addNewDetailItem(
        uid : String ,
        categoryId : Int ,
        newDetailItem : DetailModel ,
        fileImage : Uri
    ): DetailModel {
        try {
            val categoryRefFromCategoryId = uidRef(uid)
                .child("Category")
                .child(categoryId.toString())

            val itemsRef = categoryRefFromCategoryId.child("items")

            val snapshotFromItem = itemsRef.get().await()

            val nextKey = if (!snapshotFromItem.exists() || newDetailItem.id == 0) {
                val maxKey = snapshotFromItem.children.maxOfOrNull { it.key?.toInt() ?: 0 } ?: 0
                maxKey + 1
            } else {
                newDetailItem.id //if item edit will returned the previous id
            }

            val itemRef = itemsRef.child(nextKey.toString())

            val itemWithKey = newDetailItem.copy(
                id = nextKey
            )

            itemRef.setValue(itemWithKey).await()

            val imageUrl = if (fileImage.scheme == "file") {
                val fileRef: StorageReference = FirebaseStorage
                    .getInstance()
                    .getReference("user_$uid/image_category/category_${categoryId}/itemImg_$nextKey.jpg")

                fileRef.putFile(fileImage)
                    .addOnProgressListener { taskSnapshot ->
                        val progress = ((100.0 * taskSnapshot.bytesTransferred) / taskSnapshot.totalByteCount).toInt()
                        _uploadProgress.postValue(progress)
                    }
                    .await()
                fileRef.downloadUrl.await().toString()
            }else{
                fileImage.toString()
            }

            val updateDetailItem = itemWithKey.copy(imageURL = imageUrl)
            itemRef.setValue(updateDetailItem).await()

            boxDatabase.detailDao().insertDetails(mutableListOf(updateDetailItem))

            Log.d("BoxRepository", "Detail item added successfully")

            return updateDetailItem
        }catch (e: Exception){
            Log.e("BoxRepository", "Error adding detail item: ${e.message}")
            throw e
        }
    }

    //delete

    private val deleteBoxLiveData: MutableLiveData<Result<Unit>> = MutableLiveData()
    fun deleteBox(uid:String, box : CategoryModel, itemId: Int) {
        deleteBoxLiveData.value = Result.Loading

        executor.execute {
            try {
                boxDatabase.boxDao().delete(box)

                uidRef(uid)
                    .child("Category")
                    .child(itemId.toString())
                    .removeValue()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            deleteBoxLiveData.postValue(Result.Success(Unit))
                            val storageRef = storage.child("user_$uid/image_category/category_${itemId}/")
                            deleteFolder(storageRef)
                        }
                    }
                    .addOnFailureListener { exception ->
                        deleteBoxLiveData.postValue(Result.Error(exception.message ?: "An error occurred"))
                    }

            } catch (e: Exception) {
                deleteBoxLiveData.postValue(Result.Error("An error occurred: ${e.message}"))
            }
        }
    }

    private fun deleteFolder(folderRef: StorageReference) {
        folderRef.listAll().addOnSuccessListener { listResult ->
            listResult.items.forEach { item ->
                item.delete()
            }
            listResult.prefixes.forEach { prefix ->
                deleteFolder(prefix)
            }
        }.addOnFailureListener {exception->
            deleteBoxLiveData.postValue(Result.Error(exception.message ?: "An error occurred"))
        }
    }

    private val deleteBoxItemLiveData: MutableLiveData<Result<Unit>> = MutableLiveData()
    fun deleteDetailBox(uid: String, detailBox : DetailModel, categoryId: Int, itemId : Int) {
        deleteBoxItemLiveData.value = Result.Loading

        executor.execute {
            try {
                boxDatabase.detailDao().deleteBox(detailBox)

                uidRef(uid)
                    .child("Category")
                    .child(categoryId.toString())
                    .child("items")
                    .child(itemId.toString())
                    .removeValue()
                    .addOnCompleteListener { task->
                        if (task.isSuccessful) {
                            deleteBoxItemLiveData.postValue(Result.Success(Unit))
                            storage
                                .child("user_$uid/image_category/category_${categoryId}/itemImg_$itemId.jpg")
                                .delete()
                        }else{
                            deleteBoxItemLiveData.postValue(Result.Error("Failed to delete from Firebase"))
                        }
                    }
                    .addOnFailureListener { exception->
                        deleteBoxItemLiveData.postValue(Result.Error(exception.message ?: "An error occurred"))
                    }
            } catch (e: Exception) {
                deleteBoxItemLiveData.postValue(Result.Error("An error occurred: ${e.message}"))
            }
        }
    }

}