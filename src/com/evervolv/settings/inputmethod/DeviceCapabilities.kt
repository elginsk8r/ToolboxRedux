/*
 * SPDX-FileCopyrightText: 2024 Evervolv
 * SPDX-License-Identifier: Apache-2.0
 */

package com.evervolv.settings.inputmethod

import android.content.Context
import com.evervolv.internal.util.DeviceKeysConstants.KEY_MASK_HOME
import com.evervolv.internal.util.DeviceKeysConstants.KEY_MASK_MENU
import com.evervolv.internal.util.DeviceKeysConstants.KEY_MASK_ASSIST
import com.evervolv.internal.util.DeviceKeysConstants.KEY_MASK_APP_SWITCH
import com.evervolv.platform.internal.R.integer.config_deviceHardwareKeys

object DeviceCapabilities {

    private fun getResourcesInteger(context: Context, id: Int): Int =
        context.resources.getInteger(id)

    fun getDeviceKeys(context: Context): Int =
        getResourcesInteger(context, config_deviceHardwareKeys)

    fun hasKey(context: Context, keyMask: Int): Boolean =
        getDeviceKeys(context) and keyMask != 0

    fun hasHomeKey(context: Context): Boolean = hasKey(context, KEY_MASK_HOME)

    fun hasMenuKey(context: Context): Boolean = hasKey(context, KEY_MASK_MENU)

    fun hasAssistKey(context: Context): Boolean = hasKey(context, KEY_MASK_ASSIST)

    fun hasAppSwitchKey(context: Context): Boolean = hasKey(context, KEY_MASK_APP_SWITCH)
}
