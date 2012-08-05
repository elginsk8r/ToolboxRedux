/*
 * SPDX-FileCopyrightText: 2024 Evervolv
 * SPDX-License-Identifier: Apache-2.0
 */

package com.evervolv.settings.inputmethod

import android.content.Context
import com.android.settings.R
import com.android.settings.core.TogglePreferenceController
import evervolv.hardware.HardwareManager
import evervolv.hardware.HardwareManager.FEATURE_KEY_DISABLE
import evervolv.provider.EVSettings
import evervolv.provider.EVSettings.Secure.DEV_FORCE_SHOW_NAVBAR

class DisableNavigationBarController(
    context: Context,
    key: String,
) : TogglePreferenceController(context, key) {

    private val manager: HardwareManager = HardwareManager.getInstance(mContext)

    override fun getAvailabilityStatus(): Int =
        if (manager.isSupported(FEATURE_KEY_DISABLE))
            AVAILABLE_UNSEARCHABLE
        else
            UNSUPPORTED_ON_DEVICE

    override fun isChecked(): Boolean =
        EVSettings.Secure.getInt(
            mContext.contentResolver, DEV_FORCE_SHOW_NAVBAR, 0
        ) == 1

    override fun setChecked(isChecked: Boolean): Boolean =
        EVSettings.Secure.putInt(
            mContext.contentResolver, DEV_FORCE_SHOW_NAVBAR, if (isChecked) 1 else 0
        )

    override fun getSliceHighlightMenuRes(): Int {
        return R.string.menu_key_system
    }
}
