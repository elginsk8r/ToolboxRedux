/*
 * SPDX-FileCopyrightText: 2024 Evervolv
 * SPDX-License-Identifier: Apache-2.0
 */

package com.evervolv.settings.inputmethod

import android.content.Context
import com.android.settings.R
import com.android.settings.core.TogglePreferenceController
import evervolv.provider.EVSettings

class AssistKeyWakePreferenceController(
    context: Context,
    key: String,
) : TogglePreferenceController(context, key) {

    override fun getAvailabilityStatus(): Int =
        if (DeviceCapabilities.canWakeUsingAssistKey(mContext)) {
            AVAILABLE
        } else {
            UNSUPPORTED_ON_DEVICE
        }

    override fun isChecked(): Boolean =
        EVSettings.System.getInt(
            mContext.contentResolver,
            EVSettings.System.ASSIST_WAKE_SCREEN,
            0
        ) != 1

    override fun setChecked(isChecked: Boolean): Boolean =
        EVSettings.System.putInt(
            mContext.contentResolver,
            EVSettings.System.ASSIST_WAKE_SCREEN,
            if (isChecked) 1 else 0
        )

    override fun getSliceHighlightMenuRes(): Int = R.string.menu_key_system
}
