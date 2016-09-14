/*
 * SPDX-FileCopyrightText: 2024 Evervolv
 * SPDX-License-Identifier: Apache-2.0
 */

package com.evervolv.toolbox.statusbar

import android.content.Context
import com.android.settings.Utils
import com.android.settings.core.BasePreferenceController

class StatusBarIconsController(
    context: Context,
    key: String
) : BasePreferenceController(context, key) {

    override fun getAvailabilityStatus(): Int =
        AVAILABLE_UNSEARCHABLE.takeIf {
            Utils.isBatteryPresent(mContext)
        } ?: CONDITIONALLY_UNAVAILABLE
}

