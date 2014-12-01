/*
 * SPDX-FileCopyrightText: 2024 Evervolv
 * SPDX-License-Identifier: Apache-2.0
 */

package com.evervolv.settings.inputmethod

import android.content.Context
import com.android.settings.R
import com.android.settings.core.TogglePreferenceController
import evervolv.provider.EVSettings

class KeyWakePreferenceController(
    context: Context,
    key: String,
) : TogglePreferenceController(context, key) {

    private val available =
        when (key) {
            "home_wake_screen" -> DeviceCapabilities.canWakeUsingHomeKey(mContext)
            "back_wake_screen" -> DeviceCapabilities.canWakeUsingBackKey(mContext)
            "menu_wake_screen" -> DeviceCapabilities.canWakeUsingMenuKey(mContext)
            "assist_wake_screen" -> DeviceCapabilities.canWakeUsingAssistKey(mContext)
            "app_switch_wake_screen" -> DeviceCapabilities.canWakeUsingAppSwitchKey(mContext)
            "volume_wake_screen" -> DeviceCapabilities.canWakeUsingVolumeKeys(mContext)
            else -> false
        }

    override fun getAvailabilityStatus(): Int =
        if (available) AVAILABLE else UNSUPPORTED_ON_DEVICE

    override fun isChecked(): Boolean =
        EVSettings.System.getInt(
            mContext.contentResolver, key, 0
        ) != 1

    override fun setChecked(isChecked: Boolean): Boolean =
        EVSettings.System.putInt(
            mContext.contentResolver, key, if (isChecked) 1 else 0
        )

    override fun getSliceHighlightMenuRes(): Int = R.string.menu_key_system
}
