/*
 * SPDX-FileCopyrightText: 2024 Evervolv
 * SPDX-License-Identifier: Apache-2.0
 */

package com.evervolv.toolbox.display

import android.content.Context
import android.content.DialogInterface
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.evervolv.settingslib.widget.CustomDialogPreferenceExt
import com.evervolv.settingslib.widget.IntervalSeekBar
import evervolv.hardware.LiveDisplayManager
import kotlin.math.roundToInt

/**
 * Special preference type that allows configuration of Color settings
 */
class DisplayColorPreference(context: Context, attrs: AttributeSet?) :
    CustomDialogPreferenceExt(context, attrs) {

    private val liveDisplay = LiveDisplayManager.getInstance(context)

    private val currentColors = FloatArray(3)
    private val originalColors = FloatArray(3)

    private val seekBars = Array<ColorSeekBar?>(SEEKBAR_IDS.size) { null }

    init {
        dialogLayoutResource = com.evervolv.toolbox.R.layout.display_color_calibration
    }

    override fun onPrepareDialogBuilder(
        builder: AlertDialog.Builder,
        listener: DialogInterface.OnClickListener?
    ) {
        super.onPrepareDialogBuilder(builder, listener)
        builder.setNeutralButton(com.evervolv.toolbox.R.string.live_display_reset, null)
        builder.setNegativeButton(android.R.string.cancel, null)
        builder.setPositiveButton(android.R.string.ok, null)
    }

    override fun onBindDialogView(view: View) {
        super.onBindDialogView(view)

        System.arraycopy(liveDisplay.colorAdjustment, 0, originalColors, 0, 3)
        System.arraycopy(originalColors, 0, currentColors, 0, 3)

        for (i in SEEKBAR_IDS.indices) {
            val seekBar = view.findViewById<IntervalSeekBar>(SEEKBAR_IDS[i])
            val value = view.findViewById<TextView>(SEEKBAR_VALUE_IDS[i])
            seekBars[i] = ColorSeekBar(seekBar, value, i)
        }

        updateBars()
    }

    private fun updateBars() {
        for (i in seekBars.indices) {
            seekBars[i]?.setValue(currentColors[i])
        }
    }

    override fun onDismissDialog(dialog: DialogInterface, which: Int): Boolean {
        return if (which == DialogInterface.BUTTON_NEUTRAL) {
            for (i in seekBars.indices) {
                currentColors[i] = 1.0f
            }
            updateBars()
            updateColors(currentColors)
            false
        } else {
            true
        }
    }

    override fun onDialogClosed(positiveResult: Boolean) {
        super.onDialogClosed(positiveResult)
        updateColors(if (positiveResult) currentColors else originalColors)
    }

    override fun onSaveInstanceState(): Parcelable? {
        val superState = super.onSaveInstanceState()
        if (dialog == null || !dialog!!.isShowing) {
            return superState
        }

        val savedState = SavedState(superState)
        savedState.currentColors = currentColors.copyOf()
        savedState.originalColors = originalColors.copyOf()

        updateColors(originalColors)
        return savedState
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state !is SavedState) {
            super.onRestoreInstanceState(state)
            return
        }

        super.onRestoreInstanceState(state.superState)
        System.arraycopy(state.originalColors, 0, originalColors, 0, 3)
        System.arraycopy(state.currentColors, 0, currentColors, 0, 3)

        updateBars()
        updateColors(currentColors)
    }

    private fun updateColors(adjustment: FloatArray) {
        liveDisplay.setColorAdjustment(adjustment)
    }

    private inner class ColorSeekBar(
        private val seekBar: IntervalSeekBar,
        private val valueView: TextView,
        private val index: Int
    ) : SeekBar.OnSeekBarChangeListener {

        init {
            seekBar.setMinimum(0.1f)
            seekBar.setMaximum(1.0f)
            seekBar.setOnSeekBarChangeListener(this)
        }

        override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
            val floatProgress = (seekBar as IntervalSeekBar).progressFloat
            if (fromUser) {
                currentColors[index] = if (floatProgress > 1.0f) 1.0f else floatProgress;
                updateColors(currentColors)
            }
            valueView.text = getLabel(currentColors[index])
        }

        override fun onStartTrackingTouch(seekBar: SeekBar) = Unit
        override fun onStopTrackingTouch(seekBar: SeekBar) = Unit

        private fun getLabel(value: Float): String = "${(value * 100).roundToInt()}%"

        fun setValue(value: Float) {
            seekBar.setProgressFloat(value)
            valueView.text = getLabel(value)
        }
    }

    private class SavedState : BaseSavedState {
        lateinit var originalColors: FloatArray
        lateinit var currentColors: FloatArray

        constructor(superState: Parcelable?) : super(superState)
        constructor(source: Parcel) : super(source) {
            originalColors = source.createFloatArray() ?: FloatArray(3)
            currentColors = source.createFloatArray() ?: FloatArray(3)
        }

        override fun writeToParcel(dest: Parcel, flags: Int) {
            super.writeToParcel(dest, flags)
            dest.writeFloatArray(originalColors)
            dest.writeFloatArray(currentColors)
        }

        companion object {
            @JvmField
            val CREATOR: Parcelable.Creator<SavedState> =
                object : Parcelable.Creator<SavedState> {
                    override fun createFromParcel(source: Parcel) = SavedState(source)
                    override fun newArray(size: Int) = arrayOfNulls<SavedState>(size)
                }
        }
    }

    companion object {
        private val SEEKBAR_IDS = intArrayOf(
            com.evervolv.toolbox.R.id.color_red_seekbar,
            com.evervolv.toolbox.R.id.color_green_seekbar,
            com.evervolv.toolbox.R.id.color_blue_seekbar
        )

        private val SEEKBAR_VALUE_IDS = intArrayOf(
            com.evervolv.toolbox.R.id.color_red_value,
            com.evervolv.toolbox.R.id.color_green_value,
            com.evervolv.toolbox.R.id.color_blue_value
        )
    }
}
