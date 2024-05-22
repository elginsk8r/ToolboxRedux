/*
 * SPDX-FileCopyrightText: 2024 Evervolv
 * SPDX-License-Identifier: Apache-2.0
 */

package com.evervolv.toolbox.notification

import android.content.Context
import com.android.settings.dashboard.DashboardFragment
import com.android.settings.search.BaseSearchIndexProvider
import com.evervolv.internal.logging.EVMetricsLogger
import com.evervolv.toolbox.R

class EdgeLightSettings : DashboardFragment() {

    override fun getPreferenceScreenResId() = R.xml.edge_light_settings
    override fun getMetricsCategory() = EVMetricsLogger.DONT_LOG
    override fun getLogTag() = TAG

    companion object {
        private const val TAG = "EdgeLightSettings"
    }
}
