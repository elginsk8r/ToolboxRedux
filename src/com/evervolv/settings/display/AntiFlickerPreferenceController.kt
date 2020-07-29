/*
 * SPDX-FileCopyrightText: 2024 Evervolv
 * SPDX-License-Identifier: Apache-2.0
 */

package com.evervolv.settings.display

import android.content.Context
import com.android.settings.R
import com.android.settings.core.TogglePreferenceController
import evervolv.hardware.LiveDisplayManager
import evervolv.hardware.LiveDisplayManager.FEATURE_ANTI_FLICKER

/**
 * Preference controller for "Anti-Flicker"
 */
class AntiFlickerPreferenceController(
    context: Context,
    key: String
) : TogglePreferenceController(context, key) {

    private val manager: LiveDisplayManager = LiveDisplayManager.getInstance(mContext)

    override fun getAvailabilityStatus(): Int =
        if (manager.config.hasFeature(FEATURE_ANTI_FLICKER)) {
            AVAILABLE_UNSEARCHABLE
        } else {
            UNSUPPORTED_ON_DEVICE
        }

    override fun isChecked(): Boolean =
        manager.isAntiFlickerEnabled()

    override fun setChecked(isChecked: Boolean): Boolean =
        manager.setAntiFlickerEnabled(isChecked)

    override fun getSliceHighlightMenuRes(): Int = R.string.menu_key_display
}
