/*
 * SPDX-FileCopyrightText: 2024 Evervolv
 * SPDX-License-Identifier: Apache-2.0
 */

package com.evervolv.settings.display

import android.content.Context
import com.android.settings.R
import com.android.settings.core.TogglePreferenceController
import evervolv.hardware.LiveDisplayManager
import evervolv.hardware.LiveDisplayManager.FEATURE_CABC

/**
 * Preference controller for "Adaptive Backlight"
 */
class DisplayLowPowerModeController(
    context: Context,
    key: String
) : TogglePreferenceController(context, key) {

    private val manager: LiveDisplayManager = LiveDisplayManager.getInstance(mContext)

    override fun getAvailabilityStatus(): Int =
        if (manager.config.hasFeature(FEATURE_CABC)) {
            AVAILABLE_UNSEARCHABLE
        } else {
            UNSUPPORTED_ON_DEVICE
        }

    override fun isChecked(): Boolean =
        manager.isCABCEnabled()

    override fun setChecked(isChecked: Boolean): Boolean =
        manager.setCABCEnabled(isChecked)

    override fun getSliceHighlightMenuRes(): Int {
        return R.string.menu_key_display
    }
}
