/*
 * SPDX-FileCopyrightText: 2024 Evervolv
 * SPDX-License-Identifier: Apache-2.0
 */

package com.evervolv.settings.development

import android.content.Context
import com.android.settings.R
import com.android.settings.core.TogglePreferenceController
import com.android.settingslib.development.DevelopmentSettingsEnabler
import evervolv.provider.EVSettings

/**
 * Preference controller for "Color enhancement"
 */
class AdvancedRebootController(
    context: Context,
    key: String
) : TogglePreferenceController(context, key) {

    override fun getAvailabilityStatus(): Int =
        if (DevelopmentSettingsEnabler.isDevelopmentSettingsEnabled(mContext))
            AVAILABLE_UNSEARCHABLE
        else
            UNSUPPORTED_ON_DEVICE

    override fun isChecked(): Boolean =
        EVSettings.Secure.getInt(
            mContext.contentResolver,
            EVSettings.Secure.ADVANCED_REBOOT,
            0
        ) != 0

    override fun setChecked(isChecked: Boolean): Boolean =
        EVSettings.Secure.putInt(
            mContext.contentResolver,
            EVSettings.Secure.ADVANCED_REBOOT,
            if (isChecked) 1 else 0
        )

    override fun getSliceHighlightMenuRes(): Int = R.string.menu_key_system
}
