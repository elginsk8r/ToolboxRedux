/*
 * SPDX-FileCopyrightText: 2024 Evervolv
 * SPDX-License-Identifier: Apache-2.0
 */

package com.evervolv.settings.fuelgauge

import android.content.Context
import android.os.RemoteException
import com.android.settings.R
import com.android.settings.core.TogglePreferenceController
import vendor.evervolv.fastcharge.V1_0.IFastCharge

/**
 * Preference controller for "Fast Charging"
 */
class FastChargingPreferenceController(
    context: Context,
    key: String,
) : TogglePreferenceController(context, key) {

    private var fastCharge: IFastCharge? = runCatching {
        IFastCharge.getService(false)
    }.getOrNull()

    override fun getAvailabilityStatus() =
        if (fastCharge != null) AVAILABLE_UNSEARCHABLE else UNSUPPORTED_ON_DEVICE

    override fun isChecked(): Boolean = runCatching {
        fastCharge?.isEnabled() ?: false
    }.getOrDefault(false)


    override fun setChecked(isChecked: Boolean): Boolean = runCatching {
        fastCharge?.setEnabled(isChecked) ?: false
    }.getOrDefault(false)

    override fun getSliceHighlightMenuRes(): Int {
        return R.string.menu_key_battery
    }
}
