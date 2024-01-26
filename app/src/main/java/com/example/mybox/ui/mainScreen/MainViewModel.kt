package com.example.mybox.ui.mainScreen

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mybox.R
import com.example.mybox.data.BoxRepository
import com.example.mybox.data.model.CategoryModel
import com.example.mybox.utils.Event
import com.example.mybox.utils.SharedPreferences
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainViewModel(private val boxRepository : BoxRepository): ViewModel() {
    private val _snackbarText = MutableLiveData<Event<Int>>()
    val snackbarText : LiveData<Event<Int>> = _snackbarText

    private var retryCount = 0
    private val maxRetryCount = 5
    private val timeoutMillis = 5000

    fun getBox(uid : String) = boxRepository.getAllBox(uid)

    private fun logOut() = boxRepository.logOut()

    fun retryOrLogout(uid : String, context : Context) {
        viewModelScope.launch {
            if (retryCount < maxRetryCount) {
                delay(timeoutMillis.toLong())
                getBox(uid)
                retryCount++
            }else{
                logOut()
                SharedPreferences().logOut(context)
            }
        }
    }

    fun deleteBox(uid : String , box : CategoryModel , id : Int?) {
        viewModelScope.launch {
            if (id != null) {
                boxRepository.deleteBox(uid = uid , box = box , itemId = id)
            }
            _snackbarText.value = Event(R.string.box_deleted)
        }
    }

    fun getUserDataByUID(uid : String) = boxRepository.getUserDataByUID(uid)

}
