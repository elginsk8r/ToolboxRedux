/*
 * SPDX-FileCopyrightText: 2024 Evervolv
 * SPDX-License-Identifier: Apache-2.0
 */

package com.evervolv.toolbox.statusbar

import android.content.Context
import android.view.View
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceScreen
import com.android.settings.core.BasePreferenceController
import evervolv.provider.EVSettings
import evervolv.provider.EVSettings.System.STATUS_BAR_QUICK_QS_PULLDOWN

class QuickPulldownPreferenceController(
    context: Context,
    key: String
) : BasePreferenceController(context, key), Preference.OnPreferenceChangeListener {

    private val summaryMap: Map<String, String> by lazy {
        val values = mContext.resources.getStringArray(
            com.evervolv.toolbox.R.array.status_bar_quick_qs_pulldown_values
        )
        val entries = mContext.resources.getStringArray(
            com.evervolv.toolbox.R.array.status_bar_quick_qs_pulldown_entries
        )
        values.indices.associate { i -> values[i] to entries[i] }
    }

    override fun getAvailabilityStatus(): Int = AVAILABLE

    override fun getSummary(): CharSequence {
        val value = EVSettings.System.getString(
            mContext.contentResolver, STATUS_BAR_QUICK_QS_PULLDOWN, "0"
        )
        return summaryMap[value] ?: mContext.getString(
            com.android.settings.R.string.device_info_default
        )
    }

    override fun displayPreference(screen: PreferenceScreen) {
        super.displayPreference(screen)
        if (mContext.resources.configuration.getLayoutDirection() == View.LAYOUT_DIRECTION_RTL) {
            val preference: ListPreference? = screen.findPreference(preferenceKey)
            preference?.let {
                it.setEntries(com.evervolv.toolbox.R.array.status_bar_quick_qs_pulldown_entries_rtl)
                it.setEntryValues(com.evervolv.toolbox.R.array.status_bar_quick_qs_pulldown_values_rtl)
            }
        }
    }

    override fun onPreferenceChange(preference: Preference, value: Any): Boolean {
        val newValue = value as String
        preference.summary = summaryMap[newValue]
        return EVSettings.System.putString(
            mContext.contentResolver, STATUS_BAR_QUICK_QS_PULLDOWN, newValue
        )
    }
}
