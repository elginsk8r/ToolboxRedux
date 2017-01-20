/*
 * SPDX-FileCopyrightText: 2024 Evervolv
 * SPDX-License-Identifier: Apache-2.0
 */

package com.evervolv.settings

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.UserManager
import android.util.Log
import androidx.preference.PreferenceManager
import evervolv.hardware.HardwareManager

import com.evervolv.settings.gestures.TouchscreenGestureConstants

class ToolboxBootReceiver : BroadcastReceiver() {
    private val hardwareFeatures: Map<Int, String> = mapOf(
        HardwareManager.FEATURE_KEY_SWAP to "swap_capacitive_keys",
    )

    override fun onReceive(context: Context, intent: Intent) {
        val userManager = context.getSystemService(UserManager::class.java)!!
        if (!userManager.isSystemUser) {
            Log.d(TAG, "Not running as the primary user, skipping tunable restoration.")
            return
        }

        HardwareManager.getInstance(context)?.let { hardware ->
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                context
            )
            hardwareFeatures.forEach { (feature, key) ->
                if (hardware.isSupported(feature)) {
                    hardware.set(feature, sharedPreferences.getBoolean(key, false))
                }
            }
            if (hardware.isSupported(HardwareManager.FEATURE_TOUCHSCREEN_GESTURES)) {
                hardware.touchscreenGestures.let { gestures ->
                    val actionList: IntArray = TouchscreenGestureConstants.buildActionList(context, gestures)
                    for (gesture in gestures) {
                        hardware.setTouchscreenGestureEnabled(gesture, actionList[gesture.id] > 0)
                    }
                    TouchscreenGestureConstants.sendUpdateBroadcast(context, gestures)
                }
            }
        }
    }

    companion object {
        private const val TAG = "ToolboxBootReceiver"
    }
}
