/*
 * SPDX-FileCopyrightText: 2024 Evervolv
 * SPDX-License-Identifier: Apache-2.0
 */

package com.evervolv.toolbox.inputmethod

import android.content.Context
import com.android.settings.dashboard.DashboardFragment
import com.evervolv.toolbox.R
import com.evervolv.internal.logging.EVMetricsLogger

class ButtonSettings : DashboardFragment() {

    override fun getPreferenceScreenResId() = R.xml.button_settings
    override fun getMetricsCategory() = EVMetricsLogger.DONT_LOG
    override fun getLogTag() = TAG

    companion object {
        private const val TAG = "ButtonSettings"
    }
}
