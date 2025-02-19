package com.example.mybox.ui.detailItemScreen

import androidx.lifecycle.ViewModel
import com.example.mybox.data.repository.BoxRepository
import javax.inject.Inject

class DetailItemViewModel @Inject constructor(private val boxRepository : BoxRepository) : ViewModel() {

    fun getDetailBox(uid: String, id: Int, categoryId: Int) =
        boxRepository.getDetailBoxById(uid = uid , id = id , categoryId = categoryId)
}