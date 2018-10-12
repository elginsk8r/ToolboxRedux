/*
 * SPDX-FileCopyrightText: 2024 Evervolv
 * SPDX-License-Identifier: Apache-2.0
 */

package com.evervolv.settings.display

import android.content.Context
import android.hardware.display.ColorDisplayManager
import android.hardware.display.ColorDisplayManager.COLOR_MODE_AUTOMATIC
import android.hardware.display.ColorDisplayManager.COLOR_MODE_BOOSTED
import android.hardware.display.ColorDisplayManager.COLOR_MODE_NATURAL
import android.hardware.display.ColorDisplayManager.COLOR_MODE_SATURATED
import android.hardware.display.ColorDisplayManager.VENDOR_COLOR_MODE_RANGE_MAX
import android.hardware.display.ColorDisplayManager.VENDOR_COLOR_MODE_RANGE_MIN
import androidx.annotation.VisibleForTesting
import com.android.settings.R
import com.android.settings.core.BasePreferenceController

class ColorModeExtPreferenceController(
    context: Context,
    key: String
) : BasePreferenceController(context, key) {

    private val colorDisplayManager = mContext.getSystemService(ColorDisplayManager::class.java)!!

    private val colorModeOptions = mContext.resources.getStringArray(R.array.config_color_mode_options_strings)
    private val colorModeValues = mContext.resources.getIntArray(R.array.config_color_mode_options_values)
    private val colorModeMap: Map<Int, String> =
        if (colorModeOptions.size != colorModeValues.size)
            emptyMap()
        else
            colorModeValues.zip(colorModeOptions).toMap()
                .filterKeys { mode ->
                    mode == COLOR_MODE_NATURAL ||
                    mode == COLOR_MODE_BOOSTED ||
                    mode == COLOR_MODE_SATURATED ||
                    mode == COLOR_MODE_AUTOMATIC ||
                    (mode in VENDOR_COLOR_MODE_RANGE_MIN..VENDOR_COLOR_MODE_RANGE_MAX)
                }

    private val availableColorModes = mContext.resources.getIntArray(
        com.android.internal.R.array.config_availableColorModes
    )

    override fun getAvailabilityStatus(): Int =
        if (colorDisplayManager.isDeviceColorManaged == true &&
            !ColorDisplayManager.areAccessibilityTransformsEnabled(mContext)) {
            if (availableColorModes.isEmpty()) {
                UNSUPPORTED_ON_DEVICE
            } else {
                AVAILABLE
            }
        } else {
            DISABLED_FOR_USER
        }

    override fun getSummary(): CharSequence =
        colorModeMap[colorDisplayManager.colorMode] ?: mContext.resources.getString(R.string.device_info_default)
}

