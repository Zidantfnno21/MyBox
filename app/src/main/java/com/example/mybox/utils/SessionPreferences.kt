package com.example.mybox.utils

import android.content.Context
import android.content.SharedPreferences

class SessionPreferences {
    fun initPref(context : Context, name: String): SharedPreferences {
        return context.getSharedPreferences(name, Context.MODE_PRIVATE)
    }

    private fun editorPref(context : Context, name : String): SharedPreferences.Editor{
        val sharedPref = context.getSharedPreferences(name, Context.MODE_PRIVATE)
        return sharedPref.edit()
    }

    fun saveToken(token: String, context : Context){
        val editor = editorPref(context, KEY_NAME)
        editor.putString("token", token)
        editor.apply()
    }

    fun logOut(context : Context) {
        val editor = editorPref(context, KEY_NAME)
        editor.remove("token")
        editor.remove("status")
        editor.apply()
    }

    fun isTokenAvailable(context: Context): Boolean {
        val sharedPref = initPref(context, "onLogIn")
        return sharedPref.contains("token") && sharedPref.getString("token", "")?.isNotEmpty() == true
    }

}