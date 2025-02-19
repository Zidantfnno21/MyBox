package com.example.mybox.ui.screen.mainScreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mybox.R
import com.example.mybox.data.model.CategoryModel
import com.example.mybox.data.repository.BoxRepository
import com.example.mybox.utils.Event
import com.example.mybox.utils.PreferencesHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val boxRepository : BoxRepository,
    private val preferencesHelper: PreferencesHelper,
): ViewModel() {
    private val _snackbarText = MutableLiveData<Event<Int>>()
    val snackbarText : LiveData<Event<Int>> = _snackbarText

    private val _boxList = MutableLiveData<Result<List<CategoryModel>>>()
    val boxList: LiveData<Result<List<CategoryModel>>> = _boxList

    private var lastKey: String? = null
    private var size: Int = 5
    private var retryCount = 0
    private val maxRetryCount = 5
    private val timeoutMillis = 5000

    fun getBox(uid : String) = boxRepository.getAllBox(uid,lastKey, size)

    fun loadNextPage(uid: String, size: Int) {
        boxRepository.getAllBox(uid, lastKey, size).observeForever { result ->

        }
    }

    private fun logOut() = boxRepository.logOut()

    fun retryOrLogout(uid : String) {
        viewModelScope.launch {
            if (retryCount < maxRetryCount) {
                delay(timeoutMillis.toLong())
                getBox(uid)
                retryCount++
            }else{
                logOut()
                preferencesHelper.logOut()
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
