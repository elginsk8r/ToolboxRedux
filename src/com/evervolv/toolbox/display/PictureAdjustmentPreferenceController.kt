/*
 * SPDX-FileCopyrightText: 2024 Evervolv
 * SPDX-License-Identifier: Apache-2.0
 */

package com.evervolv.toolbox.display

import android.content.Context
import com.android.settings.core.BasePreferenceController
import evervolv.hardware.LiveDisplayManager

class PictureAdjustmentPreferenceController(
    context: Context,
    key: String
) : BasePreferenceController(context, key) {

    override fun getAvailabilityStatus(): Int {
        return LiveDisplayManager.getInstance(mContext)?.config?.takeIf {
            it.hasFeature(LiveDisplayManager.FEATURE_PICTURE_ADJUSTMENT)
        }?.takeIf {
            mContext.resources.getBoolean(
                com.evervolv.platform.internal.R.bool.config_enableLiveDisplay
            )
        }?.let { AVAILABLE } ?: UNSUPPORTED_ON_DEVICE
    }
}
