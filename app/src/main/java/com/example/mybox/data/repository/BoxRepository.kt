package com.example.mybox.data.repository

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import com.example.mybox.data.Result
import com.example.mybox.data.database.BoxDatabase
import com.example.mybox.data.model.CategoryModel
import com.example.mybox.data.model.DetailModel
import com.example.mybox.data.model.UserModel
import com.example.mybox.utils.FileUtils
import com.example.mybox.utils.NetworkUtils
import com.example.mybox.utils.PreferencesHelper
import com.example.mybox.utils.SyncScheduler
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
import java.util.concurrent.Executor
import javax.inject.Inject

class BoxRepository @Inject constructor(
    private val fileUtils: FileUtils,
    private val networkUtils: NetworkUtils,
    private val boxDatabase: BoxDatabase,
    private val executor: Executor,
    private val syncScheduler: SyncScheduler,
    private val preferencesHelper: PreferencesHelper,
) {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val storage = FirebaseStorage.getInstance().reference
    private val usersReference: DatabaseReference = Firebase.database.getReference("Users")
    private fun uidRef(uid: String) = usersReference.child(uid)
    private val _uploadProgress = MutableLiveData<Int>()
    private val uid = preferencesHelper.getSavedUid()

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
                preferencesHelper.saveUid(uid)
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
    fun getAllBox(
        uid: String,
        lastKey: String?,
        size: Int,
    ): LiveData<Result<List<CategoryModel>>> = liveData {
        emit(Result.Loading)
        try {
            val uidRef = usersReference.child(uid).child("Category")
            val query = if (lastKey == null) {
                uidRef.orderByKey().limitToFirst(size)
            } else {
                uidRef.orderByKey().startAfter(lastKey).limitToFirst(size)
            }

            val snapshot = query.get().await()

            val boxes = snapshot.children.mapNotNull { categorySnapshot->
                categorySnapshot.getValue(CategoryModel::class.java)
            }

            if (boxes.isNotEmpty()) {
                boxDatabase.boxDao().insertCategory(boxes)
                emitSource(
                    boxDatabase.boxDao().getCategories().map { list ->
                        Result.Success(list)
                    }
                )
            } else {
                emit(
                    Result.Success(emptyList()),
                )
            }

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
                emitSource(
                    boxDatabase.boxDao().getCategoriesById(id).map { list->
                        Result.Success(list)
                    }
                )
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
            emitSource(
                boxDatabase.detailDao().getDetailsByCategoryId(categoryId = categoryId).map { list->
                    Result.Success(list)
                }
            )
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
                val detailItem = itemSnapshot.children.mapNotNull { boxItemSnapshot->
                    boxItemSnapshot.getValue(DetailModel::class.java)
                }

                detailItem.let {
                    boxDatabase.detailDao().insertDetails(detailItem)
                    emitSource(
                        boxDatabase.detailDao()
                            .getDetailsByCategoryIdAndIds(categoryId = categoryId, boxId = id)
                            .map { list->
                                Result.Success(list)
                        }
                    )
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
        category : CategoryModel ,
        fileImage : Uri
    ): Result<Unit> {
        try {
            /*
            val snapshot = uidRef(uid).child("Category").get().await()

            val nextKey = if (!snapshot.exists() || category.id == 0 ) {
                val maxKey = snapshot.children.maxOfOrNull { it.key?.toInt() ?: 0 } ?: 0
                maxKey + 1
            } else {
                category.id
            }

            val newCategoryRef = uidRef(uid).child(
                "Category"
            ).child(nextKey.toString())

            val categoryWithKey = category.copy(
                id = nextKey,
                isSynced = false
            )

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

            val updatedCategory = categoryWithKey.copy(imageURL = imageUrl, isSynced = true)
            val updatedMap = mapOf(
                "id" to updatedCategory.id,
                "name" to updatedCategory.name,
                "description" to updatedCategory.description,
                "imageURL" to updatedCategory.imageURL
            )

            newCategoryRef.updateChildren(updatedMap).await()

            boxDatabase.boxDao().insertCategory(listOf(updatedCategory))

            Log.d("BoxRepository", "New category added successfully.")

            return Result.Success(Unit)

            */

            /// new approach
            val nextKey = (boxDatabase.boxDao().getMaxCategoryId() ?: 0) + 1
            val cachedImagePath = "category_${nextKey}.jpg"
            fileUtils.createAndWriteToCache(cachedImagePath, fileImage.toString()) { success ->
                if (!success) Log.e("BoxRepository", "Failed to cache category image.")
            }
            val categoryWithKey = category.copy(
                id = nextKey,
                imageURL = cachedImagePath,
                isSynced = false
            )
            boxDatabase.boxDao().insertCategory(listOf(categoryWithKey))
            syncScheduler.scheduleCategorySync()
            Log.d("BoxRepository", "New category added locally.")
            return Result.Success(Unit)
        } catch (e: Exception) {
            Log.e("BoxRepository", "addNewCategory: ${e.message.toString()}")
            return Result.Error(e.toString())
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
                        deleteBoxLiveData.postValue(
                            Result.Error(
                                exception.message ?: "An error occurred"
                            )
                        )
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
                        deleteBoxItemLiveData.postValue(
                            Result.Error(
                                exception.message ?: "An error occurred"
                            )
                        )
                    }
            } catch (e: Exception) {
                deleteBoxItemLiveData.postValue(Result.Error("An error occurred: ${e.message}"))
            }
        }
    }

    suspend fun syncUnsyncedCategories() {
        if (!networkUtils.isInternetAvailable()) {
            Log.d("BoxRepository", "No internet, skipping sync.")
            return
        }

        val unsyncedCategories = boxDatabase.boxDao().getUnsyncedCategories()
        for (category in unsyncedCategories) {
            try {
                val newCategoryRef = uidRef(uid).child("Category").child(category.id.toString())

                val imageFile = try {
                    fileUtils.getCached(category.imageURL ?: "")
                } catch (e: Exception) {
                    Log.e("BoxRepository", "Failed to read cached image: ${e.message}")
                    null
                }

                val imageUrl = if (imageFile != null) {
                    val fileRef: StorageReference = FirebaseStorage
                        .getInstance()
                        .getReference("user_${uid}/image_category/category_${category.id}/image.jpg")

                    fileRef.putFile(Uri.fromFile(imageFile)).await()
                    fileRef.downloadUrl.await().toString()
                } else {
                    category.imageURL
                }

                val updatedCategory = category.copy(imageURL = imageUrl, isSynced = true)
                val updatedMap = mapOf(
                    "id" to updatedCategory.id,
                    "name" to updatedCategory.name,
                    "description" to updatedCategory.description,
                    "imageURL" to updatedCategory.imageURL
                )

                newCategoryRef.updateChildren(updatedMap).await()

                updatedCategory.id?.let { boxDatabase.boxDao().updateCategorySyncStatus(it, true) }

                Log.d("BoxRepository", "Synced category ${category.id} to Firebase.")
            } catch (e: Exception) {
                Log.e("BoxRepository", "Sync error: ${e.message}")
            }
        }
    }

}