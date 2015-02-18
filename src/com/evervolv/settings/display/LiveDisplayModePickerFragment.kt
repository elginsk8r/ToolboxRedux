/*
 * SPDX-FileCopyrightText: 2024 Evervolv
 * SPDX-License-Identifier: Apache-2.0
 */

package com.evervolv.settings.display

import android.app.settings.SettingsEnums
import android.content.Context
import android.hardware.display.ColorDisplayManager
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.preference.Preference
import androidx.preference.PreferenceScreen
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.android.settings.R
import com.android.settings.SettingsPreferenceFragment
import com.android.settingslib.widget.LayoutPreference
import com.android.settingslib.widget.SelectorWithWidgetPreference
import evervolv.hardware.LiveDisplayManager
import evervolv.hardware.LiveDisplayManager.MODE_AUTO
import evervolv.hardware.LiveDisplayManager.MODE_DAY
import evervolv.hardware.LiveDisplayManager.MODE_NIGHT
import evervolv.hardware.LiveDisplayManager.MODE_OFF
import evervolv.hardware.LiveDisplayManager.MODE_OUTDOOR

class LiveDisplayModePickerFragment : SettingsPreferenceFragment(),
    SelectorWithWidgetPreference.OnClickListener {

    companion object {
        private const val MODE_KEY = "live_display_mode_"
        private const val PAGE_VIEWER_SELECTION_INDEX = "page_viewer_selection_index"
        private const val DOT_INDICATOR_SIZE = 12
        private const val DOT_INDICATOR_LEFT_PADDING = 6
        private const val DOT_INDICATOR_RIGHT_PADDING = 6
    }

    data class LiveDisplayMode(
        var entry: String,
        var value: String,
        var summary: String
    )

    private lateinit var manager: LiveDisplayManager
    private lateinit var modes: MutableList<LiveDisplayMode>
    private lateinit var arrowPrevious: View
    private lateinit var arrowNext: View
    private lateinit var viewPager: ViewPager
    private lateinit var pageList: ArrayList<View>
    private lateinit var indicators: Array<ImageView>
    private lateinit var images: Array<View>

    override fun getMetricsCategory() = -1

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        super.onCreatePreferences(savedInstanceState, rootKey)
        addPreferencesFromResource(R.xml.live_display_mode_settings)

        val screen = preferenceScreen
        screen.removeAll()

        val preview = LayoutPreference(
            screen.context,
            R.layout.color_mode_preview
        )
        preview.isSelectable = false
        screen.addPreference(preview)
        addViewPager(preview)

        manager = LiveDisplayManager.getInstance(screen.context)

        modes = screen.context.resources.run {
            val entries = getStringArray(com.evervolv.platform.internal.R.array.live_display_entries)
            val values = getStringArray(com.evervolv.platform.internal.R.array.live_display_values)
            val summaries = getStringArray(com.evervolv.platform.internal.R.array.live_display_summaries)

            entries.indices.map { i ->
                LiveDisplayMode(entries[i], values[i], summaries[i])
            }.toMutableList() // Convert to a mutable list for modification
        }

        // List of indices to remove
        val removeIndices = mutableListOf<Int>()

        // Remove outdoor mode from lists if there is no support
        val nightDisplay = ColorDisplayManager.isNightDisplayAvailable(screen.context)
        if (!manager.config.hasFeature(MODE_OUTDOOR)) {
            removeIndices.add(modes.indexOfFirst { it.value == MODE_OUTDOOR.toString() })
        } else if (nightDisplay) {
            // Update the summary for the auto mode if outdoor mode is supported
            val autoIdx = modes.indexOfFirst { it.value == MODE_AUTO.toString() }
            if (autoIdx >= 0) {
                modes[autoIdx].summary = screen.context.resources.getString(R.string.live_display_outdoor_mode_summary)
            }
        }

        // Remove night display on HWC2
        if (nightDisplay) {
            removeIndices.add(modes.indexOfFirst { it.value == MODE_DAY.toString() })
            removeIndices.add(modes.indexOfFirst { it.value == MODE_NIGHT.toString() })
        }

        // Remove the items at the specified indices
        removeIndices.filter { it >= 0 }.sortedDescending().forEach { index ->
            modes.removeAt(index)
        }

        for (mode in modes) {
            val pref = SelectorWithWidgetPreference(screen.context)
            bindPreference(pref, mode)
            screen.addPreference(pref)
        }
        mayCheckOnlyRadioButton()

        savedInstanceState?.let {
            val selectedPosition = it.getInt(PAGE_VIEWER_SELECTION_INDEX)
            viewPager.currentItem = selectedPosition
            updateIndicator(selectedPosition)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(PAGE_VIEWER_SELECTION_INDEX, viewPager.currentItem)
    }

    private fun addViewPager(preview: LayoutPreference) {
        val viewPagerList = listOf(
            R.layout.color_mode_view1,
            R.layout.color_mode_view2,
            R.layout.color_mode_view3
        )

        viewPager = preview.findViewById(R.id.viewpager)

        images = Array(3) { idx ->
            layoutInflater.inflate(viewPagerList[idx], null)
        }

        pageList = arrayListOf(
            images[0],
            images[1],
            images[2]
        )

        viewPager.adapter = ColorPagerAdapter(pageList)

        arrowPrevious = preview.findViewById(R.id.arrow_previous)
        arrowPrevious.setOnClickListener {
            val previousPos = viewPager.currentItem - 1
            viewPager.setCurrentItem(previousPos, true)
        }

        arrowNext = preview.findViewById(R.id.arrow_next)
        arrowNext.setOnClickListener {
            val nextPos = viewPager.currentItem + 1
            viewPager.setCurrentItem(nextPos, true)
        }

        viewPager.addOnPageChangeListener(createPageListener())

        val viewGroup = preview.findViewById<ViewGroup>(R.id.viewGroup)
        indicators = Array(pageList.size) { _ ->
            val imageView = ImageView(context)
            val lp = ViewGroup.MarginLayoutParams(DOT_INDICATOR_SIZE, DOT_INDICATOR_SIZE)
            lp.setMargins(DOT_INDICATOR_LEFT_PADDING, 0, DOT_INDICATOR_RIGHT_PADDING, 0)
            imageView.layoutParams = lp
            viewGroup.addView(imageView)
            imageView
        }

        updateIndicator(viewPager.currentItem)
    }

    override fun onRadioButtonClicked(selected: SelectorWithWidgetPreference) {
        if (!selected.key.startsWith(MODE_KEY)) return
        val mode = modes.find { 
            it.value == selected.key.replaceFirst(MODE_KEY, "")
        } ?: return
        manager.setMode(mode.value.toInt())
        updateCheckedState(selected.key)
    }

    private fun bindPreference(pref: SelectorWithWidgetPreference, mode: LiveDisplayMode): SelectorWithWidgetPreference {
        pref.title = mode.entry
        pref.key = MODE_KEY + mode.value
        pref.isChecked = mode.value.toInt() == manager.mode
        pref.setOnClickListener(this)
        return pref
    }

    private fun updateCheckedState(selectedKey: String) {
        val screen = preferenceScreen
        screen?.let {
            for (i in 0 until it.preferenceCount) {
                val pref = it.getPreference(i)
                if (pref is SelectorWithWidgetPreference) {
                    val newCheckedState = TextUtils.equals(pref.key, selectedKey)
                    if (pref.isChecked != newCheckedState) {
                        pref.isChecked = newCheckedState
                    }
                }
            }
        }
    }

    private fun mayCheckOnlyRadioButton() {
        val screen = preferenceScreen
        screen?.let {
            if (it.preferenceCount == 1) {
                val onlyPref = it.getPreference(0)
                if (onlyPref is SelectorWithWidgetPreference) {
                    onlyPref.isChecked = true
                }
            }
        }
    }

    private fun createPageListener(): ViewPager.OnPageChangeListener {
        return object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                if (positionOffset != 0f) {
                    images.forEach { it.visibility = View.VISIBLE }
                } else {
                    images[position].contentDescription =
                        getString(R.string.colors_viewpager_content_description)
                    updateIndicator(position)
                }
            }

            override fun onPageSelected(position: Int) {}

            override fun onPageScrollStateChanged(state: Int) {}
        }
    }

    private fun updateIndicator(position: Int) {
        indicators.forEachIndexed { i, indicator ->
            if (position == i) {
                indicator.setBackgroundResource(R.drawable.ic_color_page_indicator_focused)
                images[i].visibility = View.VISIBLE
            } else {
                indicator.setBackgroundResource(R.drawable.ic_color_page_indicator_unfocused)
                images[i].visibility = View.INVISIBLE
            }
        }

        arrowPrevious.visibility = if (position == 0) View.INVISIBLE else View.VISIBLE
        arrowNext.visibility = if (position == pageList.size - 1) View.INVISIBLE else View.VISIBLE
    }

    class ColorPagerAdapter(private val pageViewList: List<View>) : PagerAdapter() {
        override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
            container.removeView(pageViewList[position])
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            container.addView(pageViewList[position])
            return pageViewList[position]
        }

        override fun getCount(): Int = pageViewList.size

        override fun isViewFromObject(view: View, obj: Any): Boolean = view === obj
    }
}
