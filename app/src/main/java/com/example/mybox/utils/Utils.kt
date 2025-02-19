package com.example.mybox.utils

import android.app.Activity
import android.content.Context
import android.util.Patterns
import android.view.View
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.widget.NestedScrollView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

fun makeToast(context : Context, message: String) {
    Toast.makeText(context , message , Toast.LENGTH_SHORT).show()
}

fun isValidEmail(email: String): Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

fun convertTimestampToISOString(timestamp: Long): String {
    val dateFormat = SimpleDateFormat("dd-MM-yyyy 'at' HH:mm:ss" , Locale.getDefault())
    dateFormat.timeZone = TimeZone.getTimeZone("Asia/Jakarta") //change dynamically
    return dateFormat.format(Date(timestamp))
}

fun Activity.hideKeyboard() {
    val view = this.currentFocus
    if (view != null) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}

fun scrollToShowView(scrollView : NestedScrollView , view : View) {
    scrollView.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
        override fun onPreDraw(): Boolean {
            scrollView.viewTreeObserver.removeOnPreDrawListener(this)
            scrollView.post {
                scrollView.smoothScrollTo(0, view.bottom)
            }

            return true
        }
    })
}


