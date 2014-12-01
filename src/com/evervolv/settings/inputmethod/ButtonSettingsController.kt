/*
 * SPDX-FileCopyrightText: 2024 Evervolv
 * SPDX-License-Identifier: Apache-2.0
 */

package com.evervolv.settings.inputmethod

import android.content.Context
import com.android.settings.core.BasePreferenceController

class ButtonSettingsController(
    private val context: Context,
    key: String
) : BasePreferenceController(context, key) {

    private val hasHomeKey = DeviceCapabilities.hasHomeKey(mContext)
    private val hasBackKey = DeviceCapabilities.hasBackKey(mContext)
    private val hasMenuKey = DeviceCapabilities.hasMenuKey(mContext)
    private val hasAssistKey = DeviceCapabilities.hasAssistKey(mContext)
    private val hasAppSwitchKey = DeviceCapabilities.hasAppSwitchKey(mContext)
    private val hasVolumeKeys = DeviceCapabilities.hasVolumeKeys(mContext)

    override fun getAvailabilityStatus(): Int =
        if (hasHomeKey || hasBackKey || hasMenuKey || hasAssistKey || hasAppSwitchKey || hasVolumeKeys) {
            AVAILABLE
        } else {
            UNSUPPORTED_ON_DEVICE
        }
}

