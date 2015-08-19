/*
 * SPDX-FileCopyrightText: 2024 Evervolv
 * SPDX-License-Identifier: Apache-2.0
 */

package com.evervolv.settings.display

import android.content.Context
import com.android.settings.core.BasePreferenceController
import com.evervolv.internal.util.ResourceUtils
import com.evervolv.settings.display.DisplayModePickerFragment.Companion.COLOR_PROFILE_TITLE
import evervolv.hardware.DisplayMode
import evervolv.hardware.HardwareManager
import evervolv.hardware.HardwareManager.FEATURE_DISPLAY_MODES

class DisplayModePreferenceController(
    context: Context,
    key: String
) : BasePreferenceController(context, key) {

    private val manager: HardwareManager = HardwareManager.getInstance(mContext)

    override fun getAvailabilityStatus(): Int =
        if (manager.isSupported(FEATURE_DISPLAY_MODES)) {
            AVAILABLE_UNSEARCHABLE
        } else {
            UNSUPPORTED_ON_DEVICE
        }

    override fun getSummary(): CharSequence {
        val currentMode: DisplayMode = manager.getCurrentDisplayMode()
            .takeIf { it != null } ?: manager.getDefaultDisplayMode()
        return ResourceUtils.getLocalizedString(
            mContext.resources, currentMode.name, COLOR_PROFILE_TITLE
        )
    }
}

