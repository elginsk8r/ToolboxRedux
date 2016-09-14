/*
 * SPDX-FileCopyrightText: 2024 Evervolv
 * SPDX-License-Identifier: Apache-2.0
 */

package com.evervolv.toolbox.core

import android.content.Context
import android.content.Intent
import com.android.settings.core.BasePreferenceController

class SettingsIntentPreferenceController(
    context: Context,
    key: String
) : BasePreferenceController(context, key) {

    private val SETTINGS_ACTION = "com.android.settings.action.${getPreferenceKey().uppercase()}"

    override fun getAvailabilityStatus(): Int =
        if (mContext.packageManager.queryIntentActivities(Intent(SETTINGS_ACTION), 0).isNotEmpty()) {
            AVAILABLE_UNSEARCHABLE
        } else {
            UNSUPPORTED_ON_DEVICE
        }
}

