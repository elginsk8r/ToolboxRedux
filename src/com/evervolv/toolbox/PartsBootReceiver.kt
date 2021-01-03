/*
 * SPDX-FileCopyrightText: 2024 Evervolv
 * SPDX-License-Identifier: Apache-2.0
 */

package com.evervolv.toolbox

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.UserManager
import android.util.Log
import androidx.preference.PreferenceManager
import evervolv.hardware.HardwareManager

import com.evervolv.toolbox.gestures.TouchscreenGestureConstants

class PartsBootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val userManager = context.getSystemService(UserManager::class.java)!!
        if (!userManager.isSystemUser) {
            Log.d(TAG, "Not running as the primary user, skipping tunable restoration.")
            return
        }

        HardwareManager.getInstance(context)?.let { hardware ->
            if (hardware.isSupported(HardwareManager.FEATURE_TOUCHSCREEN_GESTURES)) {
                hardware.touchscreenGestures.let { gestures ->
                    val actionList: IntArray = TouchscreenGestureConstants.buildActionList(context, gestures)
                    for (gesture in gestures) {
                        hardware.setTouchscreenGestureEnabled(gesture, actionList[gesture.id] > 0)
                    }
                    TouchscreenGestureConstants.sendUpdateBroadcast(context, gestures)
                }
            }

            if (hardware.isSupported(HardwareManager.FEATURE_KEY_SWAP)) {
                val sharedPreferences = 
                    PreferenceManager.getDefaultSharedPreferences(context)
                hardware.set(
                    HardwareManager.FEATURE_KEY_SWAP,
                    sharedPreferences.getBoolean("swap_capacitive_keys", false)
                )
            }
        }
    }

    companion object {
        private const val TAG = "PartsBootReceiver"
    }
}
