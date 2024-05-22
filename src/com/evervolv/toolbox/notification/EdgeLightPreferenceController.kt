/*
 * SPDX-FileCopyrightText: 2024 Evervolv
 * SPDX-License-Identifier: Apache-2.0
 */

package com.evervolv.toolbox.notification

import android.content.Context
import com.android.settings.core.TogglePreferenceController
import evervolv.provider.EVSettings

class EdgeLightPreferenceController(
    context: Context,
    key: String
) : TogglePreferenceController(context, key) {

    override fun getAvailabilityStatus(): Int = AVAILABLE

    override fun isChecked(): Boolean =
        EVSettings.System.getInt(
            mContext.contentResolver,
            EVSettings.System.EDGE_LIGHT_ENABLED,
            0
        ) != 1

    override fun setChecked(isChecked: Boolean): Boolean =
        EVSettings.System.putInt(
            mContext.contentResolver,
            EVSettings.System.EDGE_LIGHT_ENABLED,
            if (isChecked) 1 else 0
        )

    override fun getSliceHighlightMenuRes(): Int =
        com.android.settings.R.string.menu_key_notifications
}
