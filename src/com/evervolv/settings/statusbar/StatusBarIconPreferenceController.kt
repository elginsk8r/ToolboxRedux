/*
 * SPDX-FileCopyrightText: 2024 Evervolv
 * SPDX-License-Identifier: Apache-2.0
 */

package com.evervolv.settings.statusbar

import android.content.Context
import android.content.Intent
import com.android.settings.core.BasePreferenceController

class StatusBarIconPreferenceController(
    context: Context,
    key: String
) : BasePreferenceController(context, key) {

    private val STATUS_BAR_TUNER = "com.android.settings.action.STATUS_BAR_TUNER"

    override fun getAvailabilityStatus(): Int =
        if (mContext.packageManager.queryIntentActivities(Intent(STATUS_BAR_TUNER), 0).isNotEmpty()) {
            AVAILABLE_UNSEARCHABLE
        } else {
            UNSUPPORTED_ON_DEVICE
        }
}

