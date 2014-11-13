/*
 * SPDX-FileCopyrightText: 2024 Evervolv
 * SPDX-License-Identifier: Apache-2.0
 */

package com.evervolv.settings.inputmethod

import android.content.Context
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceScreen
import com.android.settings.R
import com.android.settings.core.BasePreferenceController
import com.evervolv.internal.util.DeviceKeysConstants.Action
import com.evervolv.settings.inputmethod.ButtonSettings.Companion.CATEGORY_HOME
import com.evervolv.settings.inputmethod.ButtonSettings.Companion.CATEGORY_MENU
import com.evervolv.settings.inputmethod.ButtonSettings.Companion.CATEGORY_ASSIST
import com.evervolv.settings.inputmethod.ButtonSettings.Companion.CATEGORY_APPSWITCH
import evervolv.provider.EVSettings

class PressActionPreferenceController(
    context: Context,
    key: String
) : BasePreferenceController(context, key) {

    private val category = getPreferenceKey().split("_")[1]
    private val setting = if (category == CATEGORY_HOME) {
        "key_${category}_double_tap_action"
    } else {
        "key_${category}_action"
    }

    override fun getAvailabilityStatus(): Int =
        if (when (category) {
            CATEGORY_HOME -> DeviceCapabilities.hasHomeKey(mContext)
            CATEGORY_MENU -> DeviceCapabilities.hasMenuKey(mContext)
            CATEGORY_ASSIST -> DeviceCapabilities.hasAssistKey(mContext)
            CATEGORY_APPSWITCH -> DeviceCapabilities.hasAppSwitchKey(mContext)
            else -> false
        }) {
            AVAILABLE
        } else {
            UNSUPPORTED_ON_DEVICE
        }

    override fun displayPreference(screen: PreferenceScreen) {
        super.displayPreference(screen)
        val preference: ListPreference = screen.findPreference(getPreferenceKey())!!
        preference.let {
            val value = Action.fromSettings(
                mContext.contentResolver,
                setting,
                when(category) {
                    CATEGORY_HOME -> Action.fromIntSafe(
                        mContext.resources.getInteger(
                            com.android.internal.R.integer.config_doubleTapOnHomeBehavior
                        )
                    )
                    CATEGORY_MENU -> Action.MENU
                    CATEGORY_ASSIST -> Action.SEARCH
                    CATEGORY_APPSWITCH -> Action.APP_SWITCH
                    else -> Action.NOTHING
                },
            )
            it.setValue(value.ordinal.toString())
            it.setSummary(it.getEntry())
            it.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, newValue ->
                EVSettings.System.putInt(
                    mContext.contentResolver,
                    setting,
                    Integer.valueOf(newValue as String)
                )
                true
            }
        }
    }
}
