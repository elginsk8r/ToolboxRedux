/*
 * SPDX-FileCopyrightText: 2024 Evervolv
 * SPDX-License-Identifier: Apache-2.0
 */

package com.evervolv.settings.gestures

import android.content.Context
import com.android.settings.core.BasePreferenceController
import vendor.evervolv.touch.V1_0.ITouchscreenGesture

class TouchscreenGesturePreferenceController(
    context: Context,
    key: String
) : BasePreferenceController(context, key) {

    private var touchscreenGesture: ITouchscreenGesture? = runCatching {
        ITouchscreenGesture.getService(false)
    }.getOrNull()

    override fun getAvailabilityStatus() =
        if (touchscreenGesture != null) AVAILABLE_UNSEARCHABLE else UNSUPPORTED_ON_DEVICE
}
