/*
 * SPDX-FileCopyrightText: 2024 Evervolv
 * SPDX-License-Identifier: Apache-2.0
 */

package com.evervolv.settings.inputmethod

import android.content.Context
import com.android.settings.R
import com.android.settings.core.TogglePreferenceController
import evervolv.provider.EVSettings
import evervolv.provider.EVSettings.System.VOLBTN_MUSIC_CONTROLS

class VolumeSkipTrackPreferenceController(
    context: Context,
    key: String,
) : TogglePreferenceController(context, key) {

    override fun getAvailabilityStatus(): Int = AVAILABLE_UNSEARCHABLE

    override fun isChecked(): Boolean =
        EVSettings.System.getInt(
            mContext.contentResolver, VOLBTN_MUSIC_CONTROLS, 0
        ) == 1

    override fun setChecked(isChecked: Boolean): Boolean =
        EVSettings.System.putInt(
            mContext.contentResolver, VOLBTN_MUSIC_CONTROLS, if (isChecked) 1 else 0
        )

    override fun getSliceHighlightMenuRes(): Int {
        return R.string.menu_key_system
    }
}
