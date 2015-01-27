/*
 * SPDX-FileCopyrightText: 2024 Evervolv
 * SPDX-License-Identifier: Apache-2.0
 */

package com.evervolv.settings.inputmethod

import android.content.Context
import com.android.settings.R
import com.android.settings.core.TogglePreferenceController
import evervolv.hardware.HardwareManager
import evervolv.hardware.HardwareManager.FEATURE_TOUCH_HOVERING

/**
 * Preference controller for "Fast Charging"
 */
class StylusModePreferenceController(
    context: Context,
    key: String,
) : TogglePreferenceController(context, key) {

    private val manager: HardwareManager = HardwareManager.getInstance(mContext)

    override fun getAvailabilityStatus() =
        if (manager.isSupported(FEATURE_TOUCH_HOVERING))
            AVAILABLE_UNSEARCHABLE
        else
            UNSUPPORTED_ON_DEVICE

    override fun isChecked(): Boolean =
        manager.get(FEATURE_TOUCH_HOVERING)

    override fun setChecked(isChecked: Boolean): Boolean =
        manager.set(FEATURE_TOUCH_HOVERING, isChecked)

    override fun getSliceHighlightMenuRes(): Int {
        return R.string.menu_key_system
    }
}
