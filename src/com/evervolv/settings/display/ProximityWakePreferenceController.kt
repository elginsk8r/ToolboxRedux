/*
 * SPDX-FileCopyrightText: 2024 Evervolv
 * SPDX-License-Identifier: Apache-2.0
 */

package com.evervolv.settings.display

import android.content.Context
import com.android.settings.R
import com.android.settings.core.TogglePreferenceController
import evervolv.provider.EVSettings

/**
 * Preference controller for "Proximity Wake"
 */
class ProximityWakePreferenceController(
    context: Context,
    key: String,
) : TogglePreferenceController(context, key) {

    override fun getAvailabilityStatus() =
        if (mContext.resources.getBoolean(com.evervolv.platform.internal.R.bool.config_proximityCheckOnWake))
            AVAILABLE_UNSEARCHABLE
        else
            UNSUPPORTED_ON_DEVICE

    override fun isChecked(): Boolean =
        EVSettings.System.getInt(
            mContext.contentResolver,
            EVSettings.System.PROXIMITY_ON_WAKE,
            if (mContext.resources.getBoolean(
                com.evervolv.platform.internal.R.bool.config_proximityCheckOnWakeEnabledByDefault
            )) 1 else 0
        ) != 0


    override fun setChecked(isChecked: Boolean): Boolean =
        EVSettings.System.putInt(
            mContext.contentResolver,
            EVSettings.System.PROXIMITY_ON_WAKE,
            if (isChecked) 1 else 0
        )

    override fun getSliceHighlightMenuRes(): Int {
        return R.string.menu_key_display
    }
}
