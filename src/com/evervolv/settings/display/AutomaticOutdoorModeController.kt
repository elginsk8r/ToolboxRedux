/*
 * SPDX-FileCopyrightText: 2024 Evervolv
 * SPDX-License-Identifier: Apache-2.0
 */

package com.evervolv.settings.display

import android.content.Context
import android.hardware.display.ColorDisplayManager
import com.android.settings.R
import com.android.settings.core.TogglePreferenceController
import evervolv.hardware.LiveDisplayManager
import evervolv.hardware.LiveDisplayManager.MODE_OUTDOOR

/**
 * Preference controller for "Automatic outdoor mode"
 */
class AutomaticOutdoorModeController(
    context: Context,
    key: String
) : TogglePreferenceController(context, key) {

    private val manager: LiveDisplayManager = LiveDisplayManager.getInstance(mContext)

    override fun getAvailabilityStatus(): Int =
        if (manager.config.hasFeature(MODE_OUTDOOR)) {
            AVAILABLE_UNSEARCHABLE
        } else {
            UNSUPPORTED_ON_DEVICE
        }

    override fun isChecked(): Boolean =
        manager.isAutomaticOutdoorModeEnabled()

    override fun setChecked(isChecked: Boolean): Boolean =
        manager.setAutomaticOutdoorModeEnabled(isChecked)

    override fun getSliceHighlightMenuRes(): Int {
        return R.string.menu_key_display
    }
}
