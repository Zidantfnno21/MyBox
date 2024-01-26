package com.example.mybox.ui.auth.loginScreen

import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.isVisible
import com.example.mybox.R
import com.example.mybox.data.Result
import com.example.mybox.databinding.ActivityLoginBinding
import com.example.mybox.ui.ViewModelFactory
import com.example.mybox.ui.auth.registerScreen.RegisterActivity
import com.example.mybox.ui.mainScreen.MainActivity
import com.example.mybox.utils.SharedPreferences
import com.example.mybox.utils.hideKeyboard
import com.example.mybox.utils.isValidEmail
import com.example.mybox.utils.makeToast
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.appcheck.ktx.appCheck
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {
    private lateinit var binding : ActivityLoginBinding
    private val viewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(application)
    }
    override fun onStart() {
        super.onStart()
        val savedTheme = SharedPreferences().getTheme(this)
        AppCompatDelegate.setDefaultNightMode(savedTheme)

        if (SharedPreferences().isTokenAvailable(applicationContext)) {
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        FirebaseApp.initializeApp(this)
        Firebase.appCheck.installAppCheckProviderFactory(
            DebugAppCheckProviderFactory.getInstance() ,
        )
    }

    override fun onCreate(savedInstanceState : Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        enableEdgeToEdge()

        binding.tilLoginPassword.setEndIconOnClickListener {
            val cursorPosition = binding.etLoginPassword.selectionStart
            if (binding.etLoginPassword.transformationMethod is PasswordTransformationMethod) {
                binding.etLoginPassword.transformationMethod = null
                binding.tilLoginPassword.endIconDrawable = ContextCompat.getDrawable(this, R.drawable.ic_visibility_on_24)
            } else {
                binding.etLoginPassword.transformationMethod = PasswordTransformationMethod.getInstance()
                binding.tilLoginPassword.endIconDrawable = ContextCompat.getDrawable(this , R.drawable.ic_visibility_off_24)
            }

            binding.etLoginPassword.setSelection(cursorPosition)
        }

        binding.tvLoginRegisBt.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }

        binding.btLogin.setOnClickListener {
            login()
        }

    }

    private fun login() {
        val username = binding.etLoginEmail.text.toString()
        if (!isValidEmail(username)) {
           binding.tilLoginEmail.error = "Email not valid"
        }
        
        val password = binding.etLoginPassword.text.toString()
        if (password.length < 8) {
            binding.tilLoginPassword.isEndIconVisible = false
            binding.tilLoginPassword.error = "Password Cannot less than 8 Character"
        }else{
            binding.tilLoginPassword.isEndIconVisible = true
            binding.tilLoginPassword.error = null
        }

        if (username.isNotEmpty() && password.isNotEmpty() && password.length >= 8) {
            viewModel.login(username, password).observe(this) {result->
                if (result != null){
                    when(result){
                        is Result.Loading -> {
                            hideKeyboard()
                            showLoading(true)
                        }
                        is Result.Success -> {
                            showLoading(false)
                            generateLogin(result.data)
                        }
                        is Result.Error -> {
                            showLoading(false)
                            makeToast(this, result.error)
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

    private fun generateLogin(result : FirebaseUser?) {
        result?.getIdToken(true)?.addOnCompleteListener { task->
            if (task.isSuccessful) {
                val userResult = task.result
                val userToken = userResult.token
                val userUID = result.uid

                if (userToken != null) {
                    saveUserToken(userToken)
                    saveUserInfo(userUID)
                }else{
                    Log.e(TAG, "User token is null")
                }
            }
        }
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this@LoginActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun saveUserToken(token : String) {
        SharedPreferences().saveToken(token , applicationContext)
    }

    private fun saveUserInfo(user : String) {
        try{
            SharedPreferences().saveUser(user , applicationContext)

            navigateToMainActivity()

        }catch (e: Exception){
            e.printStackTrace()
        }
    }


    private fun showLoading(loading : Boolean) {
        binding.progressBar.background = (ColorDrawable(Color.parseColor("#63616161")))
        binding.progressBar.isVisible = loading
        if (loading){
            window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE , WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        }
    }
}