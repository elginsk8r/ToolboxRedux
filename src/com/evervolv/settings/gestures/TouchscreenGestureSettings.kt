/*
 * SPDX-FileCopyrightText: 2024 Evervolv
 * SPDX-License-Identifier: Apache-2.0
 */

package com.evervolv.settings.gestures

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import com.android.settings.SettingsPreferenceFragment
import com.evervolv.internal.util.ResourceUtils
import com.evervolv.settings.gestures.TouchscreenGestureConstants.ACTION_CAMERA
import com.evervolv.settings.gestures.TouchscreenGestureConstants.ACTION_FLASHLIGHT
import com.evervolv.settings.gestures.TouchscreenGestureConstants.ACTION_BROWSER
import com.evervolv.settings.gestures.TouchscreenGestureConstants.ACTION_DIALER
import com.evervolv.settings.gestures.TouchscreenGestureConstants.ACTION_EMAIL
import com.evervolv.settings.gestures.TouchscreenGestureConstants.ACTION_MESSAGES
import com.evervolv.settings.gestures.TouchscreenGestureConstants.ACTION_PLAY_PAUSE_MUSIC
import com.evervolv.settings.gestures.TouchscreenGestureConstants.ACTION_PREVIOUS_TRACK
import com.evervolv.settings.gestures.TouchscreenGestureConstants.ACTION_NEXT_TRACK
import com.evervolv.settings.gestures.TouchscreenGestureConstants.ACTION_VOLUME_DOWN
import com.evervolv.settings.gestures.TouchscreenGestureConstants.ACTION_VOLUME_UP
import com.evervolv.settings.gestures.TouchscreenGestureConstants.KEY_TOUCHSCREEN_GESTURE_HAPTIC_FEEDBACK
import com.evervolv.settings.gestures.TouchscreenGestureConstants.TOUCHSCREEN_GESTURE_TITLE
import evervolv.provider.EVSettings
import vendor.evervolv.touch.V1_0.Gesture
import vendor.evervolv.touch.V1_0.ITouchscreenGesture

class TouchscreenGestureSettings : SettingsPreferenceFragment(), Preference.OnPreferenceChangeListener {

    private var hapticFeedbackPreference: SwitchPreference? = null

    private val handler = Handler(Looper.getMainLooper())

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        addPreferencesFromResource(R.xml.gesture_settings)

        val service: ITouchscreenGesture = ITouchscreenGesture.getService(false)
        val actions: IntArray =
            TouchscreenGestureConstants.getDefaultGestureActions(
                requireContext(),
                service.supportedGestures
            )
        for (gesture in service.supportedGestures) {
            preferenceScreen.addPreference(
                TouchscreenGesturePreference(
                    requireContext(), service, gesture, actions[gesture.id]
                )
            )
        }

        hapticFeedbackPreference = findPreference(KEY_TOUCHSCREEN_GESTURE_HAPTIC_FEEDBACK)
        hapticFeedbackPreference?.setChecked(
            EVSettings.System.getInt(
                requireContext().contentResolver,
                EVSettings.System.TOUCHSCREEN_GESTURE_HAPTIC_FEEDBACK,
                0
            ) == 1
        )
        hapticFeedbackPreference?.onPreferenceChangeListener = this
    }

    override fun getMetricsCategory() = EVMetricsLogger.DONT_LOG

    private class TouchscreenGesturePreference(
        private val context: Context,
        private val service: ITouchscreenGesture,
        private val gesture: Gesture,
        private val defaultAction: Int
    ) : ListPreference(context) {

        init {
            setKey(TouchscreenGestureConstants.buildPreferenceKey(gesture))
            setEntries(R.array.touchscreen_gesture_action_entries)
            setEntryValues(R.array.touchscreen_gesture_action_values)
            setDefaultValue(defaultAction.toString())
            setIcon(getIconDrawableResourceForAction(defaultAction))

            setSummary("%s")
            setDialogTitle(R.string.touchscreen_gesture_action_dialog_title)
            setTitle(
                ResourceUtils.getLocalizedString(
                    context.resources, gesture.name, TOUCHSCREEN_GESTURE_TITLE
                )
            )
        }

        override fun callChangeListener(newValue: Any): Boolean =
            if (!service.setGestureEnabled(gesture, newValue.toString().toInt() > 0)) {
                false
            } else {
                super.callChangeListener(newValue)
            }

        override fun persistString(value: String): Boolean =
            if (!super.persistString(value)) {
                false
            } else {
                setIcon(getIconDrawableResourceForAction(value.toInt()))
                TouchscreenGestureConstants.sendUpdateBroadcast(
                    context, service.supportedGestures
                )
                true
            }

        fun getIconDrawableResourceForAction(action: Int): Int =
            when (action) {
                ACTION_CAMERA -> R.drawable.ic_gesture_action_camera
                ACTION_FLASHLIGHT -> R.drawable.ic_gesture_action_flashlight
                ACTION_BROWSER -> R.drawable.ic_gesture_action_browser
                ACTION_DIALER -> R.drawable.ic_gesture_action_dialer
                ACTION_EMAIL -> R.drawable.ic_gesture_action_email
                ACTION_MESSAGES -> R.drawable.ic_gesture_action_messages
                ACTION_PLAY_PAUSE_MUSIC -> R.drawable.ic_gesture_action_play_pause
                ACTION_PREVIOUS_TRACK -> R.drawable.ic_gesture_action_previous_track
                ACTION_NEXT_TRACK -> R.drawable.ic_gesture_action_next_track
                ACTION_VOLUME_DOWN -> R.drawable.ic_gesture_action_volume_down
                ACTION_VOLUME_UP -> R.drawable.ic_gesture_action_volume_up
                else -> R.drawable.ic_gesture_action_none
            }
    }

    override fun onPreferenceChange(preference: Preference, newValue: Any?): Boolean =
        if (preference == hapticFeedbackPreference) {
            handler.post {
                val enabled: Boolean = (newValue as? Boolean) ?: false
                EVSettings.System.putInt(
                    requireContext().contentResolver,
                    EVSettings.System.TOUCHSCREEN_GESTURE_HAPTIC_FEEDBACK,
                    if (enabled) 0 else 1
                )
            }
            true
        } else {
            true
        }
}
