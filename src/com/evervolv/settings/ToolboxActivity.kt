/*
 * SPDX-FileCopyrightText: 2024 Evervolv
 * SPDX-License-Identifier: Apache-2.0
 */

package com.evervolv.settings

import com.android.settings.SettingsActivity

open class ToolboxActivity : SettingsActivity() {
    override fun isValidFragment(fragmentName: String): Boolean {
        return ToolboxGateway.ENTRY_FRAGMENTS.contains(fragmentName)
    }
}
