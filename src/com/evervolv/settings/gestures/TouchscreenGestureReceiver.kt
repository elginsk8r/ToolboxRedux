/*
 * SPDX-FileCopyrightText: 2024 Evervolv
 * SPDX-License-Identifier: Apache-2.0
 */

package com.evervolv.settings.gestures

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.UserManager
import android.util.Log
import vendor.evervolv.touch.V1_0.Gesture
import vendor.evervolv.touch.V1_0.ITouchscreenGesture

object TouchscreenGestureReceiver : BroadcastReceiver() {
    private const val TAG = "TouchscreenGestureReceiver"

    override fun onReceive(context: Context, intent: Intent) {
        val userManager = context.getSystemService(UserManager::class.java)!!
        if (!userManager.isSystemUser) {
            Log.d(TAG, "Not running as the primary user, skipping tunable restoration.")
            return
        }

        runCatching {
            val service: ITouchscreenGesture = ITouchscreenGesture.getService(false)
            val gestures: MutableList<Gesture> = service.supportedGestures
            val actionList: IntArray = TouchscreenGestureConstants.buildActionList(context, gestures)
            for (gesture in gestures) {
                    service.setGestureEnabled(gesture, actionList[gesture.id] > 0)
                }
            TouchscreenGestureConstants.sendUpdateBroadcast(context, gestures)
        }
    }
}
