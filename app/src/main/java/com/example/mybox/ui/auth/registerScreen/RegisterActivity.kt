package com.example.mybox.ui.auth.registerScreen

import android.content.Intent
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.example.mybox.R
import com.example.mybox.data.Result
import com.example.mybox.databinding.ActivityRegisterBinding
import com.example.mybox.ui.ViewModelFactory
import com.example.mybox.ui.auth.loginScreen.LoginActivity
import com.example.mybox.utils.hideKeyboard
import com.example.mybox.utils.isValidEmail
import com.example.mybox.utils.makeToast

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private val viewModel by viewModels<RegisterViewModel> {
        ViewModelFactory.getInstance(application)
    }
    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        enableEdgeToEdge()

        binding.tilRegisterPassword.setEndIconOnClickListener {
            val cursorPosition = binding.etRegisterPassword.selectionStart

            if (binding.etRegisterPassword.transformationMethod is PasswordTransformationMethod) {
                binding.etRegisterPassword.transformationMethod = null
                binding.tilRegisterPassword.endIconDrawable = ContextCompat.getDrawable(this , R.drawable.ic_visibility_on_24)
            } else {
                binding.etRegisterPassword.transformationMethod = PasswordTransformationMethod.getInstance()
                binding.tilRegisterPassword.endIconDrawable = ContextCompat.getDrawable(this, R.drawable.ic_visibility_off_24)
            }

            binding.etRegisterPassword.setSelection(cursorPosition)
        }

        binding.tvLoginRegisBt.setOnClickListener {
            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.btRegister.setOnClickListener {
            register()
        }
    }

    private fun register() {
        val username = binding.etRegisterUsername.text.toString()
        if (username.isEmpty()) {
            binding.tilRegisterUsername.error = "Username Cannot be Empty"
        }

        val email = binding.etRegisterEmail.text.toString()
        if (!isValidEmail(email)) {
            binding.tilRegisterEmail.error = "Email not valid"
        }

        val password = binding.etRegisterPassword.text.toString()
        if (password.length < 8) {
            binding.tilRegisterPassword.isEndIconVisible = false
            binding.tilRegisterPassword.error = "Password Cannot less than 8 Character"
        }else{
            binding.tilRegisterPassword.isEndIconVisible = true
            binding.tilRegisterPassword.error = null
        }


        if (username.isNotEmpty() && password.isNotEmpty() && password.length >= 8) {
            viewModel.register(
                name = username ,
                email = email ,
                password = password
            ).observe(this) { result->
                if (result != null){
                    when(result){
                        is Result.Loading -> {
                            hideKeyboard()
                            showLoading(true)
                        }
                        is Result.Success -> {
                            showLoading(false)
                            Toast.makeText(this@RegisterActivity , "Register Success!! Please Login" , Toast.LENGTH_SHORT)
                                .show()
                            val i = Intent(this@RegisterActivity, LoginActivity::class.java)
                            startActivity(i)
                            finish()
                        }
                        is Result.Error -> {
                            showLoading(false)
                            val errorMessage = result.error
                            makeToast(this, errorMessage)
                        }
                    }
                }
            }
        }
    }

    private fun showLoading(loading : Boolean) {
        binding.progressBar.isVisible = loading
        if (loading){
            window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE , WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        }
    }

}