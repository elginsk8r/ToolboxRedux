/*
 * SPDX-FileCopyrightText: 2024 Evervolv
 * SPDX-License-Identifier: Apache-2.0
 */

package com.evervolv.settings.gestures

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import com.android.settings.core.BasePreferenceController
import com.evervolv.settings.R
import evervolv.hardware.HardwareManager
import evervolv.hardware.HardwareManager.FEATURE_TOUCHSCREEN_GESTURES

class TouchscreenGesturePreferenceController(
    context: Context,
    key: String
) : BasePreferenceController(context, key) {

    private val supported = HardwareManager.getInstance(mContext).isSupported(FEATURE_TOUCHSCREEN_GESTURES)
    private val remotePackages = mContext.packageManager.queryIntentActivities(
        Intent("org.lineageos.settings.device.GESTURE_SETTINGS"),
        PackageManager.ResolveInfoFlags.of(0)
    )

    override fun getAvailabilityStatus() =
        if (supported && remotePackages.isEmpty())
            AVAILABLE
        else
            UNSUPPORTED_ON_DEVICE

    override fun getSummary(): CharSequence =
        mContext.getString(R.string.touchscreen_gesture_settings_summary)
}
