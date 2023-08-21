package com.example.mybox.settings

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import com.example.mybox.R
import com.example.mybox.utils.DarkMode

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        if (savedInstanceState == null){
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState : Bundle? , rootKey : String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)

            val themePreference = findPreference<ListPreference>(getString(R.string.pref_key_dark))
            themePreference?.setOnPreferenceChangeListener { _ , newValue ->
                val nightMode = when(newValue) {
                    getString(R.string.pref_dark_follow_system) -> DarkMode.FOLLOW_SYSTEM.value
                    getString(R.string.pref_dark_on) -> DarkMode.ON.value
                    getString(R.string.pref_dark_off) -> DarkMode.OFF.value
                    else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                }
                updateTheme(nightMode)
                true
            }
        }

        private fun updateTheme(mode: Int): Boolean {
            AppCompatDelegate.setDefaultNightMode(mode)
            requireActivity().recreate()
            return true
        }

    }
}