package com.example.mybox.ui.screen.auth.loginScreen

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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.isVisible
import com.example.mybox.R
import com.example.mybox.data.Result
import com.example.mybox.databinding.ActivityLoginBinding
import com.example.mybox.ui.components.AuthTextField
import com.example.mybox.ui.components.BigFilledButton
import com.example.mybox.ui.components.InputType
import com.example.mybox.ui.screen.auth.registerScreen.RegisterActivity
import com.example.mybox.ui.screen.mainScreen.MainActivity
import com.example.mybox.utils.PreferencesHelper
import com.example.mybox.utils.hideKeyboard
import com.example.mybox.utils.isValidEmail
import com.example.mybox.utils.makeToast
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.appcheck.ktx.appCheck
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    @Inject
    lateinit var preferencesHelper: PreferencesHelper
    private lateinit var binding : ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()
    override fun onStart() {
        super.onStart()
        val savedTheme = preferencesHelper.getTheme()
        AppCompatDelegate.setDefaultNightMode(savedTheme)

        if (preferencesHelper.isTokenAvailable()) {
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
        preferencesHelper.saveToken(token)
    }

    private fun saveUserInfo(user : String) {
        try{
            preferencesHelper.saveUser(user)

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

/// Compose view migration on going...

@Composable
fun LoginScreen() {
    val keyboardController = LocalSoftwareKeyboardController.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "We've Missing You!",
            fontWeight = FontWeight.SemiBold,
            fontSize = 24.sp
        )

        Text(
            "Please Login!",
            fontSize = 15.sp
        )
        Spacer(Modifier.height(12.dp))
        AuthTextField(
            value = email,
            onValueChange = {email = it},
            label = "Enter Email",
            inputType = InputType.EMAIL,
            modifier = Modifier,
        )
        Spacer(Modifier.height(8.dp))
        AuthTextField(
            isPassword = true,
            value = password,
            onValueChange = {password = it},
            label = "Enter password",
            inputType = InputType.PASSWORD,
            modifier = Modifier,
        )
        Spacer(Modifier.height(12.dp))
        BigFilledButton(
            modifier = Modifier,
            onClick = {
                keyboardController?.hide()
            },
            text = "Login"
        )
        Spacer(Modifier.height(12.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                "Just got here?"
            )
            Spacer(Modifier.width(4.dp))
            Text(
                "Register",
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .clickable {
                        keyboardController?.hide()
                    }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLoginScreen() {
    LoginScreen()
}

