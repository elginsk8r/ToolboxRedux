/*
 * SPDX-FileCopyrightText: 2024 Evervolv
 * SPDX-License-Identifier: Apache-2.0
 */

package com.evervolv.toolbox.inputmethod

import android.content.Context
import com.android.settings.core.BasePreferenceController
import com.evervolv.toolbox.utils.DeviceCapabilities

class ButtonSettingsController(
    private val context: Context,
    key: String
) : BasePreferenceController(context, key) {

    override fun getAvailabilityStatus(): Int =
        AVAILABLE.takeIf {
            DeviceCapabilities.hasKeySwap(mContext)
            || DeviceCapabilities.hasHomeKey(mContext)
            || DeviceCapabilities.hasBackKey(mContext)
            || DeviceCapabilities.hasMenuKey(mContext)
            || DeviceCapabilities.hasAssistKey(mContext)
            || DeviceCapabilities.hasAppSwitchKey(mContext)
            || DeviceCapabilities.hasVolumeKeys(mContext)
        }?: UNSUPPORTED_ON_DEVICE
}

