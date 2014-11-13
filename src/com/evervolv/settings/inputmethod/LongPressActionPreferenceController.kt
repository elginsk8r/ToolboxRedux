/*
 * SPDX-FileCopyrightText: 2024 Evervolv
 * SPDX-License-Identifier: Apache-2.0
 */

package com.evervolv.settings.inputmethod

import android.content.Context
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceScreen
import com.android.settings.core.BasePreferenceController
import com.evervolv.internal.util.DeviceKeysConstants.Action
import evervolv.provider.EVSettings

class LongPressActionPreferenceController(
    context: Context,
    key: String
) : BasePreferenceController(context, key) {

    private val category = getPreferenceKey().split("_")[1]
    private val setting = "key_${category}_long_press_action"

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
                            com.android.internal.R.integer.config_longPressOnHomeBehavior
                        )
                    )
                    ButtonSettings.CATEGORY_MENU ->
                        if (DeviceCapabilities.hasAssistKey(mContext)) Action.NOTHING else Action.SEARCH
                    ButtonSettings.CATEGORY_ASSIST -> Action.VOICE_SEARCH
                    ButtonSettings.CATEGORY_APPSWITCH -> Action.SPLIT_SCREEN
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
