/*
 * SPDX-FileCopyrightText: 2024 Evervolv
 * SPDX-License-Identifier: Apache-2.0
 */

package com.evervolv.toolbox.inputmethod

import android.content.Context
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceScreen
import com.android.settings.core.BasePreferenceController
import com.evervolv.internal.util.DeviceKeysConstants.Action
import evervolv.provider.EVSettings

class PressActionPreferenceController(
    context: Context,
    key: String
) : BasePreferenceController(context, key) {

    private val category = getPreferenceKey().split("_")[1]
    private val setting = if (category == ButtonSettings.CATEGORY_HOME) {
        "key_${category}_double_tap_action"
    } else {
        "key_${category}_action"
    }

    override fun getAvailabilityStatus(): Int = AVAILABLE

    override fun displayPreference(screen: PreferenceScreen) {
        super.displayPreference(screen)
        val preference: ListPreference = screen.findPreference(getPreferenceKey())!!
        preference.let {
            val value = Action.fromSettings(
                mContext.contentResolver,
                setting,
                when(category) {
                    ButtonSettings.CATEGORY_HOME -> Action.fromIntSafe(
                        mContext.resources.getInteger(
                            com.android.internal.R.integer.config_doubleTapOnHomeBehavior
                        )
                    )
                    ButtonSettings.CATEGORY_MENU -> Action.MENU
                    ButtonSettings.CATEGORY_ASSIST -> Action.SEARCH
                    ButtonSettings.CATEGORY_APPSWITCH -> Action.APP_SWITCH
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
