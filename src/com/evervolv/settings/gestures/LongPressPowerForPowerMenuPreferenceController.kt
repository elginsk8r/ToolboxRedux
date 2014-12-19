/*
 * SPDX-FileCopyrightText: 2024 Evervolv
 * SPDX-License-Identifier: Apache-2.0
 */

package com.evervolv.settings.gestures

import android.content.Context
import android.content.Intent
import android.view.View
import androidx.preference.PreferenceScreen
import com.android.settingslib.widget.SelectorWithWidgetPreference

class LongPressPowerForPowerMenuPreferenceController(
    context: Context,
    key: String
) : com.android.settings.gestures.LongPressPowerForPowerMenuPreferenceController(context, key) {

    override fun displayPreference(screen: PreferenceScreen) {
        super.displayPreference(screen)
        val preference: SelectorWithWidgetPreference = screen.findPreference(getPreferenceKey())!!
        preference.setExtraWidgetOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                mContext.startActivity(
                    Intent("com.evervolv.settings.POWER_MENU_SETTINGS")
                        .setPackage(mContext.packageName)
                )
            }
        })
    }
}
