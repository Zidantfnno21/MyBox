package com.example.mybox.ui.addItemScreen

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mybox.data.BoxRepository
import com.example.mybox.data.Result
import com.example.mybox.data.model.DetailModel
import kotlinx.coroutines.launch

class AddItemViewModel(private val boxRepository : BoxRepository) : ViewModel() {
    private val _uploadProgress = boxRepository.uploadProgress
    val uploadProgress: LiveData<Int>
        get() = _uploadProgress

    fun postItem(
        uid: String,
        file: Uri,
        item: DetailModel,
        categoryId: Int,
        callback : (Result<DetailModel>) -> Unit,
    ) {
        viewModelScope.launch {
            try {
                callback(Result.Loading)

                val detail = boxRepository.addNewDetailItem(uid, categoryId, item, file)

                callback(Result.Success(detail))
            }catch (e: Exception){
                callback(Result.Error(e.message.toString()))
            }
        }
    }
}