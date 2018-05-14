/*
 * SPDX-FileCopyrightText: 2024 Evervolv
 * SPDX-License-Identifier: Apache-2.0
 */

package com.evervolv.settings.display

import android.content.Context
import com.android.settings.R
import com.android.settings.core.TogglePreferenceController
import evervolv.hardware.HardwareManager
import evervolv.hardware.HardwareManager.FEATURE_READING_ENHANCEMENT

/**
 * Preference controller for "Reading Mode"
 */
class ReadingModePreferenceController(
    private val context: Context,
    key: String
) : TogglePreferenceController(context, key) {

    private val manager: HardwareManager = HardwareManager.getInstance(mContext)

    override fun getAvailabilityStatus(): Int =
        if (manager.isSupported(FEATURE_READING_ENHANCEMENT)) {
            AVAILABLE_UNSEARCHABLE
        } else {
            UNSUPPORTED_ON_DEVICE
        }

    override fun isChecked(): Boolean =
        manager.get(FEATURE_READING_ENHANCEMENT)

    override fun setChecked(isChecked: Boolean): Boolean =
        manager.set(FEATURE_READING_ENHANCEMENT, isChecked)

    override fun getSliceHighlightMenuRes(): Int {
        return R.string.menu_key_display
    }
}
