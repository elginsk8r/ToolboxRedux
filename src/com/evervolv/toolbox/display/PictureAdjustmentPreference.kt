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
import android.util.Range
import android.view.View
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.android.settings.R
import com.evervolv.settingslib.widget.CustomDialogPreferenceExt
import com.evervolv.settingslib.widget.IntervalSeekBar
import evervolv.hardware.HSIC
import evervolv.hardware.LiveDisplayManager
import kotlin.math.roundToInt

class PictureAdjustmentPreference(context: Context, attrs: AttributeSet?) :
    CustomDialogPreferenceExt(context, attrs) {

    private val liveDisplay = LiveDisplayManager.getInstance(context)
    private val ranges = liveDisplay.config.pictureAdjustmentRanges

    private val currentAdj = FloatArray(5)
    private val originalAdj = FloatArray(5)

    private val seekBars = Array<ColorSeekBar?>(SEEKBAR_IDS.size) { null }

    init {
        dialogLayoutResource = com.evervolv.toolbox.R.layout.display_picture_adjustment
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

        System.arraycopy(liveDisplay.pictureAdjustment.toFloatArray(), 0, originalAdj, 0, 5)
        System.arraycopy(originalAdj, 0, currentAdj, 0, 5)

        for (i in SEEKBAR_IDS.indices) {
            val seekBar = view.findViewById<IntervalSeekBar>(SEEKBAR_IDS[i])
            val value = view.findViewById<TextView>(SEEKBAR_VALUE_IDS[i])
            val range = ranges[i]
            seekBars[i] = ColorSeekBar(seekBar, range, value, i)
        }

        updateBars()
    }

    private fun updateBars() {
        for (i in seekBars.indices) {
            seekBars[i]?.setValue(currentAdj[i])
        }
    }

    override fun onDismissDialog(dialog: DialogInterface, which: Int): Boolean {
        return if (which == DialogInterface.BUTTON_NEUTRAL) {
            System.arraycopy(
                liveDisplay.defaultPictureAdjustment.toFloatArray(), 0, currentAdj, 0, 5
            )
            updateBars()
            updateAdjustment(currentAdj)
            false
        } else {
            true
        }
    }

    override fun onDialogClosed(positiveResult: Boolean) {
        super.onDialogClosed(positiveResult)
        updateAdjustment(if (positiveResult) currentAdj else originalAdj)
    }

    override fun onSaveInstanceState(): Parcelable? {
        val superState = super.onSaveInstanceState()
        if (dialog == null || !dialog!!.isShowing) {
            return superState
        }

        val savedState = SavedState(superState)
        savedState.currentAdj = currentAdj.copyOf()
        savedState.originalAdj = originalAdj.copyOf()

        updateAdjustment(originalAdj)
        return savedState
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state !is SavedState) {
            super.onRestoreInstanceState(state)
            return
        }

        super.onRestoreInstanceState(state.superState)
        System.arraycopy(state.originalAdj, 0, originalAdj, 0, 5)
        System.arraycopy(state.currentAdj, 0, currentAdj, 0, 5)

        updateBars()
        updateAdjustment(currentAdj)
    }

    private fun updateAdjustment(adjustment: FloatArray) {
        liveDisplay.setPictureAdjustment(HSIC.fromFloatArray(adjustment))
    }

    private inner class ColorSeekBar(
        private val seekBar: IntervalSeekBar,
        private val range: Range<Float>,
        private val valueView: TextView,
        private val index: Int
    ) : SeekBar.OnSeekBarChangeListener {

        init {
            seekBar.setMinimum(range.lower)
            seekBar.setMaximum(range.upper)
            seekBar.setOnSeekBarChangeListener(this)
        }

        override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
            val floatProgress = (seekBar as IntervalSeekBar).progressFloat
            if (fromUser) {
                currentAdj[index] = ranges[index].clamp(floatProgress)
                updateAdjustment(currentAdj)
            }
            valueView.text = getLabel(currentAdj[index])
        }

        override fun onStartTrackingTouch(seekBar: SeekBar) = Unit
        override fun onStopTrackingTouch(seekBar: SeekBar) = Unit

        private fun getLabel(value: Float): String {
            return if (range.upper == 1.0f) {
                "${(value * 100).roundToInt()}%"
            } else {
                "${value.roundToInt()}"
            }
        }

        fun setValue(value: Float) {
            seekBar.setProgressFloat(value)
            valueView.text = getLabel(value)
        }
    }

    private class SavedState : BaseSavedState {
        lateinit var originalAdj: FloatArray
        lateinit var currentAdj: FloatArray

        constructor(superState: Parcelable?) : super(superState)
        constructor(source: Parcel) : super(source) {
            originalAdj = source.createFloatArray() ?: FloatArray(5)
            currentAdj = source.createFloatArray() ?: FloatArray(5)
        }

        override fun writeToParcel(dest: Parcel, flags: Int) {
            super.writeToParcel(dest, flags)
            dest.writeFloatArray(originalAdj)
            dest.writeFloatArray(currentAdj)
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
            com.evervolv.toolbox.R.id.adj_hue_seekbar,
            com.evervolv.toolbox.R.id.adj_saturation_seekbar,
            com.evervolv.toolbox.R.id.adj_intensity_seekbar,
            com.evervolv.toolbox.R.id.adj_contrast_seekbar
        )

        private val SEEKBAR_VALUE_IDS = intArrayOf(
            com.evervolv.toolbox.R.id.adj_hue_value,
            com.evervolv.toolbox.R.id.adj_saturation_value,
            com.evervolv.toolbox.R.id.adj_intensity_value,
            com.evervolv.toolbox.R.id.adj_contrast_value
        )
    }
}
