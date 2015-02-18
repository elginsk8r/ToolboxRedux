/*
 * SPDX-FileCopyrightText: 2024 Evervolv
 * SPDX-License-Identifier: Apache-2.0
 */

package com.evervolv.settings.display

import android.content.Context
import com.android.settings.core.BasePreferenceController
import evervolv.hardware.LiveDisplayManager

class LiveDisplayPreferenceController(
    context: Context,
    key: String
) : BasePreferenceController(context, key) {

    private val manager: LiveDisplayManager = LiveDisplayManager.getInstance(mContext)

    override fun getAvailabilityStatus(): Int =
        if (mContext.resources.getBoolean(
            com.evervolv.platform.internal.R.bool.config_enableLiveDisplay
        )) {
            CONDITIONALLY_UNAVAILABLE
        } else if (manager.config.isAvailable()) {
            AVAILABLE
        } else {
            UNSUPPORTED_ON_DEVICE
        }
}

