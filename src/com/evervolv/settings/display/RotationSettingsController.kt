/*
 * SPDX-FileCopyrightText: 2024 Evervolv
 * SPDX-License-Identifier: Apache-2.0
 */

package com.evervolv.settings.display

import android.content.Context
import com.android.settings.core.BasePreferenceController

class RotationSettingsController(
    context: Context,
    key: String
) : BasePreferenceController(context, key) {

    override fun getAvailabilityStatus(): Int =
        if (mContext.packageManager.hasSystemFeature("android.hardware.sensor.accelerometer")) {
            AVAILABLE
        } else {
            UNSUPPORTED_ON_DEVICE
        }
}
