package com.example.mybox.utils

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferencesHelper @Inject constructor(private val sharedPreferences: SharedPreferences){
    private fun getSharedPreferences(): SharedPreferences {
        return sharedPreferences
    }

    private fun getEditor(): SharedPreferences.Editor {
        return sharedPreferences.edit()
    }

    fun saveTheme(themeMode: Int) {
        try {
            getEditor().putInt(THEME_MODE, themeMode).apply()
        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    fun saveToken(token: String,  ) {
        try {
            getEditor().putString(TOKEN_KEY, token).apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun saveState(state: Boolean,  ) {
        try {
            getEditor().putBoolean(STATE_KEY, state).apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun saveUser(name: String,  ) {
        getEditor().putString(USER_KEY, name).apply()
    }

    fun saveUid(uid: String,  ) {
        getEditor().putString(UID_KEY, uid).apply()
    }

    fun saveViewState(state: Int,  ) {
        getEditor().putInt(RV_VIEW_KEY, state).apply()
    }

    fun logOut( ) {
        val editor = getEditor()
        editor.remove(TOKEN_KEY)
        editor.remove(STATUS_KEY)
        editor.remove(USER_KEY)
        editor.apply()
    }

    fun getTheme( ): Int {
        val sharedPref = getSharedPreferences()
        return sharedPref.getInt(THEME_MODE, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
    }

    fun isTokenAvailable( ): Boolean {
        val sharedPref = getSharedPreferences()
        return sharedPref.contains(TOKEN_KEY) && sharedPref.getString(TOKEN_KEY, "")?.isNotEmpty() == true
    }

    fun isViewVisible( ): Boolean {
        val sharedPref = getSharedPreferences()
        return sharedPref.getBoolean(STATE_KEY , true)
    }

    fun getSavedUsername( ): String {
        val sharedPref = getSharedPreferences()
        return sharedPref.getString(USER_KEY, "DefaultUsername") ?: "DefaultUsername"
    }

    fun getSavedUid( ): String {
        val sharedPref = getSharedPreferences()
        return sharedPref.getString(UID_KEY, "Default UID") ?: "Default UID"
    }

    fun getView( ): Int {
        val sharedPref = getSharedPreferences()
        return sharedPref.getInt(RV_VIEW_KEY , 0)
    }
}