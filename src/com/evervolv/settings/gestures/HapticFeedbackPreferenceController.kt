/*
 * SPDX-FileCopyrightText: 2024 Evervolv
 * SPDX-License-Identifier: Apache-2.0
 */

package com.evervolv.settings.gestures

import android.content.Context
import android.os.Vibrator
import com.android.settings.core.TogglePreferenceController
import com.android.settings.R
import evervolv.provider.EVSettings
import evervolv.provider.EVSettings.System.TOUCHSCREEN_GESTURE_HAPTIC_FEEDBACK

/**
 * Preference controller for "Fast Charging"
 */
class HapticFeedbackPreferenceController(
    context: Context,
    key: String,
) : TogglePreferenceController(context, key) {

    private val vibrator = context.getSystemService(Vibrator::class.java)!!

    override fun getAvailabilityStatus() =
        if (vibrator.hasVibrator())
            AVAILABLE_UNSEARCHABLE
        else
            UNSUPPORTED_ON_DEVICE

    override fun isChecked(): Boolean =
        EVSettings.System.getInt(
            mContext.contentResolver,
            TOUCHSCREEN_GESTURE_HAPTIC_FEEDBACK,
            1
        ) != 0

    override fun setChecked(isChecked: Boolean): Boolean =
        EVSettings.System.putInt(
            mContext.contentResolver,
            TOUCHSCREEN_GESTURE_HAPTIC_FEEDBACK,
            if (isChecked) 1 else 0
        )

    override fun getSliceHighlightMenuRes(): Int =
        com.android.settings.R.string.menu_key_system
}
