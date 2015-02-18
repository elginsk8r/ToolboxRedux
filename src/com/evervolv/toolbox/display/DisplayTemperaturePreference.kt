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
import com.evervolv.internal.util.MathUtils
import evervolv.hardware.LiveDisplayConfig
import evervolv.hardware.LiveDisplayManager
import kotlin.math.max
import kotlin.math.roundToInt

class DisplayTemperaturePreference(
    context: Context,
    attrs: AttributeSet?
) : CustomDialogPreferenceExt(context, attrs) {

    companion object {
        private const val STEP = 100
    }

    private val liveDisplay = LiveDisplayManager.getInstance(context)
    private val config: LiveDisplayConfig = liveDisplay.config

    private lateinit var dayTemperature: ColorTemperatureSeekBar
    private lateinit var nightTemperature: ColorTemperatureSeekBar

    private var originalDayTemperature: Int = 0
    private var originalNightTemperature: Int = 0

    init {
        dialogLayoutResource = com.evervolv.toolbox.R.layout.display_temperature
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

        originalDayTemperature = liveDisplay.dayColorTemperature
        originalNightTemperature = liveDisplay.nightColorTemperature

        dayTemperature = ColorTemperatureSeekBar(
            view.findViewById(com.evervolv.toolbox.R.id.day_temperature_seekbar),
            view.findViewById(com.evervolv.toolbox.R.id.day_temperature_value)
        )
        nightTemperature = ColorTemperatureSeekBar(
            view.findViewById(com.evervolv.toolbox.R.id.night_temperature_seekbar),
            view.findViewById(com.evervolv.toolbox.R.id.night_temperature_value)
        )

        dayTemperature.setTemperature(originalDayTemperature)
        nightTemperature.setTemperature(originalNightTemperature)
    }

    override fun onDismissDialog(dialog: DialogInterface, which: Int): Boolean {
        return if (which == DialogInterface.BUTTON_NEUTRAL) {
            dayTemperature.setTemperature(config.defaultDayTemperature)
            nightTemperature.setTemperature(config.defaultNightTemperature)
            updateTemperature(true)
            false
        } else {
            true
        }
    }

    override fun onDialogClosed(positiveResult: Boolean) {
        super.onDialogClosed(positiveResult)
        updateTemperature(positiveResult)
    }

    override fun onSaveInstanceState(): Parcelable? {
        val superState = super.onSaveInstanceState()
        if (dialog == null || !dialog!!.isShowing) return superState

        return SavedState(superState).apply {
            originalDayTemperature = this@DisplayTemperaturePreference.originalDayTemperature
            originalNightTemperature = this@DisplayTemperaturePreference.originalNightTemperature
            currentDayTemperature = dayTemperature.getTemperature()
            currentNightTemperature = nightTemperature.getTemperature()
        }.also {
            updateTemperature(false)
        }
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state !is SavedState) {
            super.onRestoreInstanceState(state)
            return
        }

        super.onRestoreInstanceState(state.superState)

        originalDayTemperature = state.originalDayTemperature
        originalNightTemperature = state.originalNightTemperature
        dayTemperature.setTemperature(state.currentDayTemperature)
        nightTemperature.setTemperature(state.currentNightTemperature)

        updateTemperature(true)
    }

    private fun updateTemperature(accept: Boolean) {
        val day = if (accept) dayTemperature.getTemperature() else originalDayTemperature
        val night = if (accept) nightTemperature.getTemperature() else originalNightTemperature
        callChangeListener(arrayOf(day, night))
        liveDisplay.setDayColorTemperature(day)
        liveDisplay.setNightColorTemperature(night)
    }

    private fun roundUp(value: Int): Int {
        return ((value + STEP / 2) / STEP) * STEP
    }

    private inner class ColorTemperatureSeekBar(
        private val seekBar: SeekBar,
        private val valueView: TextView
    ) : SeekBar.OnSeekBarChangeListener {

        private val min = config.colorTemperatureRange.lower
        private val max = config.colorTemperatureRange.upper
        private val balanceMin = config.colorBalanceRange.lower
        private val balanceMax = config.colorBalanceRange.upper
        private val useBalance = config.hasFeature(LiveDisplayManager.FEATURE_COLOR_BALANCE) &&
            (balanceMin != 0 || balanceMax != 0)

        private val balanceCurve: DoubleArray? =
            if (useBalance) {
                MathUtils.powerCurve(
                    min as Double,
                    config.defaultDayTemperature as Double,
                    max as Double
                )
            } else {
                null
            }

        private val barMax = if (useBalance) {
            balanceMax - balanceMin
        } else {
            (max - min) / STEP
        }

        init {
            seekBar.max = barMax
            seekBar.setOnSeekBarChangeListener(this)
            onProgressChanged(seekBar, seekBar.progress, false)
        }

        override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
            if (fromUser) {
                updateTemperature(true)
            }

            val displayValue = if (useBalance) {
                roundUp(
                    MathUtils.linearToPowerCurve(
                        balanceCurve, progress.toDouble() / barMax
                    ).toFloat().roundToInt()
                )
            } else {
                progress * STEP + min
            }

            valueView.text = context.getString(
                com.evervolv.toolbox.R.string.live_display_color_temperature_label,
                displayValue
            )
        }

        fun setTemperature(temperature: Int) {
            if (useBalance) {
                val z = MathUtils.powerCurveToLinear(balanceCurve, temperature.toDouble())
                seekBar.progress = (z * barMax).toFloat().roundToInt()
            } else {
                val p = max(temperature, min) - min
                seekBar.progress = (p / STEP.toFloat()).roundToInt()
            }
        }

        fun getTemperature(): Int {
            return if (useBalance) {
                MathUtils.linearToPowerCurve(
                    balanceCurve, seekBar.progress.toDouble() / barMax
                ).toFloat().roundToInt()
            } else {
                seekBar.progress * STEP + min
            }
        }

        override fun onStartTrackingTouch(seekBar: SeekBar) {}
        override fun onStopTrackingTouch(seekBar: SeekBar) {}
    }

    private class SavedState : BaseSavedState {
        var originalDayTemperature: Int = 0
        var originalNightTemperature: Int = 0
        var currentDayTemperature: Int = 0
        var currentNightTemperature: Int = 0

        constructor(superState: Parcelable?) : super(superState)
        constructor(source: Parcel) : super(source) {
            originalDayTemperature = source.readInt()
            originalNightTemperature = source.readInt()
            currentDayTemperature = source.readInt()
            currentNightTemperature = source.readInt()
        }

        override fun writeToParcel(dest: Parcel, flags: Int) {
            super.writeToParcel(dest, flags)
            dest.writeInt(originalDayTemperature)
            dest.writeInt(originalNightTemperature)
            dest.writeInt(currentDayTemperature)
            dest.writeInt(currentNightTemperature)
        }

        companion object {
            @JvmField
            val CREATOR: Parcelable.Creator<SavedState> =
                object : Parcelable.Creator<SavedState> {
                    override fun createFromParcel(source: Parcel) = SavedState(source)
                    override fun newArray(size: Int): Array<SavedState?> = arrayOfNulls(size)
                }
        }
    }
}
