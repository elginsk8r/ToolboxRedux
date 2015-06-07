/*
 * SPDX-FileCopyrightText: 2024 Evervolv
 * SPDX-License-Identifier: Apache-2.0
 */

package com.evervolv.settings.inputmethod

import com.android.settings.R
import com.android.settings.dashboard.DashboardFragment
import com.evervolv.internal.logging.EVMetricsLogger

class ButtonSettings : DashboardFragment() {

    override fun getPreferenceScreenResId() = R.xml.button_settings
    override fun getMetricsCategory() = EVMetricsLogger.DONT_LOG
    override fun getLogTag() = TAG

    companion object {
        private const val TAG = "ButtonSettings"

        const val CATEGORY_HOME = "home"
        const val CATEGORY_BACK = "back"
        const val CATEGORY_MENU = "menu"
        const val CATEGORY_ASSIST = "assist"
        const val CATEGORY_APPSWITCH = "app_switch"
        const val CATEGORY_CAMERA = "camera"
        const val CATEGORY_VOLUME = "volume"
    }
}
