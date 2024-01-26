package com.example.mybox.settings

import androidx.lifecycle.ViewModel
import com.example.mybox.data.BoxRepository

class SettingsViewModel(private val boxRepository : BoxRepository): ViewModel() {

    fun logOut() = boxRepository.logOut()
}