package com.example.mybox.ui.auth.loginScreen

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
import com.example.mybox.data.Result
import com.example.mybox.databinding.ActivityLoginBinding
import com.example.mybox.ui.ViewModelFactory
import com.example.mybox.ui.auth.registerScreen.RegisterActivity
import com.example.mybox.ui.mainScreen.MainActivity
import com.example.mybox.utils.SessionPreferences
import com.google.firebase.auth.FirebaseUser

class LoginActivity : AppCompatActivity() {
    private lateinit var binding : ActivityLoginBinding
    private val viewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(application)
    }
    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            val window : Window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.WHITE
        }

        binding.tvLoginRegisBt.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }

        binding.btLogin.setOnClickListener {
            val username = binding.etLoginUsername.text.toString()
            val password = binding.etLoginPassword.text.toString()

            if (username.isNotEmpty() && password.isNotEmpty() && password.length >=8) {
                viewModel.login(username, password).observe(this) {result->
                    if (result != null){
                        when(result){
                            is Result.Loading -> {
                                showLoading(true)
                            }
                            is Result.Success -> {
                                showLoading(false)
                                val user = result.data
                                generateLogin(user)
                            }
                            is Result.Error -> {
                                showLoading(false)
                                Toast.makeText(this , "Something went wrong, try again later!" , Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            } else if (password.length <= 8) {
                Toast.makeText(this , "Password must more than 8 Chara" , Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this , "Form cannot be empty!!" , Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun onStart() {
        super.onStart()
        if (SessionPreferences().isTokenAvailable(applicationContext)) {
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun generateLogin(data : FirebaseUser?) {
        SessionPreferences().saveToken(data?.getIdToken(true)?.result?.token.toString() , applicationContext)
        val intent = Intent(this@LoginActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun showLoading(loading : Boolean) {
        binding.progressBar.isVisible = loading
    }
}