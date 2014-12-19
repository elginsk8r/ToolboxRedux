/*
 * SPDX-FileCopyrightText: 2024 Evervolv
 * SPDX-License-Identifier: Apache-2.0
 */

package com.evervolv.settings.gestures

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.UserHandle
import android.os.UserManager
import android.provider.Settings
import android.service.controls.ControlsProviderService
import android.telephony.TelephonyManager
import androidx.preference.Preference
import com.android.internal.util.EmergencyAffordanceManager
import com.android.settings.core.TogglePreferenceController
import com.android.settingslib.applications.ServiceListing
import evervolv.app.GlobalActionManager
import com.evervolv.internal.util.PowerMenuConstants

class PowerMenuActionController(
    context: Context,
    key: String,
) : TogglePreferenceController(context, key) {

    private val telephony = mContext.getSystemService(TelephonyManager::class.java)!!
    private val userManager = mContext.getSystemService(UserManager::class.java)!!
    private val emergencyAffordance = EmergencyAffordanceManager(mContext).needsEmergencyAffordance()
    private val actionManager = GlobalActionManager.getInstance(mContext)

    override fun getAvailabilityStatus(): Int {
        val available = when (getPreferenceKey()) {
            "users" -> UserHandle.MU_ENABLED && UserManager.supportsMultipleUsers()
            "emergency" -> telephony.isDeviceVoiceCapable()
            else -> true
        }
        return if (PowerMenuConstants.getAllActions().contains(getPreferenceKey()) && available) {
            AVAILABLE
        } else {
            UNSUPPORTED_ON_DEVICE
        }
    }

    override fun isChecked(): Boolean =
        when (getPreferenceKey()) {
            "emergency" -> actionManager.userConfigContains(getPreferenceKey()) || emergencyAffordance
            else -> actionManager.userConfigContains(getPreferenceKey())
        }

    override fun setChecked(isChecked: Boolean): Boolean =
        when (getPreferenceKey()) {
            "bugreport" -> {
                actionManager.updateUserConfig(isChecked, getPreferenceKey())
                Settings.Secure.putInt(
                    mContext.contentResolver,
                    Settings.Secure.BUGREPORT_IN_POWER_MENU,
                    if (isChecked) 1 else 0
                )
                true
            }
            else -> {
                actionManager.updateUserConfig(isChecked, getPreferenceKey())
                true
            }
        }

    override fun updateState(preference: Preference) {
        super.updateState(preference)
        when (preference.key) {
            "bugreport" -> {
                preference.isEnabled =
                    !userManager.hasUserRestriction(UserManager.DISALLOW_DEBUGGING_FEATURES)
            }
            "devicecontrols" -> {
                val serviceListing = ServiceListing.Builder(mContext)
                    .setIntentAction(ControlsProviderService.SERVICE_CONTROLS)
                    .setPermission(Manifest.permission.BIND_CONTROLS)
                    .setNoun("Controls Provider")
                    .setSetting("controls_providers")
                    .setTag("controls_providers")
                    .build()

                serviceListing.addCallback { services ->
                    preference.isEnabled = services.isNotEmpty()
                }
                serviceListing.reload()
            }
            "emergency" -> preference.isEnabled = emergencyAffordance
            "users" -> preference.isEnabled = userManager.users.size > 1
        }
    }

    override fun getSliceHighlightMenuRes(): Int =
        com.android.settings.R.string.menu_key_system
}

