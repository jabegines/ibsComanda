package es.albaibs.ibscomanda.varios

import android.os.Bundle
import android.preference.*
import androidx.appcompat.app.AppCompatActivity
import es.albaibs.ibscomanda.R


class Preferencias: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (fragmentManager.findFragmentById(android.R.id.content) == null) {
            fragmentManager.beginTransaction().add(android.R.id.content, SettingsFragment()).commit()
        }
    }


    class SettingsFragment : PreferenceFragment() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            addPreferencesFromResource(R.xml.preferencias)

            bindPreferenceSummaryToValue(findPreference("terminal"))
            bindPreferenceSummaryToValue(findPreference("usuario"))
            bindPreferenceSummaryToValue(findPreference("ip_servidor"))
            bindPreferenceSummaryToValue(findPreference("puerto_servidor"))
            bindPreferenceSummaryToValue(findPreference("prefijo"))
            bindPreferenceSummaryToValue(findPreference("sistema"))
        }
    }

    companion object {
        /**
         * A preference value change listener that updates the preference's summary
         * to reflect its new value.
         */
        private val sBindPreferenceSummaryToValueListener = Preference.OnPreferenceChangeListener { preference, value ->

            val stringValue = value.toString()

            if (preference is ListPreference) {
                // For list preferencias, look up the correct display value in
                // the preference's 'entries' list.
                val listPreference = preference
                val index = listPreference.findIndexOfValue(stringValue)

                // Set the summary to reflect the new value.
                preference.setSummary(
                    if (index >= 0)
                        listPreference.entries[index]
                    else
                        null)
            }
            else if (preference is CheckBoxPreference) {

            }
            else {
                // For all other preferencias, set the summary to the value's simple string representation.
                if (preference.key != "password_wifi") preference.summary = stringValue
                else preference.summary = "*".repeat(stringValue.length)
            }
            true
        }


        private fun bindPreferenceSummaryToValue(preference: Preference) {
            // Set the listener to watch for value changes.
            preference.onPreferenceChangeListener = sBindPreferenceSummaryToValueListener

            // Trigger the listener immediately with the preference's current value.
            if (preference is CheckBoxPreference)
                sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                    PreferenceManager.getDefaultSharedPreferences(preference.context).getBoolean(preference.key, false))
            else
                sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                    PreferenceManager.getDefaultSharedPreferences(preference.context).getString(preference.key, ""))
        }
    }


}