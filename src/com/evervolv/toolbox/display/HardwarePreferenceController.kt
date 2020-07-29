/*
 * SPDX-FileCopyrightText: 2024 Evervolv
 * SPDX-License-Identifier: Apache-2.0
 */

package com.evervolv.toolbox.display

import android.content.Context
import android.hardware.display.ColorDisplayManager
import com.android.settings.core.TogglePreferenceController
import evervolv.hardware.HardwareManager

class HardwarePreferenceController(
    context: Context,
    key: String
) : TogglePreferenceController(context, key) {

    private val manager = HardwareManager.getInstance(mContext)
    private val nightDisplay = ColorDisplayManager.isNightDisplayAvailable(mContext)

    private val feature = mapOf(
        "display_anti_flicker" to HardwareManager.FEATURE_ANTI_FLICKER,
        "display_color_enhance" to HardwareManager.FEATURE_COLOR_ENHANCEMENT,
        "display_low_power" to HardwareManager.FEATURE_ADAPTIVE_BACKLIGHT,
        "display_auto_outdoor_mode" to HardwareManager.FEATURE_SUNLIGHT_ENHANCEMENT,
        "display_reading_mode" to HardwareManager.FEATURE_READING_ENHANCEMENT
    ).getOrDefault(preferenceKey, FEATURE_UNSUPPORTED)

    override fun getAvailabilityStatus(): Int =
        AVAILABLE_UNSEARCHABLE.takeIf {
            feature != FEATURE_UNSUPPORTED &&
            manager.isSupported(feature) &&
            (feature != HardwareManager.FEATURE_SUNLIGHT_ENHANCEMENT || !nightDisplay)
        } ?: UNSUPPORTED_ON_DEVICE

    override fun isChecked(): Boolean =
        manager.get(feature).takeIf {
            feature != FEATURE_UNSUPPORTED
        } ?: false

    override fun setChecked(isChecked: Boolean): Boolean =
        manager.set(feature, isChecked).takeIf {
            feature != FEATURE_UNSUPPORTED
        } ?: false

    override fun getSliceHighlightMenuRes(): Int =
        com.android.settings.R.string.menu_key_display

    companion object {
        private const val FEATURE_UNSUPPORTED = -1
    }
}
