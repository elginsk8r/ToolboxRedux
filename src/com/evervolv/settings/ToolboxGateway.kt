/*
 * SPDX-FileCopyrightText: 2024 Evervolv
 * SPDX-License-Identifier: Apache-2.0
 */

package com.evervolv.settings

import com.evervolv.settings.gestures.PowerMenuActions

/**
 * A singleton object that contains a list of fragments that can be hosted by ToolboxActivity.
 * SettingsActivity will throw a security exception if the fragment it needs to display is not in this list.
 */
object ToolboxGateway {
    val ENTRY_FRAGMENTS = arrayOf(
        PowerMenuActions::class.java.name
    )
}
