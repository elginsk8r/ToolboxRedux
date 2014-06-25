/*
 * SPDX-FileCopyrightText: 2024 Evervolv
 * SPDX-License-Identifier: Apache-2.0
 */

package com.evervolv.settings.display

import android.content.Context
import com.android.settings.R
import com.android.settings.core.TogglePreferenceController
import evervolv.hardware.HardwareManager
import evervolv.hardware.HardwareManager.FEATURE_HIGH_TOUCH_SENSITIVITY

/**
 * Preference controller for "Glove Mode"
 */
class GloveModePreferenceController(
    context: Context,
    key: String,
) : TogglePreferenceController(context, key) {

    private val manager: HardwareManager = HardwareManager.getInstance(mContext)

    override fun getAvailabilityStatus() =
        if (manager.isSupported(FEATURE_HIGH_TOUCH_SENSITIVITY))
            AVAILABLE_UNSEARCHABLE
        else
            UNSUPPORTED_ON_DEVICE

    override fun isChecked(): Boolean =
        manager.get(FEATURE_HIGH_TOUCH_SENSITIVITY)

    override fun setChecked(isChecked: Boolean): Boolean =
        manager.set(FEATURE_HIGH_TOUCH_SENSITIVITY, isChecked)

    override fun getSliceHighlightMenuRes(): Int {
        return R.string.menu_key_display
    }
}
