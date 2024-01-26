package com.example.mybox.ui.addCategoryScreen

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mybox.data.BoxRepository
import com.example.mybox.data.Result
import com.example.mybox.data.model.CategoryModel
import kotlinx.coroutines.launch

class AddCategoryViewModel(private val boxRepository : BoxRepository): ViewModel() {
    private val _uploadProgress = boxRepository.uploadProgress
    val uploadProgress: LiveData<Int>
        get() = _uploadProgress

    fun postCategory(
        uid: String,
        file: Uri,
        category: CategoryModel,
        callback : (Result<CategoryModel>) -> Unit
    ) {
        viewModelScope.launch {
            try {
                callback(Result.Loading)

                val cat = boxRepository.addNewCategories(uid = uid , category = category , fileImage = file)

                callback(Result.Success(cat))
            } catch (e: Exception) {
                callback(Result.Error(e.message.toString()))
            }
        }
    }

}