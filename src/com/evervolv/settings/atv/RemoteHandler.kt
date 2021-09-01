/*
 * SPDX-FileCopyrightText: 2025 Evervolv
 * SPDX-License-Identifier: Apache-2.0
 */

package com.evervolv.settings.atv.remote

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.Settings
import android.util.Log
import android.util.SparseArray
import android.view.KeyEvent
import com.android.internal.os.DeviceKeyHandler
import com.evervolv.platform.internal.R

class RemoteHandler(private val context: Context) : DeviceKeyHandler {

    private val remoteCode = context.resources.getIntArray(
        R.array.keyhandler_keycodes
    )
    private val remotePackages = context.resources.getStringArray(
        R.array.keyhandler_packages
    )
    private var remoteActions = SparseArray<String>()

    init {
        remoteCode.forEachIndexed { index, keyCode ->
            remoteActions.put(keyCode, remotePackages[index])
        }
    }

    override fun handleKeyEvent(event: KeyEvent): KeyEvent? {
        if (event.action != KeyEvent.ACTION_UP || !isSetupComplete()) {
            return event
        }
        
        val keyCode = event.keyCode
        val targetName = remoteActions.get(keyCode)
        
        targetName?.let {
            launchTarget(it)
            return null
        }
        
        return event
    }

    private fun isSetupComplete(): Boolean {
        return Settings.Secure.getInt(
            context.contentResolver,
            Settings.Secure.TV_USER_SETUP_COMPLETE,
            0
        ) != 0
    }

    private fun launchTarget(targetName: String) {
        var launchIntent: Intent? =
            context.packageManager.getLaunchIntentForPackage(targetName)

        if (launchIntent == null) {
            launchIntent = Intent(targetName).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                putExtra("no_input_mode", true)
            }
            val launchComponent = launchIntent.resolveActivity(
                context.packageManager
            )
            if (launchComponent == null) {
                launchIntent = null
            }
        }

        if (launchIntent != null) {
            context.startActivity(launchIntent)
        } else {
            Log.w(TAG, "Cannot launch $targetName.")
        }
    }

    companion object {
        private const val TAG = "RemoteHandler"
    }
}
