/*
 * SPDX-FileCopyrightText: 2024 Evervolv
 * SPDX-License-Identifier: Apache-2.0
 */

package com.evervolv.toolbox.gestures

import android.content.Context
import com.android.settings.core.BasePreferenceController
import com.evervolv.internal.util.PowerMenuUtils

class PowerMenuCategoryController(
    context: Context,
    key: String
) : BasePreferenceController(context, key) {

    override fun getAvailabilityStatus(): Int =
        AVAILABLE.takeIf {
            PowerMenuUtils.isAdvancedRestartPossible(mContext)
        } ?: CONDITIONALLY_UNAVAILABLE
}

