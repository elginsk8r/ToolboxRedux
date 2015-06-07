/*
 * SPDX-FileCopyrightText: 2024 Evervolv
 * SPDX-License-Identifier: Apache-2.0
 */

package com.evervolv.settings.inputmethod

import android.content.Context
import com.android.settings.R
import com.android.settings.dashboard.DashboardFragment
import com.evervolv.internal.logging.EVMetricsLogger

class ButtonSettings : DashboardFragment() {

    override fun getPreferenceScreenResId() = R.xml.button_settings
    override fun getMetricsCategory() = EVMetricsLogger.DONT_LOG
    override fun getLogTag() = TAG

    override fun createPreferenceControllers(context: Context) =
        listOf(
            CategoryPreferenceController(
                context,
                "home_key",
                DeviceCapabilities.hasHomeKey(context)
            ),
            CategoryPreferenceController(
                context,
                "back_key",
                DeviceCapabilities.hasBackKey(context)
            ),
            CategoryPreferenceController(
                context,
                "menu_key",
                DeviceCapabilities.hasMenuKey(context)
            ),
            CategoryPreferenceController(
                context,
                "assist_key",
                DeviceCapabilities.hasAssistKey(context)
            ),
            CategoryPreferenceController(
                context,
                "app_switch_key",
                DeviceCapabilities.hasAppSwitchKey(context)
            ),
            CategoryPreferenceController(
                context,
                "camera_key",
                DeviceCapabilities.hasCameraKey(context)
            ),
            CategoryPreferenceController(
                context,
                "volume_keys",
                DeviceCapabilities.hasVolumeKeys(context)
            ),
        )

    companion object {
        private const val TAG = "ButtonSettings"
    }
}
