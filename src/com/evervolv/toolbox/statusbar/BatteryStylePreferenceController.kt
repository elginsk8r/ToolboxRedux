/*
 * SPDX-FileCopyrightText: 2024 Evervolv
 * SPDX-License-Identifier: Apache-2.0
 */

package com.evervolv.toolbox.statusbar

import android.content.Context
import androidx.preference.Preference
import com.android.settings.Utils
import com.android.settings.core.BasePreferenceController
import evervolv.provider.EVSettings
import evervolv.provider.EVSettings.System.STATUS_BAR_BATTERY_STYLE

class BatteryStylePreferenceController(
    context: Context,
    key: String
) : BasePreferenceController(context, key), Preference.OnPreferenceChangeListener {

    private val summaryMap: Map<String, String> by lazy {
        val values = mContext.resources.getStringArray(
            com.evervolv.toolbox.R.array.status_bar_battery_style_values
        )
        val entries = mContext.resources.getStringArray(
            com.evervolv.toolbox.R.array.status_bar_battery_style_entries
        )
        values.indices.associate { i -> values[i] to entries[i] }
    }

    override fun getAvailabilityStatus(): Int =
        AVAILABLE_UNSEARCHABLE.takeIf {
            Utils.isBatteryPresent(mContext)
        } ?: CONDITIONALLY_UNAVAILABLE

    override fun getSummary(): CharSequence {
        val value = EVSettings.System.getString(
            mContext.contentResolver, STATUS_BAR_BATTERY_STYLE, "0"
        )
        return summaryMap[value] ?: mContext.getString(
            com.android.settings.R.string.device_info_default
        )
    }

    override fun onPreferenceChange(preference: Preference, value: Any): Boolean {
        val newValue = value as String
        preference.summary = summaryMap[newValue]
        return EVSettings.System.putString(
            mContext.contentResolver, STATUS_BAR_BATTERY_STYLE, newValue
        )
    }
}
