/*
 * SPDX-FileCopyrightText: 2025 Evervolv
 * SPDX-License-Identifier: Apache-2.0
 */

package com.evervolv.toolbox.atv

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.Settings
import android.util.Log
import android.util.SparseArray
import android.view.KeyEvent
import com.android.internal.os.DeviceKeyHandler

class KeyHandler(private val context: Context) : DeviceKeyHandler {

    private val remoteActions: Map<Int, String> = run {
        val remoteCode = context.resources.getIntArray(
            com.evervolv.platform.internal.R.array.keyhandler_keycodes
        )
        val remotePackages = context.resources.getStringArray(
            com.evervolv.platform.internal.R.array.keyhandler_packages
        )
        remoteCode.zip(remotePackages).toMap()
    }

    override fun handleKeyEvent(event: KeyEvent): KeyEvent? {
        return if (event.action != KeyEvent.ACTION_UP || !isSetupComplete()) {
            event
        } else {
            remoteActions[event.keyCode]?.let { packageName ->
                launchTarget(packageName)
                null
            } ?: event
        }
    }

    private fun isSetupComplete(): Boolean {
        return Settings.Secure.getInt(
            context.contentResolver,
            Settings.Secure.TV_USER_SETUP_COMPLETE,
            0
        ) != 0
    }

    private fun launchTarget(targetName: String) {
        context.packageManager.getLaunchIntentForPackage(targetName)
            ?.apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                putExtra("no_input_mode", true)
            }
            ?: Intent(targetName).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                putExtra("no_input_mode", true)
            }.takeIf { intent ->
                intent.resolveActivity(context.packageManager) != null
            }
            ?.let { context.startActivity(it) }
            ?: Log.w(TAG, "Cannot launch $targetName.")
    }

    companion object {
        private const val TAG = "KeyHandler"
    }
}
