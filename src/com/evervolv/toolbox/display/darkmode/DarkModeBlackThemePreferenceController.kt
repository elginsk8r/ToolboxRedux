/*
 * SPDX-FileCopyrightText: 2024 Evervolv
 * SPDX-License-Identifier: Apache-2.0
 */

package com.evervolv.toolbox.display.darkmode

import android.content.Context
import android.content.pm.PackageManager
import com.android.settings.core.TogglePreferenceController
import evervolv.provider.EVSettings

class DarkModeBlackThemePreferenceController(
    context: Context,
    key: String,
) : TogglePreferenceController(context, key) {

    override fun getAvailabilityStatus() =
        if (runCatching {
            mContext.packageManager.getApplicationInfo(
                "org.lineageos.overlay.customization.blacktheme",
                PackageManager.ApplicationInfoFlags.of(0)
            ).enabled
        }.getOrDefault(false)) AVAILABLE else UNSUPPORTED_ON_DEVICE

    override fun isChecked(): Boolean =
        EVSettings.Secure.getInt(
            mContext.contentResolver,
            EVSettings.Secure.BERRY_BLACK_THEME,
            0
        ) != 0

    override fun setChecked(isChecked: Boolean): Boolean =
        EVSettings.Secure.putInt(
            mContext.contentResolver,
            EVSettings.Secure.BERRY_BLACK_THEME,
            if (isChecked) 1 else 0
        )

    override fun getSliceHighlightMenuRes(): Int =
        com.android.settings.R.string.menu_key_display
}
