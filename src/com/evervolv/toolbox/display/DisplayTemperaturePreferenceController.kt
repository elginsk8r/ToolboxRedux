/*
 * SPDX-FileCopyrightText: 2024 Evervolv
 * SPDX-License-Identifier: Apache-2.0
 */

package com.evervolv.toolbox.display

import android.content.Context
import android.hardware.display.ColorDisplayManager
import com.android.settings.core.BasePreferenceController
import evervolv.hardware.LiveDisplayManager

class DisplayTemperaturePreferenceController(
    context: Context,
    key: String
) : BasePreferenceController(context, key) {


    override fun getAvailabilityStatus(): Int {
        return ColorDisplayManager.isNightDisplayAvailable(mContext)?.takeUnless {
            it
        }?.let { AVAILABLE } ?: UNSUPPORTED_ON_DEVICE
    }

    override fun getSummary(): CharSequence {
        val manager = LiveDisplayManager.getInstance(mContext)
        return mContext.getResources().getString(
            com.evervolv.toolbox.R.string.live_display_color_temperature_summary,
            roundTemperature(manager.dayColorTemperature),
            roundTemperature(manager.nightColorTemperature)
        )
    }

    private fun roundTemperature(value: Int): Int = ((value + STEP / 2) / STEP) * STEP

    companion object {
        private const val STEP = 100
    }
}
