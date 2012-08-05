/*
 * SPDX-FileCopyrightText: 2024 Evervolv
 * SPDX-License-Identifier: Apache-2.0
 */

package com.evervolv.toolbox.gestures

import android.content.Context
import com.android.settings.core.TogglePreferenceController
import evervolv.hardware.HardwareManager
import evervolv.provider.EVSettings

class NavigationBarPreferenceController(
    context: Context,
    key: String,
) : TogglePreferenceController(context, key) {

    private val manager = HardwareManager.getInstance(mContext)

    override fun getAvailabilityStatus(): Int =
        AVAILABLE.takeIf {
            manager.isSupported(HardwareManager.FEATURE_KEY_DISABLE)
        }?: UNSUPPORTED_ON_DEVICE

    override fun isChecked(): Boolean =
        EVSettings.Secure.getInt(
            mContext.contentResolver,
            preferenceKey,
            1
        ) != 0

    override fun setChecked(isChecked: Boolean): Boolean =
        EVSettings.Secure.putInt(
            mContext.contentResolver,
            preferenceKey,
            if (isChecked) 1 else 0
        )

    override fun getSliceHighlightMenuRes(): Int =
        com.android.settings.R.string.menu_key_system
}

