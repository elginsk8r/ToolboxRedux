/*
 * SPDX-FileCopyrightText: 2024 Evervolv
 * SPDX-License-Identifier: Apache-2.0
 */

package com.evervolv.toolbox.inputmethod

import android.content.Context
import com.android.settings.core.BasePreferenceController
import com.evervolv.toolbox.utils.DeviceCapabilities

class CategoryPreferenceController(
    context: Context,
    key: String
) : BasePreferenceController(context, key) {

    private val category = getPreferenceKey().split("_")[0]

    override fun getAvailabilityStatus(): Int =
        if (when (category) {
            ButtonSettings.CATEGORY_HOME -> DeviceCapabilities.hasHomeKey(mContext)
            ButtonSettings.CATEGORY_BACK -> DeviceCapabilities.hasBackKey(mContext)
            ButtonSettings.CATEGORY_MENU -> DeviceCapabilities.hasMenuKey(mContext)
            ButtonSettings.CATEGORY_ASSIST -> DeviceCapabilities.hasAssistKey(mContext)
            ButtonSettings.CATEGORY_APPSWITCH -> DeviceCapabilities.hasAppSwitchKey(mContext)
            ButtonSettings.CATEGORY_VOLUME -> DeviceCapabilities.hasVolumeKeys(mContext)
            else -> false
        }) {
            AVAILABLE
        } else {
            UNSUPPORTED_ON_DEVICE
        }
}
