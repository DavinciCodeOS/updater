package org.davincicodeos.updater.ui.settings

import android.content.Context
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import org.davincicodeos.updater.R
import org.davincicodeos.updater.Utils

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        // Now set a dynamic default for the OS flavour, since that is only
        // known at run-time
        val flavourPreference = findPreference<ListPreference>("flavour")

        if (flavourPreference != null && flavourPreference.value == null) {
            flavourPreference.value = Utils.getCurrentFlavour()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        requireContext().theme.applyStyle(R.style.Theme_Updater_Settings, true)
        return view
    }
}