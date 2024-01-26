package com.example.mybox.ui.auth.registerScreen

import androidx.lifecycle.ViewModel
import com.example.mybox.data.BoxRepository

class RegisterViewModel(private val boxRepository : BoxRepository) : ViewModel() {
    fun register(name: String, email: String, password: String) =
        boxRepository.register(
            users = name ,
            email = email ,
            password = password
    )
}