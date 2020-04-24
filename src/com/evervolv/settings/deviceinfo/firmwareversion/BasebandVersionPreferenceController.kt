/*
 * SPDX-FileCopyrightText: 2024 Evervolv
 * SPDX-License-Identifier: Apache-2.0
 */

package com.evervolv.settings.deviceinfo.firmwareversion

import android.content.Context
import android.os.SystemProperties
import android.telephony.TelephonyManager
import com.android.settings.R
import com.android.settings.core.BasePreferenceController

class BasebandVersionPreferenceController(
    context: Context,
    preferenceKey: String
) : BasePreferenceController(context, preferenceKey) {

    private val telephonyManager =
        mContext.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    private val telephonyAvailable: Boolean =
        telephonyManager.phoneType != TelephonyManager.PHONE_TYPE_NONE

    override fun getAvailabilityStatus(): Int = 
        if (telephonyAvailable) AVAILABLE else UNSUPPORTED_ON_DEVICE

    override fun getSummary(): CharSequence =
        SystemProperties.get(
            "gsm.version.baseband",
            mContext.getString(R.string.device_info_default)
        ).split(",").firstOrNull {
            it.isNotEmpty()
        } ?: mContext.getString(R.string.device_info_default)
}
