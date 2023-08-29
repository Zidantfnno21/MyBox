package com.example.mybox.ui.detailScreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mybox.R
import com.example.mybox.data.BoxRepository
import com.example.mybox.data.model.DetailModel
import com.example.mybox.utils.Event

class CategoryDetailViewModel(private val boxRepository : BoxRepository) : ViewModel() {
    private val _snackbarText = MutableLiveData<Event<Int>>()
    val snackbarText: LiveData<Event<Int>> = _snackbarText

    fun getDetailBox(categoryId: Int) = boxRepository.getAllDetailBox(categoryId)

    fun deleteDetailBox(item: DetailModel){
        boxRepository.deleteDetailBox(item)
        _snackbarText.value = Event(R.string.box_deleted)
    }
}