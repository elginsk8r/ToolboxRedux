/*
 * SPDX-FileCopyrightText: 2024 Evervolv
 * SPDX-License-Identifier: Apache-2.0
 */

package com.evervolv.settings.statusbar

import android.content.Context
import androidx.preference.Preference
import com.android.settings.R
import com.android.settings.core.BasePreferenceController
import evervolv.provider.EVSettings
import evervolv.provider.EVSettings.System.STATUS_BAR_BATTERY_STYLE

class BatteryStylePreferenceController(
    context: Context,
    key: String
) : BasePreferenceController(context, key), Preference.OnPreferenceChangeListener {

    private val summaryMap: Map<String, String> by lazy {
        val values = mContext.resources.getStringArray(R.array.status_bar_battery_style_values)
        val entries = mContext.resources.getStringArray(R.array.status_bar_battery_style_entries)
        values.indices.associate { i -> values[i] to entries[i] }
    }

    override fun getAvailabilityStatus(): Int = AVAILABLE

    override fun getSummary(): CharSequence {
        val value = EVSettings.System.getString(
            mContext.contentResolver, STATUS_BAR_BATTERY_STYLE, DEFAULT_VALUE
        )
        return summaryMap[value] ?: mContext.getString(R.string.device_info_default)
    }

    override fun onPreferenceChange(preference: Preference, value: Any): Boolean {
        val newValue = value as String
        preference.summary = summaryMap[newValue]
        return EVSettings.System.putString(
            mContext.contentResolver, STATUS_BAR_BATTERY_STYLE, newValue
        )
    }

    companion object {
        private const val DEFAULT_VALUE = "0"
    }
}
