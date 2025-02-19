package com.example.mybox.ui.screen.auth.registerScreen

import android.content.Intent
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
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
import androidx.core.view.isVisible
import com.example.mybox.R
import com.example.mybox.data.Result
import com.example.mybox.databinding.ActivityRegisterBinding
import com.example.mybox.ui.components.AuthTextField
import com.example.mybox.ui.components.BigFilledButton
import com.example.mybox.ui.components.InputType
import com.example.mybox.ui.screen.auth.loginScreen.LoginActivity
import com.example.mybox.utils.hideKeyboard
import com.example.mybox.utils.isValidEmail
import com.example.mybox.utils.makeToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private val viewModel : RegisterViewModel by viewModels()
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

@Composable
fun RegisterScreen() {
    val keyboardController = LocalSoftwareKeyboardController.current
    var username by remember { mutableStateOf("") };
    var email by remember { mutableStateOf("") };
    var password by remember { mutableStateOf("") };

    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "New Here?",
            fontWeight = FontWeight.SemiBold,
            fontSize = 24.sp
        )

        Text(
            "Don't worry you can register first 😍",
            fontSize = 15.sp
        )
        Spacer(Modifier.height(12.dp))
        AuthTextField(
            value = username,
            onValueChange = {username = it},
            label = "Enter Username",
            inputType = InputType.USERNAME,
            modifier = Modifier,
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
                "Already have account?"
            )
            Spacer(Modifier.width(4.dp))
            Text(
                "Log in",
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
fun PreviewRegisterScreen() {
    RegisterScreen()
}