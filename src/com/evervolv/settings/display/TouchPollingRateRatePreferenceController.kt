/*
 * SPDX-FileCopyrightText: 2024 Evervolv
 * SPDX-License-Identifier: Apache-2.0
 */

package com.evervolv.settings.display

import android.content.Context
import com.android.settings.R
import com.android.settings.core.TogglePreferenceController
import evervolv.hardware.HardwareManager
import evervolv.hardware.HardwareManager.FEATURE_HIGH_TOUCH_POLLING_RATE

/**
 * Preference controller for "High Polling Rate"
 */
class TouchPollingRateRatePreferenceController(
    context: Context,
    key: String,
) : TogglePreferenceController(context, key) {

    private val manager: HardwareManager = HardwareManager.getInstance(mContext)

    override fun getAvailabilityStatus() =
        if (manager.isSupported(FEATURE_HIGH_TOUCH_POLLING_RATE))
            AVAILABLE_UNSEARCHABLE
        else
            UNSUPPORTED_ON_DEVICE

    override fun isChecked(): Boolean =
        manager.get(FEATURE_HIGH_TOUCH_POLLING_RATE)

    override fun setChecked(isChecked: Boolean): Boolean =
        manager.set(FEATURE_HIGH_TOUCH_POLLING_RATE, isChecked)

    override fun getSliceHighlightMenuRes(): Int {
        return R.string.menu_key_display
    }
}
