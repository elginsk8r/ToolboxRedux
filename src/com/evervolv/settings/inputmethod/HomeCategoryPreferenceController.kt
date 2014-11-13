/*
 * SPDX-FileCopyrightText: 2024 Evervolv
 * SPDX-License-Identifier: Apache-2.0
 */

package com.evervolv.settings.inputmethod

import android.content.Context
import com.android.settings.core.BasePreferenceController

class HomeCategoryPreferenceController(
    context: Context,
    key: String
) : BasePreferenceController(context, key) {

    override fun getAvailabilityStatus(): Int =
        if (DeviceCapabilities.hasHomeKey(mContext)) {
            AVAILABLE
        } else {
            UNSUPPORTED_ON_DEVICE
        }
}
