/*
 * SPDX-FileCopyrightText: 2024 Evervolv
 * SPDX-License-Identifier: Apache-2.0
 */

package com.evervolv.settings.gestures

import com.android.settings.R
import com.android.settings.dashboard.DashboardFragment
import com.android.settings.search.BaseSearchIndexProvider
import com.evervolv.internal.logging.EVMetricsLogger

class PowerMenuActions : DashboardFragment() {

    override fun getPreferenceScreenResId() = R.xml.power_menu_actions
    override fun getMetricsCategory() = EVMetricsLogger.DONT_LOG
    override fun getLogTag() = TAG

    companion object {
        private const val TAG = "PowerMenuActions"
    }
}
