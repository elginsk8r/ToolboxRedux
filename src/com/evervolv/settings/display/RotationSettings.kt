/*
 * SPDX-FileCopyrightText: 2024 Evervolv
 * SPDX-License-Identifier: Apache-2.0
 */

package com.evervolv.settings.display

import android.os.Bundle
import android.os.UserHandle
import android.provider.Settings
import androidx.preference.CheckBoxPreference
import androidx.preference.Preference
import androidx.preference.PreferenceScreen
import com.android.settings.R
import com.android.settings.SettingsPreferenceFragment
import com.evervolv.internal.logging.EVMetricsLogger

class RotationSettings : SettingsPreferenceFragment() {

    private lateinit var rotation0Pref: CheckBoxPreference
    private lateinit var rotation90Pref: CheckBoxPreference
    private lateinit var rotation180Pref: CheckBoxPreference
    private lateinit var rotation270Pref: CheckBoxPreference

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        addPreferencesFromResource(R.xml.display_rotation)

        with(preferenceScreen) {
            rotation0Pref = findPreference(ROTATION_0_PREF)!!
            rotation90Pref = findPreference(ROTATION_90_PREF)!!
            rotation180Pref = findPreference(ROTATION_180_PREF)!!
            rotation270Pref = findPreference(ROTATION_270_PREF)!!
        }

        val mode = Settings.System.getIntForUser(
            requireContext().contentResolver,
            Settings.System.ACCELEROMETER_ROTATION_ANGLES,
            ROTATION_0_MODE or ROTATION_90_MODE or ROTATION_270_MODE,
            UserHandle.USER_CURRENT
        )

        rotation0Pref.isChecked = mode and ROTATION_0_MODE != 0
        rotation90Pref.isChecked = mode and ROTATION_90_MODE != 0
        rotation180Pref.isChecked = mode and ROTATION_180_MODE != 0
        rotation270Pref.isChecked = mode and ROTATION_270_MODE != 0
    }

    private fun getRotationBitmask(): Int = listOf(
        rotation0Pref to ROTATION_0_MODE,
        rotation90Pref to ROTATION_90_MODE,
        rotation180Pref to ROTATION_180_MODE,
        rotation270Pref to ROTATION_270_MODE
    ).fold(0) { acc, (pref, mode) -> if (pref.isChecked) acc or mode else acc }

    override fun onPreferenceTreeClick(preference: Preference): Boolean {
        if (preference in listOf(rotation0Pref, rotation90Pref, rotation180Pref, rotation270Pref)) {
            var mode = getRotationBitmask()
            if (mode == 0) {
                mode = mode or ROTATION_0_MODE
                rotation0Pref.isChecked = true
            }
            Settings.System.putIntForUser(
                requireContext().contentResolver,
                Settings.System.ACCELEROMETER_ROTATION_ANGLES,
                mode,
                UserHandle.USER_CURRENT
            )
            return true
        }
        return super.onPreferenceTreeClick(preference)
    }

    override fun getMetricsCategory() = EVMetricsLogger.DONT_LOG

    companion object {
        const val TAG = "RotationSettings"

        private const val ROTATION_0_PREF = "display_rotation_0"
        private const val ROTATION_90_PREF = "display_rotation_90"
        private const val ROTATION_180_PREF = "display_rotation_180"
        private const val ROTATION_270_PREF = "display_rotation_270"

        const val ROTATION_0_MODE = 1
        const val ROTATION_90_MODE = 2
        const val ROTATION_180_MODE = 4
        const val ROTATION_270_MODE = 8
    }
}
