package org.davincicodeos.updater.ui.settings

import android.os.Bundle
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import org.davincicodeos.updater.R
import org.davincicodeos.updater.Utils

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        // Now set a dynamic default for the OS flavour, since that is only
        // known at run-time
        val flavourPreference = findPreference<ListPreference>("flavour");

        if (flavourPreference != null && flavourPreference.value == null) {
            flavourPreference.value = Utils.getCurrentFlavour();
        }
    }
}