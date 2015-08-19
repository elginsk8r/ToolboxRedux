/*
 * SPDX-FileCopyrightText: 2024 Evervolv
 * SPDX-License-Identifier: Apache-2.0
 */

package com.evervolv.toolbox.display

import android.content.Context
import com.android.settings.core.BasePreferenceController
import com.evervolv.toolbox.display.DisplayModePickerFragment.Companion.COLOR_PROFILE_TITLE
import com.evervolv.toolbox.utils.ResourceUtils
import evervolv.hardware.HardwareManager

class DisplayModePreferenceController(
    context: Context,
    key: String
) : BasePreferenceController(context, key) {

    private val manager: HardwareManager = HardwareManager.getInstance(mContext)

    override fun getAvailabilityStatus(): Int =
        if (manager.isSupported(HardwareManager.FEATURE_DISPLAY_MODES)) {
            AVAILABLE_UNSEARCHABLE
        } else {
            UNSUPPORTED_ON_DEVICE
        }

    override fun getSummary(): CharSequence {
        val currentMode = manager.getCurrentDisplayMode()
            .takeIf { it != null } ?: manager.getDefaultDisplayMode()
        return ResourceUtils.getLocalizedString(
            mContext, currentMode.name, COLOR_PROFILE_TITLE
        )
    }
}

