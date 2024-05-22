/*
 * SPDX-FileCopyrightText: 2024 Evervolv
 * SPDX-License-Identifier: Apache-2.0
 */

package com.evervolv.toolbox.notification

import android.content.Context
import androidx.preference.Preference
import com.android.settings.core.BasePreferenceController
import evervolv.provider.EVSettings

class EdgeLightColorModePreferenceController(
    context: Context,
    key: String
) : BasePreferenceController(context, key), Preference.OnPreferenceChangeListener {

    private val summaryMap: Map<String, String> by lazy {
        val values = mContext.resources.getStringArray(
            com.evervolv.toolbox.R.array.notifications_edge_light_color_mode_values
        )
        val entries = mContext.resources.getStringArray(
            com.evervolv.toolbox.R.array.notifications_edge_light_color_mode_entries
        )
        values.indices.associate { i -> values[i] to entries[i] }
    }

    override fun getAvailabilityStatus(): Int =
        AVAILABLE.takeIf {
            EVSettings.System.getString(
                mContext.contentResolver,
                EVSettings.System.EDGE_LIGHT_COLOR_MODE
            ) != null
        } ?: UNSUPPORTED_ON_DEVICE

    override fun getSummary(): CharSequence {
        val value = EVSettings.System.getString(
            mContext.contentResolver,
            EVSettings.System.EDGE_LIGHT_COLOR_MODE,
            "0"
        )
        return summaryMap[value] ?: mContext.getString(
            com.android.settings.R.string.device_info_default
        )
    }

    override fun onPreferenceChange(preference: Preference, value: Any): Boolean {
        val newValue = value as String
        preference.summary = summaryMap[newValue]
        return EVSettings.System.putString(
            mContext.contentResolver,
            EVSettings.System.EDGE_LIGHT_COLOR_MODE,
            newValue
        )
    }
}
