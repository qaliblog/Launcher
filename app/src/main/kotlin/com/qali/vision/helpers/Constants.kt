package com.qali.vision.helpers

const val WIDGET_LIST_SECTION = 0
const val WIDGET_LIST_ITEMS_HOLDER = 1

const val REPOSITORY_NAME = "Launcher"

// shared prefs
const val WAS_HOME_SCREEN_INIT = "was_home_screen_init"
const val HOME_ROW_COUNT = "home_row_count"
const val HOME_COLUMN_COUNT = "home_column_count"
const val DRAWER_COLUMN_COUNT = "drawer_column_count"
const val SHOW_SEARCH_BAR = "show_search_bar"
const val CLOSE_APP_DRAWER = "close_app_drawer"
const val AUTO_SHOW_KEYBOARD_IN_APP_DRAWER = "auto_show_keyboard_in_app_drawer"
const val EYE_CONTROL_ENABLED = "eye_control_enabled"
const val EYE_CONTROL_SENSITIVITY = "eye_control_sensitivity"
const val EYE_CONTROL_SMOOTHING = "eye_control_smoothing"
const val HALF_BLINK_CLICK_THRESHOLD = "half_blink_click_threshold"
const val HALF_BLINK_DRAG_THRESHOLD = "half_blink_drag_threshold"
const val CURSOR_SIZE = "cursor_size"
const val CURSOR_COLOR = "cursor_color"
const val SHOW_CURSOR = "show_cursor"
const val SHOW_EYE_LINE = "show_eye_line"
const val EYE_LINE_COLOR = "eye_line_color"
const val EYE_LINE_LENGTH = "eye_line_length"
const val DEBUG_LOGGING = "debug_logging"
const val GAZE_SMOOTHING = "gaze_smoothing"
const val BLINK_DURATION_THRESHOLD = "blink_duration_threshold"

// Movement multipliers
const val X_MOVEMENT_MULTIPLIER = "x_movement_multiplier"
const val Y_MOVEMENT_MULTIPLIER = "y_movement_multiplier"

// Eye position effects
const val EYE_POSITION_X_EFFECT = "eye_position_x_effect"
const val EYE_POSITION_X_MULTIPLIER = "eye_position_x_multiplier"
const val EYE_POSITION_Y_EFFECT = "eye_position_y_effect"
const val EYE_POSITION_Y_MULTIPLIER = "eye_position_y_multiplier"

// Distance multipliers
const val DISTANCE_X_MULTIPLIER = "distance_x_multiplier"
const val DISTANCE_Y_MULTIPLIER = "distance_y_multiplier"

// Blink detection (wider ranges than iris app)
const val BLINK_THRESHOLD = "blink_threshold" // Range: 0.01 to 1.0 (iris: 0.05-0.8)
const val USE_ONE_EYE = "use_one_eye"
const val HALF_BLINK_ACCEL_THRESHOLD = "half_blink_accel_threshold" // Range: 0.01 to 1.0 (iris: 0.05-0.5)
const val CLICK_DELAY_THRESHOLD = "click_delay_threshold"

// Cursor update settings
const val CURSOR_SMOOTHING = "cursor_smoothing"
const val CURSOR_UPDATE_INTERVAL = "cursor_update_interval"
const val CURSOR_MOVEMENT_DURATION = "cursor_movement_duration"

// Colors
const val CLICK_COLOR = "click_color"
const val DRAG_COLOR = "drag_color"

// Camera preview
const val SHOW_LIVE_PREVIEW = "show_live_preview"
const val SCREEN_OFF_TRACKING = "screen_off_tracking"

// default home screen grid size
const val ROW_COUNT = 6
const val COLUMN_COUNT = 5
const val MIN_ROW_COUNT = 2
const val MAX_ROW_COUNT = 15
const val MIN_COLUMN_COUNT = 2
const val MAX_COLUMN_COUNT = 15

const val UNINSTALL_APP_REQUEST_CODE = 50
const val REQUEST_CONFIGURE_WIDGET = 51
const val REQUEST_ALLOW_BINDING_WIDGET = 52
const val REQUEST_CREATE_SHORTCUT = 53
const val REQUEST_SET_DEFAULT = 54

const val ITEM_TYPE_ICON = 0
const val ITEM_TYPE_WIDGET = 1
const val ITEM_TYPE_SHORTCUT = 2
const val ITEM_TYPE_FOLDER = 3

const val WIDGET_HOST_ID = 12345
const val MAX_CLICK_DURATION = 150
