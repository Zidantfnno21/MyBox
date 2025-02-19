package com.example.mybox.ui.screen.auth.loginScreen

import androidx.lifecycle.ViewModel
import com.example.mybox.data.repository.BoxRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val boxRepository : BoxRepository) : ViewModel() {
    fun login(email: String, password: String) = boxRepository.logIn(email, password)

}