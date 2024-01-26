package com.example.mybox.settings

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.viewModels
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.example.mybox.R
import com.example.mybox.databinding.ActivitySettingsBinding
import com.example.mybox.ui.ViewModelFactory
import com.example.mybox.ui.auth.loginScreen.LoginActivity
import com.example.mybox.utils.SharedPreferences

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding : ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        val savedTheme = SharedPreferences().getTheme(this)
        AppCompatDelegate.setDefaultNightMode(savedTheme)

        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backButtonSettings.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }

        supportActionBar?.hide()
        enableEdgeToEdge()
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        private val viewModel by viewModels<SettingsViewModel> {
            ViewModelFactory.getInstance(requireContext())
        }

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)

            val themePreference = findPreference<ListPreference>(getString(R.string.pref_key_dark))
            themePreference?.setOnPreferenceChangeListener { _, newValue ->
                val nightMode = when (newValue) {
                    getString(R.string.pref_dark_follow_system) -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                    getString(R.string.pref_dark_on) -> AppCompatDelegate.MODE_NIGHT_YES
                    getString(R.string.pref_dark_off) -> AppCompatDelegate.MODE_NIGHT_NO
                    else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                }
                updateTheme(nightMode)

                SharedPreferences().saveTheme(nightMode , requireContext())
                true
            }

            findPreference<Preference>(getString(R.string.pref_log_out))?.setOnPreferenceClickListener {
                showLogoutDialog()

                true
            }
        }

        private fun showLogoutDialog() {
            val dialog = Dialog(requireContext(),  R.style.RoundShapeTheme)
            dialog.setContentView(R.layout.custom_dialog)

            val cancelButton: Button = dialog.findViewById(R.id.dialogButtonCancel)
            cancelButton.setOnClickListener {
                dialog.dismiss()
            }

            val logOutButton: Button = dialog.findViewById(R.id.dialogButtonLogout)
            logOutButton.setOnClickListener {
                viewModel.logOut()

                SharedPreferences().logOut(requireContext())

                val loginIntent = Intent(requireContext(), LoginActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(loginIntent)
                requireActivity().finishAffinity()

                dialog.dismiss()
            }

            dialog.show()
        }

        private fun updateTheme(mode: Int) {
            try {
                AppCompatDelegate.setDefaultNightMode(mode)
                requireActivity().recreate()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
