/*
 * SPDX-FileCopyrightText: 2024 Evervolv
 * SPDX-License-Identifier: Apache-2.0
 */

package com.evervolv.settings.inputmethod

import android.content.Context
import com.android.settings.core.BasePreferenceController

class CategoryPreferenceController(
    context: Context,
    key: String,
    private val available: Boolean
) : BasePreferenceController(context, key) {

    override fun getAvailabilityStatus(): Int =
        if (available) AVAILABLE else UNSUPPORTED_ON_DEVICE
}
