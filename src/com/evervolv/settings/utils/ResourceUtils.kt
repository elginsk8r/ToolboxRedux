/*
 * SPDX-FileCopyrightText: 2025 The LineageOS project
 * SPDX-License-Identifier: Apache-2.0
 */
package com.evervolv.settings.utils

import android.content.Context
import android.util.Log

object ResourceUtils {
    private const val TAG: String = "ResourceUtils"

    fun getLocalizedString(
        context: Context,
        name: String,
        format: String
    ): String =
        context.resources.getIdentifier(
            String.format(format, name.lowercase().replace(" ", "_")),
            "string",
            context.packageName
        ) .takeIf { it != 0 } ?.let {
            context.resources.getString(it)
        } ?: run {
            Log.e(TAG, "No resource found for $name")
            name
        }
}
