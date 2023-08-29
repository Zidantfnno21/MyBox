package com.example.mybox.ui.mainScreen

import android.net.Uri
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


    fun getBox() = boxRepository.getAllBox()

    fun deleteBox(box: CategoryModel) {
        boxRepository.deleteBox(box)
        _snackbarText.value = Event(R.string.box_deleted)
    }

}