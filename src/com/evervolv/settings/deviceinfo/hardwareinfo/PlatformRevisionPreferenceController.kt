/*
 * SPDX-FileCopyrightText: 2024 Evervolv
 * SPDX-License-Identifier: Apache-2.0
 */

package com.evervolv.settings.deviceinfo.hardwareinfo

import android.content.Context
import android.os.SystemProperties
import com.android.settings.R
import com.android.settings.core.BasePreferenceController

class PlatformRevisionPreferenceController(
    context: Context,
    preferenceKey: String
) : BasePreferenceController(context, preferenceKey) {

    private val boardPlatform: String = 
        SystemProperties.get(
            "ro.board.platform",
            mContext.getString(R.string.device_info_default)
        )

    override fun getAvailabilityStatus(): Int =
        if (boardPlatform != mContext.getString(R.string.device_info_default)) {
            AVAILABLE_UNSEARCHABLE
        } else {
            UNSUPPORTED_ON_DEVICE
        }

    override fun getSummary(): CharSequence = boardPlatform
}
