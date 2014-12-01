/*
 * SPDX-FileCopyrightText: 2024 Evervolv
 * SPDX-License-Identifier: Apache-2.0
 */

package com.evervolv.settings.inputmethod

import android.content.Context
import com.evervolv.internal.util.DeviceKeysConstants.KEY_MASK_HOME
import com.evervolv.internal.util.DeviceKeysConstants.KEY_MASK_BACK
import com.evervolv.internal.util.DeviceKeysConstants.KEY_MASK_MENU
import com.evervolv.internal.util.DeviceKeysConstants.KEY_MASK_ASSIST
import com.evervolv.internal.util.DeviceKeysConstants.KEY_MASK_APP_SWITCH
import com.evervolv.internal.util.DeviceKeysConstants.KEY_MASK_CAMERA
import com.evervolv.internal.util.DeviceKeysConstants.KEY_MASK_VOLUME
import com.evervolv.platform.internal.R.integer.config_deviceHardwareKeys
import com.evervolv.platform.internal.R.integer.config_deviceHardwareWakeKeys

object DeviceCapabilities {

    private fun getResourcesInteger(context: Context, id: Int): Int =
        context.resources.getInteger(id)

    fun getDeviceKeys(context: Context): Int =
        getResourcesInteger(context, config_deviceHardwareKeys)

    fun hasKey(context: Context, keyMask: Int): Boolean =
        getDeviceKeys(context) and keyMask != 0

    fun hasHomeKey(context: Context): Boolean = hasKey(context, KEY_MASK_HOME)
    
    fun hasBackKey(context: Context): Boolean = hasKey(context, KEY_MASK_BACK)

    fun hasMenuKey(context: Context): Boolean = hasKey(context, KEY_MASK_MENU)

    fun hasAssistKey(context: Context): Boolean = hasKey(context, KEY_MASK_ASSIST)

    fun hasAppSwitchKey(context: Context): Boolean = hasKey(context, KEY_MASK_APP_SWITCH)

    fun hasCameraKey(context: Context): Boolean = hasKey(context, KEY_MASK_CAMERA)

    fun hasVolumeKeys(context: Context): Boolean = hasKey(context, KEY_MASK_VOLUME)

    fun getDeviceWakeKeys(context: Context): Int =
        getResourcesInteger(context, config_deviceHardwareWakeKeys)

    fun canWakeUsingKey(context: Context, keyMask: Int): Boolean =
        getDeviceWakeKeys(context) and keyMask != 0

    fun canWakeUsingHomeKey(context: Context): Boolean = canWakeUsingKey(context, KEY_MASK_HOME)

    fun canWakeUsingBackKey(context: Context): Boolean = canWakeUsingKey(context, KEY_MASK_BACK)

    fun canWakeUsingMenuKey(context: Context): Boolean = canWakeUsingKey(context, KEY_MASK_MENU)

    fun canWakeUsingAssistKey(context: Context): Boolean = canWakeUsingKey(context, KEY_MASK_ASSIST)

    fun canWakeUsingAppSwitchKey(context: Context): Boolean = canWakeUsingKey(context, KEY_MASK_APP_SWITCH)

    fun canWakeUsingCameraKey(context: Context): Boolean = canWakeUsingKey(context, KEY_MASK_CAMERA)

    fun canWakeUsingVolumeKeys(context: Context): Boolean = canWakeUsingKey(context, KEY_MASK_VOLUME)
}
