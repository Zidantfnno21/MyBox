package com.example.mybox.ui.screen.auth.registerScreen

import androidx.lifecycle.ViewModel
import com.example.mybox.data.repository.BoxRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(private val boxRepository : BoxRepository) : ViewModel() {
    fun register(name: String, email: String, password: String) =
        boxRepository.register(
            users = name ,
            email = email ,
            password = password
    )
}