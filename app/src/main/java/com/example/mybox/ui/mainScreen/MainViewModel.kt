package com.example.mybox.ui.mainScreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mybox.R
import com.example.mybox.data.BoxRepository
import com.example.mybox.data.model.CategoryModel
import com.example.mybox.utils.Event
import kotlinx.coroutines.launch

class MainViewModel(private val boxRepository : BoxRepository): ViewModel() {
    private val _snackbarText = MutableLiveData<Event<Int>>()
    val snackbarText: LiveData<Event<Int>> = _snackbarText

    private val _undo = MutableLiveData<Event<CategoryModel>>()
    val undo: LiveData<Event<CategoryModel>> = _undo

    fun getBox() = boxRepository.getAllBox()

    fun insert(box: CategoryModel) {
        viewModelScope.launch {
            boxRepository.addNewCategories(box)
        }
    }

    fun deleteBox(box: CategoryModel) {
        boxRepository.deleteBox(box)
        _snackbarText.value = Event(R.string.box_deleted)
        _undo.value = Event(box)
    }

}