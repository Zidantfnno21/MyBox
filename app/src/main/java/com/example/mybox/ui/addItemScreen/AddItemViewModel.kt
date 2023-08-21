package com.example.mybox.ui.addItemScreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mybox.data.BoxRepository
import com.example.mybox.utils.Event

class AddItemViewModel(private val boxRepository : BoxRepository) : ViewModel() {
    private val _snackbarText = MutableLiveData<Event<Int>>()
    val snackbarText: LiveData<Event<Int>> = _snackbarText

}