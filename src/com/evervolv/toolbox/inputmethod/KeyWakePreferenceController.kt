/*
 * SPDX-FileCopyrightText: 2024 Evervolv
 * SPDX-License-Identifier: Apache-2.0
 */

package com.evervolv.toolbox.inputmethod

import android.content.Context
import com.android.settings.core.TogglePreferenceController
import com.evervolv.toolbox.utils.DeviceCapabilities
import evervolv.provider.EVSettings

class KeyWakePreferenceController(
    context: Context,
    key: String,
) : TogglePreferenceController(context, key) {

    private val category = getPreferenceKey().split("_")[1]

    override fun getAvailabilityStatus(): Int =
        if (when (category) {
            ButtonSettings.CATEGORY_HOME -> DeviceCapabilities.canWakeUsingHomeKey(mContext)
            ButtonSettings.CATEGORY_BACK -> DeviceCapabilities.canWakeUsingBackKey(mContext)
            ButtonSettings.CATEGORY_MENU -> DeviceCapabilities.canWakeUsingMenuKey(mContext)
            ButtonSettings.CATEGORY_ASSIST -> DeviceCapabilities.canWakeUsingAssistKey(mContext)
            ButtonSettings.CATEGORY_APPSWITCH -> DeviceCapabilities.canWakeUsingAppSwitchKey(mContext)
            ButtonSettings.CATEGORY_VOLUME -> DeviceCapabilities.canWakeUsingVolumeKeys(mContext)
            else -> false
        }) AVAILABLE else UNSUPPORTED_ON_DEVICE

    override fun isChecked(): Boolean =
        EVSettings.System.getInt(
            mContext.contentResolver,
            getPreferenceKey(),
            0
        ) != 1

    override fun setChecked(isChecked: Boolean): Boolean =
        EVSettings.System.putInt(
            mContext.contentResolver,
            getPreferenceKey(),
            if (isChecked) 1 else 0
        )

    override fun getSliceHighlightMenuRes(): Int =
        com.android.settings.R.string.menu_key_system
}
