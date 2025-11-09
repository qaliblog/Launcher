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

    var showEyeLine: Boolean
        get() = prefs.getBoolean(SHOW_EYE_LINE, true)
        set(showEyeLine) = prefs.edit().putBoolean(SHOW_EYE_LINE, showEyeLine).apply()

    var eyeLineColor: Int
        get() = prefs.getInt(EYE_LINE_COLOR, android.graphics.Color.BLUE)
        set(eyeLineColor) = prefs.edit().putInt(EYE_LINE_COLOR, eyeLineColor).apply()

    var eyeLineLength: Int
        get() = prefs.getInt(EYE_LINE_LENGTH, 200)
        set(eyeLineLength) = prefs.edit().putInt(EYE_LINE_LENGTH, eyeLineLength).apply()

    var debugLogging: Boolean
        get() = prefs.getBoolean(DEBUG_LOGGING, false)
        set(debugLogging) = prefs.edit().putBoolean(DEBUG_LOGGING, debugLogging).apply()

    var gazeSmoothing: Float
        get() = prefs.getFloat(GAZE_SMOOTHING, 0.7f)
        set(gazeSmoothing) = prefs.edit().putFloat(GAZE_SMOOTHING, gazeSmoothing).apply()

    var blinkDurationThreshold: Int
        get() = prefs.getInt(BLINK_DURATION_THRESHOLD, 300)
        set(blinkDurationThreshold) = prefs.edit().putInt(BLINK_DURATION_THRESHOLD, blinkDurationThreshold).apply()

    // Movement multipliers
    var xMovementMultiplier: Float
        get() = prefs.getFloat(X_MOVEMENT_MULTIPLIER, 1.0f)
        set(xMovementMultiplier) = prefs.edit().putFloat(X_MOVEMENT_MULTIPLIER, xMovementMultiplier).apply()

    var yMovementMultiplier: Float
        get() = prefs.getFloat(Y_MOVEMENT_MULTIPLIER, 1.0f)
        set(yMovementMultiplier) = prefs.edit().putFloat(Y_MOVEMENT_MULTIPLIER, yMovementMultiplier).apply()

    // Eye position effects
    var eyePositionXEffect: Float
        get() = prefs.getFloat(EYE_POSITION_X_EFFECT, 0f)
        set(eyePositionXEffect) = prefs.edit().putFloat(EYE_POSITION_X_EFFECT, eyePositionXEffect).apply()

    var eyePositionXMultiplier: Float
        get() = prefs.getFloat(EYE_POSITION_X_MULTIPLIER, 1.0f)
        set(eyePositionXMultiplier) = prefs.edit().putFloat(EYE_POSITION_X_MULTIPLIER, eyePositionXMultiplier).apply()

    var eyePositionYEffect: Float
        get() = prefs.getFloat(EYE_POSITION_Y_EFFECT, 0f)
        set(eyePositionYEffect) = prefs.edit().putFloat(EYE_POSITION_Y_EFFECT, eyePositionYEffect).apply()

    var eyePositionYMultiplier: Float
        get() = prefs.getFloat(EYE_POSITION_Y_MULTIPLIER, 1.0f)
        set(eyePositionYMultiplier) = prefs.edit().putFloat(EYE_POSITION_Y_MULTIPLIER, eyePositionYMultiplier).apply()

    // Distance multipliers
    var distanceXMultiplier: Float
        get() = prefs.getFloat(DISTANCE_X_MULTIPLIER, 0f)
        set(distanceXMultiplier) = prefs.edit().putFloat(DISTANCE_X_MULTIPLIER, distanceXMultiplier).apply()

    var distanceYMultiplier: Float
        get() = prefs.getFloat(DISTANCE_Y_MULTIPLIER, 0f)
        set(distanceYMultiplier) = prefs.edit().putFloat(DISTANCE_Y_MULTIPLIER, distanceYMultiplier).apply()

    // Blink detection - WIDER RANGES than iris app
    var blinkThreshold: Float
        get() = prefs.getFloat(BLINK_THRESHOLD, 0.3f)
        set(blinkThreshold) = prefs.edit().putFloat(BLINK_THRESHOLD, blinkThreshold.coerceIn(0.01f, 1.0f)).apply()
        // Range: 0.01 to 1.0 (iris app: 0.05-0.8)

    var useOneEyeDetection: Boolean
        get() = prefs.getBoolean(USE_ONE_EYE, false)
        set(useOneEyeDetection) = prefs.edit().putBoolean(USE_ONE_EYE, useOneEyeDetection).apply()

    var halfBlinkAccelThreshold: Float
        get() = prefs.getFloat(HALF_BLINK_ACCEL_THRESHOLD, 0.15f)
        set(halfBlinkAccelThreshold) = prefs.edit().putFloat(HALF_BLINK_ACCEL_THRESHOLD, halfBlinkAccelThreshold.coerceIn(0.01f, 1.0f)).apply()
        // Range: 0.01 to 1.0 (iris app: 0.05-0.5)

    var clickDelayThreshold: Long
        get() = prefs.getLong(CLICK_DELAY_THRESHOLD, 200L)
        set(clickDelayThreshold) = prefs.edit().putLong(CLICK_DELAY_THRESHOLD, clickDelayThreshold.coerceIn(0L, 1000L)).apply()

    // Cursor update settings
    var cursorSmoothingFactor: Float
        get() = prefs.getFloat(CURSOR_SMOOTHING, 0.7f)
        set(cursorSmoothingFactor) = prefs.edit().putFloat(CURSOR_SMOOTHING, cursorSmoothingFactor.coerceIn(0f, 1f)).apply()

    var cursorUpdateInterval: Long
        get() = prefs.getLong(CURSOR_UPDATE_INTERVAL, 16L)
        set(cursorUpdateInterval) = prefs.edit().putLong(CURSOR_UPDATE_INTERVAL, cursorUpdateInterval.coerceIn(8L, 100L)).apply()

    var cursorMovementDuration: Long
        get() = prefs.getLong(CURSOR_MOVEMENT_DURATION, 100L)
        set(cursorMovementDuration) = prefs.edit().putLong(CURSOR_MOVEMENT_DURATION, cursorMovementDuration.coerceIn(50L, 300L)).apply()

    // Colors
    var clickColor: Int
        get() = prefs.getInt(CLICK_COLOR, android.graphics.Color.GREEN)
        set(clickColor) = prefs.edit().putInt(CLICK_COLOR, clickColor).apply()

    var dragColor: Int
        get() = prefs.getInt(DRAG_COLOR, android.graphics.Color.parseColor("#9C27B0"))
        set(dragColor) = prefs.edit().putInt(DRAG_COLOR, dragColor).apply()

    // Camera preview
    var showLivePreview: Boolean
        get() = prefs.getBoolean(SHOW_LIVE_PREVIEW, false)
        set(showLivePreview) = prefs.edit().putBoolean(SHOW_LIVE_PREVIEW, showLivePreview).apply()

    var screenOffTracking: Boolean
        get() = prefs.getBoolean(SCREEN_OFF_TRACKING, true)
        set(screenOffTracking) = prefs.edit().putBoolean(SCREEN_OFF_TRACKING, screenOffTracking).apply()
}
