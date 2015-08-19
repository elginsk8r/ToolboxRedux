/*
 * SPDX-FileCopyrightText: 2024 Evervolv
 * SPDX-License-Identifier: Apache-2.0
 */

package com.evervolv.settings.display

import android.app.settings.SettingsEnums
import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
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
import com.evervolv.internal.util.ResourceUtils
import evervolv.hardware.DisplayMode
import evervolv.hardware.HardwareManager

class DisplayModePickerFragment : SettingsPreferenceFragment(),
    SelectorWithWidgetPreference.OnClickListener {

    companion object {
        const val COLOR_PROFILE_TITLE = "live_display_color_profile_%s_title"
        private const val COLOR_PROFILE = "color_profile_"
        private const val PAGE_VIEWER_SELECTION_INDEX = "page_viewer_selection_index"
        private const val DOT_INDICATOR_SIZE = 12
        private const val DOT_INDICATOR_LEFT_PADDING = 6
        private const val DOT_INDICATOR_RIGHT_PADDING = 6
    }

    private lateinit var manager: HardwareManager
    private lateinit var arrowPrevious: View
    private lateinit var arrowNext: View
    private lateinit var viewPager: ViewPager
    private lateinit var pageList: ArrayList<View>
    private lateinit var indicators: Array<ImageView>
    private lateinit var images: Array<View>

    override fun getMetricsCategory() = SettingsEnums.COLOR_MODE_SETTINGS

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        super.onCreatePreferences(savedInstanceState, rootKey)
        addPreferencesFromResource(R.xml.color_mode_settings)

        val screen = preferenceScreen
        screen.removeAll()

        val preview = LayoutPreference(
            screen.context,
            R.layout.color_mode_preview
        )
        preview.isSelectable = false
        screen.addPreference(preview)
        addViewPager(preview)

        manager = HardwareManager.getInstance(screen.context)

        val modes = manager.displayModes
        modes?.let {
            for (mode in modes) {
                val pref = SelectorWithWidgetPreference(screen.context)
                bindPreference(pref, mode)
                screen.addPreference(pref)
            }
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
        val selectedKey = selected.key
        if (selectedKey.startsWith(COLOR_PROFILE)) {
            val modeId = selectedKey.replaceFirst(COLOR_PROFILE, "").toInt()
            manager.displayModes?.let { modes ->
                for (mode in modes) {
                    if (mode.id == modeId) {
                        manager.setDisplayMode(mode, true)
                        updateCheckedState(selectedKey)
                    }
                }
            }
        }
    }

    private fun bindPreference(pref: SelectorWithWidgetPreference, mode: DisplayMode): SelectorWithWidgetPreference {
        val defaultMode = manager.currentDisplayMode ?: manager.defaultDisplayMode
        pref.title = ResourceUtils.getLocalizedString(
            resources, mode.name, COLOR_PROFILE_TITLE
        )
        pref.key = COLOR_PROFILE + mode.id
        pref.isChecked = mode.id == defaultMode.id
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
