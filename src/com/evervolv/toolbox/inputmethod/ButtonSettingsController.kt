/*
 * SPDX-FileCopyrightText: 2024 Evervolv
 * SPDX-License-Identifier: Apache-2.0
 */

package com.evervolv.toolbox.inputmethod

import android.content.Context
import com.android.settings.core.BasePreferenceController
import evervolv.hardware.HardwareManager

class ButtonSettingsController(
    private val context: Context,
    key: String
) : BasePreferenceController(context, key) {

    override fun getAvailabilityStatus(): Int =
        AVAILABLE.takeIf {
            HardwareManager.getInstance(mContext).isSupported(HardwareManager.FEATURE_KEY_SWAP)
        }?: UNSUPPORTED_ON_DEVICE
}

