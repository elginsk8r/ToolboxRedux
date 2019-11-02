/*
 * SPDX-FileCopyrightText: 2024 Evervolv
 * SPDX-License-Identifier: Apache-2.0
 */

package com.evervolv.settings.development

import android.adb.ADBRootService
import android.content.Context
import com.android.settings.R
import com.android.settings.core.TogglePreferenceController
import com.android.settingslib.development.DevelopmentSettingsEnabler

class AdbRootPreferenceController(
    private val context: Context,
    key: String,
) : TogglePreferenceController(context, key) {

    private val service = ADBRootService()

    override fun getAvailabilityStatus() =
        if (DevelopmentSettingsEnabler.isDevelopmentSettingsEnabled(mContext))
            AVAILABLE_UNSEARCHABLE
        else
            UNSUPPORTED_ON_DEVICE

    override fun isChecked(): Boolean = service.enabled

    override fun setChecked(isChecked: Boolean): Boolean {
        service.enabled = isChecked
        return true
    }

    override fun getSliceHighlightMenuRes(): Int {
        return R.string.menu_key_system
    }
}

