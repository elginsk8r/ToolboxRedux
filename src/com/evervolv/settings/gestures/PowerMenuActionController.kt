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
import com.android.settings.R
import com.android.settings.core.TogglePreferenceController
import com.android.settingslib.applications.ServiceListing
import com.android.settingslib.development.DevelopmentSettingsEnabler
import evervolv.app.GlobalActionManager
import com.evervolv.internal.util.PowerMenuConstants

class PowerMenuActionController(
    context: Context,
    key: String,
) : TogglePreferenceController(context, key) {

    private val telephony = mContext.getSystemService(TelephonyManager::class.java)!!
    private val userManager = UserManager.get(mContext)
    private val userInfo = userManager.getUserInfo(UserHandle.myUserId())!!
    private val emergencyAffordance = EmergencyAffordanceManager(mContext).needsEmergencyAffordance()
    private val actionManager = GlobalActionManager.getInstance(mContext)

    override fun getAvailabilityStatus(): Int {
        val available = when (getPreferenceKey()) {
            "users" -> UserHandle.MU_ENABLED && UserManager.supportsMultipleUsers()
            "emergency" -> telephony.isVoiceCapable()
            else -> true
        }
        return if (PowerMenuConstants.getAllActions().contains(getPreferenceKey()) && available) {
            AVAILABLE_UNSEARCHABLE
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
                Settings.Global.putInt(
                    mContext.contentResolver,
                    Settings.Global.BUGREPORT_IN_POWER_MENU,
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
                    DevelopmentSettingsEnabler.isDevelopmentSettingsEnabled(mContext) &&
                        userInfo.isPrimary()
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

    override fun getSliceHighlightMenuRes(): Int {
        return R.string.menu_key_system
    }
}

