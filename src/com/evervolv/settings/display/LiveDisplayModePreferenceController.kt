/*
 * SPDX-FileCopyrightText: 2024 Evervolv
 * SPDX-License-Identifier: Apache-2.0
 */

package com.evervolv.settings.display

import android.content.Context
import android.hardware.display.ColorDisplayManager
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceScreen
import com.android.internal.util.ArrayUtils
import com.android.settings.R
import com.android.settings.core.BasePreferenceController
import evervolv.hardware.LiveDisplayConfig
import evervolv.hardware.LiveDisplayManager
import evervolv.hardware.LiveDisplayManager.MODE_AUTO
import evervolv.hardware.LiveDisplayManager.MODE_DAY
import evervolv.hardware.LiveDisplayManager.MODE_NIGHT
import evervolv.hardware.LiveDisplayManager.MODE_OUTDOOR

class LiveDisplayModePreferenceController(
    context: Context,
    key: String
) : BasePreferenceController(context, key) {

    data class ModeInfo(
        var entry: String,
        var value: String
    )

    private val manager: LiveDisplayManager = LiveDisplayManager.getInstance(mContext)

    // Initialize modeItems from resources
    private var modeItems: MutableList<ModeInfo> = mContext.resources.run {
        val entries = getStringArray(com.evervolv.platform.internal.R.array.live_display_entries)
        val values = getStringArray(com.evervolv.platform.internal.R.array.live_display_values)

        entries.indices.map { i ->
            ModeInfo(entries[i], values[i])
        }.toMutableList()
    }

    override fun displayPreference(screen: PreferenceScreen) {
        super.displayPreference(screen)

        // Determine which indices to remove based on feature availability
        val removeIndices = mutableListOf<Int>()

        val nightDisplayAvailable = ColorDisplayManager.isNightDisplayAvailable(mContext)

        // Outdoor mode removal based on support
        if (!manager.config.hasFeature(MODE_OUTDOOR)) {
            removeIndices.add(modeItems.indexOfFirst { it.value == MODE_OUTDOOR.toString() })
        } else if (nightDisplayAvailable) {
            // Update summary for auto mode if outdoor mode is supported
            val autoIndex = modeItems.indexOfFirst { it.value == MODE_AUTO.toString() }
            if (autoIndex >= 0) {
                modeItems[autoIndex].entry = mContext.getString(R.string.live_display_outdoor_mode_summary)
            }
        }

        // Night display removal for HWC2
        if (nightDisplayAvailable) {
            removeIndices.add(modeItems.indexOfFirst { it.value == MODE_DAY.toString() })
            removeIndices.add(modeItems.indexOfFirst { it.value == MODE_NIGHT.toString() })
        }

        // Remove items from modeItems based on computed indices
        removeIndices.filter { it >= 0 }.sortedDescending().forEach { index ->
            modeItems.removeAt(index)
        }
    }

    override fun getAvailabilityStatus(): Int =
        if (manager.config.hasModeSupport()) {
            AVAILABLE
        } else {
            UNSUPPORTED_ON_DEVICE
        }

    override fun getSummary(): CharSequence =
        modeItems.find { it.value.toInt() == manager.mode }?.entry ?: mContext.getString(R.string.device_info_default)
}

