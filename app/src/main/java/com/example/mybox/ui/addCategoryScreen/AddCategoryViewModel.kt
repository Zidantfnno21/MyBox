package com.example.mybox.ui.addCategoryScreen

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mybox.data.Result
import com.example.mybox.data.model.CategoryModel
import com.example.mybox.data.repository.BoxRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddCategoryViewModel @Inject constructor(private val boxRepository : BoxRepository): ViewModel() {
    private val _uploadProgress = boxRepository.uploadProgress
    val uploadProgress: LiveData<Int>
        get() = _uploadProgress

    fun postCategory(
        file: Uri,
        category: CategoryModel,
        callback : (Result<Unit>) -> Unit
    ) {
        viewModelScope.launch {
            try {
                callback(Result.Loading)

                boxRepository.addNewCategories(category = category , fileImage = file)

                callback(Result.Success(Unit))
            } catch (e: Exception) {
                callback(Result.Error(e.message.toString()))
            }
        }
    }

}