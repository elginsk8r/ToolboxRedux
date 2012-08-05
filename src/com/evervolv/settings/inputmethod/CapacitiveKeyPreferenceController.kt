/*
 * SPDX-FileCopyrightText: 2024 Evervolv
 * SPDX-License-Identifier: Apache-2.0
 */

package com.evervolv.settings.inputmethod

import android.content.Context
import com.android.settings.core.BasePreferenceController
import evervolv.hardware.HardwareManager
import evervolv.hardware.HardwareManager.FEATURE_KEY_DISABLE

class CapacitiveKeyPreferenceController(
    context: Context,
    key: String
) : BasePreferenceController(context, key) {

    private val manager: HardwareManager = HardwareManager.getInstance(mContext)

    override fun getAvailabilityStatus(): Int =
        if (manager.isSupported(FEATURE_KEY_DISABLE))
            AVAILABLE_UNSEARCHABLE
        else
            UNSUPPORTED_ON_DEVICE
}
