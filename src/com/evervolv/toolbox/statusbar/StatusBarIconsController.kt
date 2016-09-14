/*
 * SPDX-FileCopyrightText: 2024 Evervolv
 * SPDX-License-Identifier: Apache-2.0
 */

package com.evervolv.toolbox.statusbar

import android.content.Context
import android.content.Intent
import com.android.settings.Utils
import com.android.settings.core.BasePreferenceController

class StatusBarIconsController(
    context: Context,
    key: String
) : BasePreferenceController(context, key) {

    override fun getAvailabilityStatus(): Int =
        AVAILABLE_UNSEARCHABLE.takeIf {
            Utils.isBatteryPresent(mContext) ||
            mContext.packageManager.queryIntentActivities(
                Intent("com.android.settings.action.STATUS_BAR_TUNER"), 0
            ).isNotEmpty()
        } ?: CONDITIONALLY_UNAVAILABLE
}

