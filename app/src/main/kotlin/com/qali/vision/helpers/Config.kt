package com.qali.vision.helpers

import android.content.Context
import org.fossify.commons.helpers.BaseConfig
import com.qali.vision.R

class Config(context: Context) : BaseConfig(context) {
    companion object {
        fun newInstance(context: Context) = Config(context)
    }

    var wasHomeScreenInit: Boolean
        get() = prefs.getBoolean(WAS_HOME_SCREEN_INIT, false)
        set(wasHomeScreenInit) = prefs.edit().putBoolean(WAS_HOME_SCREEN_INIT, wasHomeScreenInit).apply()

    var homeColumnCount: Int
        get() = prefs.getInt(HOME_COLUMN_COUNT, COLUMN_COUNT)
        set(homeColumnCount) = prefs.edit().putInt(HOME_COLUMN_COUNT, homeColumnCount).apply()

    var homeRowCount: Int
        get() = prefs.getInt(HOME_ROW_COUNT, ROW_COUNT)
        set(homeRowCount) = prefs.edit().putInt(HOME_ROW_COUNT, homeRowCount).apply()

    var drawerColumnCount: Int
        get() = prefs.getInt(DRAWER_COLUMN_COUNT, context.resources.getInteger(R.integer.portrait_column_count))
        set(drawerColumnCount) = prefs.edit().putInt(DRAWER_COLUMN_COUNT, drawerColumnCount).apply()

    var showSearchBar: Boolean
        get() = prefs.getBoolean(SHOW_SEARCH_BAR, true)
        set(showSearchBar) = prefs.edit().putBoolean(SHOW_SEARCH_BAR, showSearchBar).apply()

    var closeAppDrawer: Boolean
        get() = prefs.getBoolean(CLOSE_APP_DRAWER, false)
        set(closeAppDrawer) = prefs.edit().putBoolean(CLOSE_APP_DRAWER, closeAppDrawer).apply()

    var autoShowKeyboardInAppDrawer: Boolean
        get() = prefs.getBoolean(AUTO_SHOW_KEYBOARD_IN_APP_DRAWER, false)
        set(autoShowKeyboardInAppDrawer) = prefs.edit()
            .putBoolean(AUTO_SHOW_KEYBOARD_IN_APP_DRAWER, autoShowKeyboardInAppDrawer).apply()

    // Eye Control Settings
    var eyeControlEnabled: Boolean
        get() = prefs.getBoolean(EYE_CONTROL_ENABLED, false)
        set(eyeControlEnabled) = prefs.edit().putBoolean(EYE_CONTROL_ENABLED, eyeControlEnabled).apply()

    var eyeControlSensitivity: Float
        get() = prefs.getFloat(EYE_CONTROL_SENSITIVITY, 1.0f)
        set(eyeControlSensitivity) = prefs.edit().putFloat(EYE_CONTROL_SENSITIVITY, eyeControlSensitivity).apply()

    var eyeControlSmoothing: Float
        get() = prefs.getFloat(EYE_CONTROL_SMOOTHING, 0.5f)
        set(eyeControlSmoothing) = prefs.edit().putFloat(EYE_CONTROL_SMOOTHING, eyeControlSmoothing).apply()

    var halfBlinkClickThreshold: Float
        get() = prefs.getFloat(HALF_BLINK_CLICK_THRESHOLD, 0.3f)
        set(halfBlinkClickThreshold) = prefs.edit().putFloat(HALF_BLINK_CLICK_THRESHOLD, halfBlinkClickThreshold).apply()
        // Range: 0.05 to 1.0 (increased from default 0.3)

    var halfBlinkDragThreshold: Float
        get() = prefs.getFloat(HALF_BLINK_DRAG_THRESHOLD, 0.4f)
        set(halfBlinkDragThreshold) = prefs.edit().putFloat(HALF_BLINK_DRAG_THRESHOLD, halfBlinkDragThreshold).apply()
        // Range: 0.05 to 1.0 (increased from default 0.4)

    var cursorSize: Int
        get() = prefs.getInt(CURSOR_SIZE, 20)
        set(cursorSize) = prefs.edit().putInt(CURSOR_SIZE, cursorSize).apply()

    var cursorColor: Int
        get() = prefs.getInt(CURSOR_COLOR, android.graphics.Color.RED)
        set(cursorColor) = prefs.edit().putInt(CURSOR_COLOR, cursorColor).apply()

    var showCursor: Boolean
        get() = prefs.getBoolean(SHOW_CURSOR, true)
        set(showCursor) = prefs.edit().putBoolean(SHOW_CURSOR, showCursor).apply()
}
