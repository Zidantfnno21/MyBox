package com.example.mybox.ui.auth.loginScreen

import androidx.lifecycle.ViewModel
import com.example.mybox.data.BoxRepository

class LoginViewModel(private val boxRepository : BoxRepository) : ViewModel() {
    fun login(email: String, password: String) = boxRepository.logIn(email, password)

}