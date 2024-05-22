/*
 * SPDX-FileCopyrightText: 2024 Evervolv
 * SPDX-License-Identifier: Apache-2.0
 */

package com.evervolv.toolbox.notification

import android.content.Context
import androidx.preference.Preference
import com.android.settings.core.TogglePreferenceController
import evervolv.provider.EVSettings

class EdgeLightSwitchPreferenceController(
    context: Context,
    key: String
) : TogglePreferenceController(context, key) {

    override fun getAvailabilityStatus(): Int =
        AVAILABLE.takeIf {
            EVSettings.System.getString(
                mContext.contentResolver,
                preferenceKey
            ) != null
        } ?: UNSUPPORTED_ON_DEVICE

    override fun isChecked(): Boolean =
        EVSettings.System.getInt(
            mContext.contentResolver,
            preferenceKey,
            0
        ) != 1

    override fun setChecked(isChecked: Boolean): Boolean =
        EVSettings.System.putInt(
            mContext.contentResolver,
            preferenceKey,
            if (isChecked) 1 else 0
        )

    override fun updateState(preference: Preference) {
        preference.setEnabled(
            EVSettings.System.getInt(
                mContext.contentResolver,
                EVSettings.System.EDGE_LIGHT_ENABLED,
                0
            ) != 1
        )
    }

    override fun getSliceHighlightMenuRes(): Int =
        com.android.settings.R.string.menu_key_notifications
}
