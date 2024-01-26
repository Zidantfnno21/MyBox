package com.example.mybox.ui.detailScreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mybox.R
import com.example.mybox.data.BoxRepository
import com.example.mybox.data.model.CategoryModel
import com.example.mybox.data.model.DetailModel
import com.example.mybox.utils.Event
import kotlinx.coroutines.launch

class CategoryDetailViewModel(private val boxRepository : BoxRepository) : ViewModel() {
    private val _snackBarText = MutableLiveData<Event<Int>>()
    val snackBarText: LiveData<Event<Int>> = _snackBarText

    fun getDetailBox(uid: String, categoryId: Int) = boxRepository.getAllDetailBox(
        uid = uid ,
        categoryId = categoryId
    )

    fun getCategoriesBoxById(uid: String,id: Int) = boxRepository.getBoxById(uid = uid , id = id)

    fun deleteBox(uid : String , box : CategoryModel , id : Int?) {
        viewModelScope.launch {
            if (id != null) {
                boxRepository.deleteBox(uid = uid , box = box , itemId = id)
            }
            _snackBarText.value = Event(R.string.box_deleted)
        }
    }

    fun deleteDetailBox(uid: String, item: DetailModel, categoryId : Int, itemId: Int){
        boxRepository.deleteDetailBox(
            uid = uid ,
            detailBox = item ,
            categoryId = categoryId ,
            itemId = itemId
        )
        _snackBarText.value = Event(R.string.box_deleted)

    }
}