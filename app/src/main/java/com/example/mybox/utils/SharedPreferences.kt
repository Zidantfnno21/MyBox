package com.example.mybox.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate

class SharedPreferences {
    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    private fun getEditor(context: Context): SharedPreferences.Editor {
        return getSharedPreferences(context).edit()
    }

    fun saveTheme(themeMode: Int, context : Context) {
        try {
            getEditor(context).putInt(THEME_MODE, themeMode).apply()
        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    fun saveToken(token: String, context: Context) {
        try {
            getEditor(context).putString(TOKEN_KEY, token).apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun saveState(state: Boolean, context: Context) {
        try {
            getEditor(context).putBoolean(STATE_KEY, state).apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun saveUser(name: String, context: Context) {
        getEditor(context).putString(USER_KEY, name).apply()
    }

    fun saveViewState(state: Int, context : Context) {
        getEditor(context).putInt(RV_VIEW_KEY, state).apply()
    }

    fun logOut(context: Context) {
        val editor = getEditor(context)
        editor.remove(TOKEN_KEY)
        editor.remove(STATUS_KEY)
        editor.remove(USER_KEY)
        editor.apply()
    }

    fun getTheme(context : Context): Int {
        val sharedPref = getSharedPreferences(context)
        return sharedPref.getInt(THEME_MODE, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
    }

    fun isTokenAvailable(context: Context): Boolean {
        val sharedPref = getSharedPreferences(context)
        return sharedPref.contains(TOKEN_KEY) && sharedPref.getString(TOKEN_KEY, "")?.isNotEmpty() == true
    }

    fun isViewVisible(context : Context): Boolean {
        val sharedPref = getSharedPreferences(context)
        return sharedPref.getBoolean(STATE_KEY , true)
    }

    fun getSavedUsername(context : Context): String {
        val sharedPref = getSharedPreferences(context)
        return sharedPref.getString(USER_KEY, "DefaultUsername") ?: "DefaultUsername"
    }

    fun getView(context : Context): Int {
        val sharedPref = getSharedPreferences(context)
        return sharedPref.getInt(RV_VIEW_KEY , 0)
    }
}