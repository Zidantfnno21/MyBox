package com.example.mybox.ui.auth.registerScreen

import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.isVisible
import com.example.mybox.R
import com.example.mybox.data.Result
import com.example.mybox.databinding.ActivityRegisterBinding
import com.example.mybox.ui.ViewModelFactory
import com.example.mybox.ui.auth.loginScreen.LoginActivity
import com.example.mybox.ui.auth.loginScreen.LoginViewModel

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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            val window : Window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.WHITE
        }

        binding.tvLoginRegisBt.setOnClickListener {
            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.btRegister.setOnClickListener {
            val username = binding.etRegisterUsername.text.toString()
            val password = binding.etRegisterPassword.text.toString()

            if (username.isNotEmpty() && password.isNotEmpty() && password.length >=8) {
                viewModel.register(username,password).observe(this) {result->
                    if (result != null){
                        when(result){
                            is Result.Loading -> {
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
                            }
                        }
                    }
                }
            }


        }
    }

    private fun showLoading(loading : Boolean) {
        binding.progressBar.isVisible = loading
    }
}