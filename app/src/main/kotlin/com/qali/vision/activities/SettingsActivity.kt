package com.qali.vision.activities

import android.annotation.SuppressLint
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import org.fossify.commons.dialogs.RadioGroupDialog
import org.fossify.commons.extensions.beVisibleIf
import org.fossify.commons.extensions.getProperPrimaryColor
import org.fossify.commons.extensions.toast
import org.fossify.commons.extensions.updateTextColors
import org.fossify.commons.extensions.viewBinding
import org.fossify.commons.helpers.NavigationIcon
import org.fossify.commons.helpers.isTiramisuPlus
import org.fossify.commons.models.FAQItem
import org.fossify.commons.models.RadioItem
import com.qali.vision.BuildConfig
import com.qali.vision.R
import com.qali.vision.databinding.ActivitySettingsBinding
import com.qali.vision.extensions.config
import com.qali.vision.helpers.MAX_COLUMN_COUNT
import com.qali.vision.helpers.MAX_ROW_COUNT
import com.qali.vision.helpers.MIN_COLUMN_COUNT
import com.qali.vision.helpers.MIN_ROW_COUNT
import com.qali.vision.receivers.LockDeviceAdminReceiver
import java.util.Locale
import kotlin.system.exitProcess

class SettingsActivity : SimpleActivity() {

    private val binding by viewBinding(ActivitySettingsBinding::inflate)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupEdgeToEdge(padBottomSystem = listOf(binding.settingsNestedScrollview))
        setupMaterialScrollListener(binding.settingsNestedScrollview, binding.settingsAppbar)
        setupOptionsMenu()
    }

    override fun onResume() {
        super.onResume()
        setupTopAppBar(binding.settingsAppbar, NavigationIcon.Arrow)
        refreshMenuItems()

        setupCustomizeColors()
        setupUseEnglish()
        setupDoubleTapToLock()
        setupCloseAppDrawerOnOtherAppOpen()
        setupOpenKeyboardOnAppDrawer()
        setupDrawerColumnCount()
        setupDrawerSearchBar()
        setupHomeRowCount()
        setupHomeColumnCount()
        setupLanguage()
        setupManageHiddenIcons()
        setupEyeControlSettings()
        updateTextColors(binding.settingsHolder)

        arrayOf(
            binding.settingsColorCustomizationSectionLabel,
            binding.settingsGeneralSettingsLabel,
            binding.settingsDrawerSettingsLabel,
            binding.settingsHomeScreenLabel,
            binding.settingsEyeControlLabel
        ).forEach {
            it.setTextColor(getProperPrimaryColor())
        }
    }

    private fun setupOptionsMenu() {
        binding.settingsToolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.about -> launchAbout()
                else -> return@setOnMenuItemClickListener false
            }
            return@setOnMenuItemClickListener true
        }
    }

    private fun refreshMenuItems() {
        // Removed social links menu items
    }

    private fun setupCustomizeColors() {
        binding.settingsColorCustomizationHolder.setOnClickListener {
            startCustomizationActivity()
        }
    }

    private fun setupUseEnglish() {
        binding.settingsUseEnglishHolder.beVisibleIf(
            beVisible = (config.wasUseEnglishToggled || Locale.getDefault().language != "en")
                    && !isTiramisuPlus()
        )

        binding.settingsUseEnglish.isChecked = config.useEnglish
        binding.settingsUseEnglishHolder.setOnClickListener {
            binding.settingsUseEnglish.toggle()
            config.useEnglish = binding.settingsUseEnglish.isChecked
            exitProcess(0)
        }
    }

    private fun setupDoubleTapToLock() {
        val devicePolicyManager = getSystemService(DEVICE_POLICY_SERVICE) as DevicePolicyManager
        binding.settingsDoubleTapToLock.isChecked = devicePolicyManager.isAdminActive(
            ComponentName(this, LockDeviceAdminReceiver::class.java)
        )

        binding.settingsDoubleTapToLockHolder.setOnClickListener {
            val isLockDeviceAdminActive = devicePolicyManager.isAdminActive(
                ComponentName(this, LockDeviceAdminReceiver::class.java)
            )
            if (isLockDeviceAdminActive) {
                devicePolicyManager.removeActiveAdmin(
                    ComponentName(this, LockDeviceAdminReceiver::class.java)
                )
            } else {
                val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
                intent.putExtra(
                    DevicePolicyManager.EXTRA_DEVICE_ADMIN,
                    ComponentName(this, LockDeviceAdminReceiver::class.java)
                )
                intent.putExtra(
                    DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                    getString(R.string.lock_device_admin_hint)
                )
                startActivity(intent)
            }
        }
    }

    private fun setupOpenKeyboardOnAppDrawer() {
        binding.settingsOpenKeyboardOnAppDrawerHolder.beVisibleIf(config.showSearchBar)
        binding.settingsOpenKeyboardOnAppDrawer.isChecked = config.autoShowKeyboardInAppDrawer
        binding.settingsOpenKeyboardOnAppDrawerHolder.setOnClickListener {
            binding.settingsOpenKeyboardOnAppDrawer.toggle()
            config.autoShowKeyboardInAppDrawer = binding.settingsOpenKeyboardOnAppDrawer.isChecked
        }
    }

    private fun setupCloseAppDrawerOnOtherAppOpen() {
        binding.settingsCloseAppDrawerOnOtherApp.isChecked = config.closeAppDrawer
        binding.settingsCloseAppDrawerOnOtherAppHolder.setOnClickListener {
            binding.settingsCloseAppDrawerOnOtherApp.toggle()
            config.closeAppDrawer = binding.settingsCloseAppDrawerOnOtherApp.isChecked
        }
    }

    private fun setupDrawerColumnCount() {
        val currentColumnCount = config.drawerColumnCount
        binding.settingsDrawerColumnCount.text = currentColumnCount.toString()
        binding.settingsDrawerColumnCountHolder.setOnClickListener {
            val items = ArrayList<RadioItem>()
            for (i in 1..MAX_COLUMN_COUNT) {
                items.add(
                    RadioItem(
                        id = i,
                        title = resources.getQuantityString(
                            org.fossify.commons.R.plurals.column_counts, i, i
                        )
                    )
                )
            }

            RadioGroupDialog(this, items, currentColumnCount) {
                val newColumnCount = it as Int
                if (currentColumnCount != newColumnCount) {
                    config.drawerColumnCount = newColumnCount
                    setupDrawerColumnCount()
                }
            }
        }
    }

    private fun setupDrawerSearchBar() {
        val showSearchBar = config.showSearchBar
        binding.settingsShowSearchBar.isChecked = showSearchBar
        binding.settingsDrawerSearchHolder.setOnClickListener {
            binding.settingsShowSearchBar.toggle()
            config.showSearchBar = binding.settingsShowSearchBar.isChecked
            binding.settingsOpenKeyboardOnAppDrawerHolder.beVisibleIf(config.showSearchBar)
        }
    }

    private fun setupHomeRowCount() {
        val currentRowCount = config.homeRowCount
        binding.settingsHomeScreenRowCount.text = currentRowCount.toString()
        binding.settingsHomeScreenRowCountHolder.setOnClickListener {
            val items = ArrayList<RadioItem>()
            for (i in MIN_ROW_COUNT..MAX_ROW_COUNT) {
                items.add(
                    RadioItem(
                        id = i,
                        title = resources.getQuantityString(
                            org.fossify.commons.R.plurals.row_counts, i, i
                        )
                    )
                )
            }

            RadioGroupDialog(this, items, currentRowCount) {
                val newRowCount = it as Int
                if (currentRowCount != newRowCount) {
                    config.homeRowCount = newRowCount
                    setupHomeRowCount()
                }
            }
        }
    }

    private fun setupHomeColumnCount() {
        val currentColumnCount = config.homeColumnCount
        binding.settingsHomeScreenColumnCount.text = currentColumnCount.toString()
        binding.settingsHomeScreenColumnCountHolder.setOnClickListener {
            val items = ArrayList<RadioItem>()
            for (i in MIN_COLUMN_COUNT..MAX_COLUMN_COUNT) {
                items.add(
                    RadioItem(
                        id = i,
                        title = resources.getQuantityString(
                            org.fossify.commons.R.plurals.column_counts, i, i
                        )
                    )
                )
            }

            RadioGroupDialog(this, items, currentColumnCount) {
                val newColumnCount = it as Int
                if (currentColumnCount != newColumnCount) {
                    config.homeColumnCount = newColumnCount
                    setupHomeColumnCount()
                }
            }
        }
    }

    @SuppressLint("NewApi")
    private fun setupLanguage() {
        binding.settingsLanguage.text = Locale.getDefault().displayLanguage
        binding.settingsLanguageHolder.beVisibleIf(isTiramisuPlus())
        binding.settingsLanguageHolder.setOnClickListener {
            launchChangeAppLanguageIntent()
        }
    }

    private fun setupManageHiddenIcons() {
        binding.settingsManageHiddenIconsHolder.setOnClickListener {
            startActivity(Intent(this, HiddenIconsActivity::class.java))
        }
    }

    private fun setupEyeControlSettings() {
        setupEnableEyeControl()
        setupEnableClick()
        setupEnableDrag()
        setupEyeControlSensitivity()
        setupEyeControlSmoothing()
        setupHalfBlinkClickThreshold()
        setupHalfBlinkDragThreshold()
        setupCursorSize()
        setupShowCursor()
        setupShowEyeLine()
        setupEyeLineLength()
        setupGazeSmoothing()
        setupBlinkDurationThreshold()
        setupDebugLogging()
        setupPrintLogcat()
        
        // Advanced settings with increment/decrement buttons
        setupMovementMultipliers()
        setupEyePositionEffects()
        setupDistanceMultipliers()
    }

    private fun setupEnableEyeControl() {
        binding.settingsEnableEyeControl.isChecked = config.eyeControlEnabled
        binding.settingsEnableEyeControlHolder.setOnClickListener {
            val wasEnabled = config.eyeControlEnabled
            binding.settingsEnableEyeControl.toggle()
            config.eyeControlEnabled = binding.settingsEnableEyeControl.isChecked
            
            if (config.eyeControlEnabled && !wasEnabled) {
                // Request camera permission if needed
                if (androidx.core.content.ContextCompat.checkSelfPermission(
                        this,
                        android.Manifest.permission.CAMERA
                    ) != android.content.pm.PackageManager.PERMISSION_GRANTED
                ) {
                    androidx.core.app.ActivityCompat.requestPermissions(
                        this,
                        arrayOf(android.Manifest.permission.CAMERA),
                        100
                    )
                }
            }
        }
    }

    private fun setupEyeControlSensitivity() {
        val currentSensitivity = config.eyeControlSensitivity
        binding.settingsEyeControlSensitivity.text = String.format("%.2f", currentSensitivity)
        binding.settingsEyeControlSensitivityHolder.setOnClickListener {
            val items = ArrayList<RadioItem>()
            for (i in 1..20) {
                val value = i / 10.0f
                items.add(RadioItem(id = i, title = String.format("%.1f", value)))
            }
            RadioGroupDialog(this, items, (currentSensitivity * 10).toInt()) {
                val newSensitivity = (it as Int) / 10.0f
                if (currentSensitivity != newSensitivity) {
                    config.eyeControlSensitivity = newSensitivity
                    setupEyeControlSensitivity()
                }
            }
        }
    }

    private fun setupEyeControlSmoothing() {
        val currentSmoothing = config.eyeControlSmoothing
        binding.settingsEyeControlSmoothing.text = String.format("%.2f", currentSmoothing)
        binding.settingsEyeControlSmoothingHolder.setOnClickListener {
            val items = ArrayList<RadioItem>()
            for (i in 0..10) {
                val value = i / 10.0f
                items.add(RadioItem(id = i, title = String.format("%.1f", value)))
            }
            RadioGroupDialog(this, items, (currentSmoothing * 10).toInt()) {
                val newSmoothing = (it as Int) / 10.0f
                if (currentSmoothing != newSmoothing) {
                    config.eyeControlSmoothing = newSmoothing
                    setupEyeControlSmoothing()
                }
            }
        }
    }

    private fun setupHalfBlinkClickThreshold() {
        val currentThreshold = config.halfBlinkClickThreshold
        binding.settingsHalfBlinkClickThreshold.text = String.format("%.2f", currentThreshold)
        binding.settingsHalfBlinkClickThresholdHolder.setOnClickListener {
            val items = ArrayList<RadioItem>()
            // Increased range: 0.05 to 1.0 (was 0.3 default, now range is 0.05-1.0)
            for (i in 1..20) {
                val value = 0.05f + (i - 1) * 0.05f // Range from 0.05 to 1.0
                items.add(RadioItem(id = i, title = String.format("%.2f", value)))
            }
            RadioGroupDialog(this, items, (currentThreshold * 20).toInt()) {
                val newThreshold = (it as Int) / 20.0f
                if (currentThreshold != newThreshold) {
                    config.halfBlinkClickThreshold = newThreshold
                    setupHalfBlinkClickThreshold()
                }
            }
        }
    }

    private fun setupHalfBlinkDragThreshold() {
        val currentThreshold = config.halfBlinkDragThreshold
        binding.settingsHalfBlinkDragThreshold.text = String.format("%.2f", currentThreshold)
        binding.settingsHalfBlinkDragThresholdHolder.setOnClickListener {
            val items = ArrayList<RadioItem>()
            // Increased range: 0.1 to 1.0 (was 0.4 default, now range is 0.1-1.0)
            for (i in 1..20) {
                val value = i / 20.0f
                items.add(RadioItem(id = i, title = String.format("%.2f", value)))
            }
            RadioGroupDialog(this, items, (currentThreshold * 20).toInt()) {
                val newThreshold = (it as Int) / 20.0f
                if (currentThreshold != newThreshold) {
                    config.halfBlinkDragThreshold = newThreshold
                    setupHalfBlinkDragThreshold()
                }
            }
        }
    }

    private fun setupCursorSize() {
        val currentSize = config.cursorSize
        binding.settingsCursorSize.text = currentSize.toString()
        binding.settingsCursorSizeHolder.setOnClickListener {
            val items = ArrayList<RadioItem>()
            for (i in 10..50) {
                items.add(RadioItem(id = i, title = "$i"))
            }
            RadioGroupDialog(this, items, currentSize) {
                val newSize = it as Int
                if (currentSize != newSize) {
                    config.cursorSize = newSize
                    setupCursorSize()
                }
            }
        }
    }

    private fun setupEnableClick() {
        try {
            val enableClickSwitch = findViewById<com.google.android.material.switchmaterial.MaterialSwitch>(
                resources.getIdentifier("settings_enable_click", "id", packageName)
            )
            enableClickSwitch?.isChecked = config.enableClick
            val enableClickHolder = findViewById<View>(
                resources.getIdentifier("settings_enable_click_holder", "id", packageName)
            )
            enableClickHolder?.setOnClickListener {
                enableClickSwitch?.toggle()
                config.enableClick = enableClickSwitch?.isChecked ?: true
            }
        } catch (e: Exception) {
            // Layout might not have this field yet
        }
    }

    private fun setupEnableDrag() {
        try {
            val enableDragSwitch = findViewById<com.google.android.material.switchmaterial.MaterialSwitch>(
                resources.getIdentifier("settings_enable_drag", "id", packageName)
            )
            enableDragSwitch?.isChecked = config.enableDrag
            val enableDragHolder = findViewById<View>(
                resources.getIdentifier("settings_enable_drag_holder", "id", packageName)
            )
            enableDragHolder?.setOnClickListener {
                enableDragSwitch?.toggle()
                config.enableDrag = enableDragSwitch?.isChecked ?: true
            }
        } catch (e: Exception) {
            // Layout might not have this field yet
        }
    }

    private fun setupShowCursor() {
        binding.settingsShowCursor.isChecked = config.showCursor
        binding.settingsShowCursorHolder.setOnClickListener {
            binding.settingsShowCursor.toggle()
            config.showCursor = binding.settingsShowCursor.isChecked
        }
    }

    private fun setupShowEyeLine() {
        binding.settingsShowEyeLine.isChecked = config.showEyeLine
        binding.settingsShowEyeLineHolder.setOnClickListener {
            binding.settingsShowEyeLine.toggle()
            config.showEyeLine = binding.settingsShowEyeLine.isChecked
        }
    }

    private fun setupEyeLineLength() {
        val currentLength = config.eyeLineLength
        binding.settingsEyeLineLength.text = "$currentLength"
        binding.settingsEyeLineLengthHolder.setOnClickListener {
            val items = ArrayList<RadioItem>()
            for (i in 50..500 step 50) {
                items.add(RadioItem(id = i, title = "$i"))
            }
            RadioGroupDialog(this, items, currentLength) {
                val newLength = it as Int
                if (currentLength != newLength) {
                    config.eyeLineLength = newLength
                    setupEyeLineLength()
                }
            }
        }
    }

    private fun setupGazeSmoothing() {
        val currentSmoothing = config.gazeSmoothing
        binding.settingsGazeSmoothing.text = String.format("%.2f", currentSmoothing)
        binding.settingsGazeSmoothingHolder.setOnClickListener {
            val items = ArrayList<RadioItem>()
            for (i in 1..20) {
                val value = 0.1f + (i - 1) * 0.05f // Range from 0.1 to 1.0
                items.add(RadioItem(id = i, title = String.format("%.2f", value)))
            }
            val selectedIndex = ((currentSmoothing - 0.1f) / 0.05f).toInt().coerceIn(0, 19)
            RadioGroupDialog(this, items, selectedIndex + 1) {
                val newSmoothing = 0.1f + ((it as Int) - 1) * 0.05f
                if (kotlin.math.abs(currentSmoothing - newSmoothing) > 0.01f) {
                    config.gazeSmoothing = newSmoothing
                    setupGazeSmoothing()
                }
            }
        }
    }

    private fun setupBlinkDurationThreshold() {
        val currentThreshold = config.blinkDurationThreshold
        binding.settingsBlinkDurationThreshold.text = "$currentThreshold ms"
        binding.settingsBlinkDurationThresholdHolder.setOnClickListener {
            val items = ArrayList<RadioItem>()
            for (i in 100..1000 step 50) {
                items.add(RadioItem(id = i, title = "$i ms"))
            }
            RadioGroupDialog(this, items, currentThreshold) {
                val newThreshold = it as Int
                if (currentThreshold != newThreshold) {
                    config.blinkDurationThreshold = newThreshold
                    setupBlinkDurationThreshold()
                }
            }
        }
    }

    private fun setupDebugLogging() {
        binding.settingsDebugLogging.isChecked = config.debugLogging
        binding.settingsDebugLoggingHolder.setOnClickListener {
            binding.settingsDebugLogging.toggle()
            config.debugLogging = binding.settingsDebugLogging.isChecked
        }
    }

    private fun setupPrintLogcat() {
        binding.settingsPrintLogcatHolder.setOnClickListener {
            printLogcat()
        }
    }

    private fun printLogcat() {
        try {
            val process = Runtime.getRuntime().exec("logcat -d")
            val reader = java.io.BufferedReader(java.io.InputStreamReader(process.inputStream))
            val log = StringBuilder()
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                log.append(line).append("\n")
            }
            reader.close()
            
            // Filter for relevant tags
            val filteredLog = log.toString().lines()
                .filter { it.contains("EyeControlManager") || it.contains("CursorView") || it.contains("vision") }
                .joinToString("\n")
            
            android.util.Log.d("VisionLogcat", "=== Eye Control Logcat ===")
            android.util.Log.d("VisionLogcat", filteredLog)
            android.util.Log.d("VisionLogcat", "=== End Logcat ===")
            
            toast("Logcat printed to logcat. Filter by 'VisionLogcat' tag.")
        } catch (e: Exception) {
            android.util.Log.e("SettingsActivity", "Error printing logcat", e)
            toast("Error printing logcat: ${e.message}")
        }
    }

    private fun launchAbout() {
        val licenses = 0L
        val faqItems = ArrayList<FAQItem>()

        // Removed social links and donation references

        startAboutActivity(
            appNameId = R.string.app_name,
            licenseMask = licenses,
            versionName = BuildConfig.VERSION_NAME,
            faqItems = faqItems,
            showFAQBeforeMail = true
        )
    }
    
    // Advanced Settings with Increment/Decrement Adjusters
    
    private fun setupMovementMultipliers() {
        // X Movement Multiplier
        val xMovementValue = findViewById<android.widget.EditText>(R.id.x_movement_value)
        val xMovementMinus = findViewById<android.widget.Button>(R.id.x_movement_minus)
        val xMovementPlus = findViewById<android.widget.Button>(R.id.x_movement_plus)
        
        xMovementValue?.setText(String.format("%.2f", config.xMovementMultiplier))
        
        setupValueEditorDirect(xMovementValue,
            { config.xMovementMultiplier },
            { config.xMovementMultiplier = it })
        
        xMovementMinus?.setOnClickListener {
            xMovementValue?.clearFocus()
            val newValue = (config.xMovementMultiplier - 0.1f).coerceIn(-5f, 5f)
            config.xMovementMultiplier = newValue
            xMovementValue?.setText(String.format("%.2f", newValue))
        }
        
        xMovementPlus?.setOnClickListener {
            xMovementValue?.clearFocus()
            val newValue = (config.xMovementMultiplier + 0.1f).coerceIn(-5f, 5f)
            config.xMovementMultiplier = newValue
            xMovementValue?.setText(String.format("%.2f", newValue))
        }
        
        // Y Movement Multiplier
        val yMovementValue = findViewById<android.widget.EditText>(R.id.y_movement_value)
        val yMovementMinus = findViewById<android.widget.Button>(R.id.y_movement_minus)
        val yMovementPlus = findViewById<android.widget.Button>(R.id.y_movement_plus)
        
        yMovementValue?.setText(String.format("%.2f", config.yMovementMultiplier))
        
        setupValueEditorDirect(yMovementValue,
            { config.yMovementMultiplier },
            { config.yMovementMultiplier = it })
        
        yMovementMinus?.setOnClickListener {
            yMovementValue?.clearFocus()
            val newValue = (config.yMovementMultiplier - 0.1f).coerceIn(-5f, 5f)
            config.yMovementMultiplier = newValue
            yMovementValue?.setText(String.format("%.2f", newValue))
        }
        
        yMovementPlus?.setOnClickListener {
            yMovementValue?.clearFocus()
            val newValue = (config.yMovementMultiplier + 0.1f).coerceIn(-5f, 5f)
            config.yMovementMultiplier = newValue
            yMovementValue?.setText(String.format("%.2f", newValue))
        }
    }
    
    private fun setupEyePositionEffects() {
        // Eye Position X Effect
        val eyePosXEffectValue = findViewById<android.widget.EditText>(R.id.eye_pos_x_effect_value)
        val eyePosXEffectMinus = findViewById<android.widget.Button>(R.id.eye_pos_x_effect_minus)
        val eyePosXEffectPlus = findViewById<android.widget.Button>(R.id.eye_pos_x_effect_plus)
        
        eyePosXEffectValue?.setText(String.format("%.2f", config.eyePositionXEffect))
        
        setupValueEditorDirect(eyePosXEffectValue,
            { config.eyePositionXEffect },
            { config.eyePositionXEffect = it })
        
        eyePosXEffectMinus?.setOnClickListener {
            eyePosXEffectValue?.clearFocus()
            val newValue = (config.eyePositionXEffect - 0.1f).coerceIn(-5f, 5f)
            config.eyePositionXEffect = newValue
            eyePosXEffectValue?.setText(String.format("%.2f", newValue))
        }
        
        eyePosXEffectPlus?.setOnClickListener {
            eyePosXEffectValue?.clearFocus()
            val newValue = (config.eyePositionXEffect + 0.1f).coerceIn(-5f, 5f)
            config.eyePositionXEffect = newValue
            eyePosXEffectValue?.setText(String.format("%.2f", newValue))
        }
        
        // Eye Position X Multiplier
        val eyePosXMultValue = findViewById<android.widget.EditText>(R.id.eye_pos_x_mult_value)
        val eyePosXMultMinus = findViewById<android.widget.Button>(R.id.eye_pos_x_mult_minus)
        val eyePosXMultPlus = findViewById<android.widget.Button>(R.id.eye_pos_x_mult_plus)
        
        eyePosXMultValue?.setText(String.format("%.2f", config.eyePositionXMultiplier))
        
        setupValueEditorDirect(eyePosXMultValue,
            { config.eyePositionXMultiplier },
            { config.eyePositionXMultiplier = it })
        
        eyePosXMultMinus?.setOnClickListener {
            eyePosXMultValue?.clearFocus()
            val newValue = (config.eyePositionXMultiplier - 0.1f).coerceIn(-5f, 5f)
            config.eyePositionXMultiplier = newValue
            eyePosXMultValue?.setText(String.format("%.2f", newValue))
        }
        
        eyePosXMultPlus?.setOnClickListener {
            eyePosXMultValue?.clearFocus()
            val newValue = (config.eyePositionXMultiplier + 0.1f).coerceIn(-5f, 5f)
            config.eyePositionXMultiplier = newValue
            eyePosXMultValue?.setText(String.format("%.2f", newValue))
        }
        
        // Eye Position Y Effect
        val eyePosYEffectValue = findViewById<android.widget.EditText>(R.id.eye_pos_y_effect_value)
        val eyePosYEffectMinus = findViewById<android.widget.Button>(R.id.eye_pos_y_effect_minus)
        val eyePosYEffectPlus = findViewById<android.widget.Button>(R.id.eye_pos_y_effect_plus)
        
        eyePosYEffectValue?.setText(String.format("%.2f", config.eyePositionYEffect))
        
        setupValueEditorDirect(eyePosYEffectValue,
            { config.eyePositionYEffect },
            { config.eyePositionYEffect = it })
        
        eyePosYEffectMinus?.setOnClickListener {
            eyePosYEffectValue?.clearFocus()
            val newValue = (config.eyePositionYEffect - 0.1f).coerceIn(-5f, 5f)
            config.eyePositionYEffect = newValue
            eyePosYEffectValue?.setText(String.format("%.2f", newValue))
        }
        
        eyePosYEffectPlus?.setOnClickListener {
            eyePosYEffectValue?.clearFocus()
            val newValue = (config.eyePositionYEffect + 0.1f).coerceIn(-5f, 5f)
            config.eyePositionYEffect = newValue
            eyePosYEffectValue?.setText(String.format("%.2f", newValue))
        }
        
        // Eye Position Y Multiplier
        val eyePosYMultValue = findViewById<android.widget.EditText>(R.id.eye_pos_y_mult_value)
        val eyePosYMultMinus = findViewById<android.widget.Button>(R.id.eye_pos_y_mult_minus)
        val eyePosYMultPlus = findViewById<android.widget.Button>(R.id.eye_pos_y_mult_plus)
        
        eyePosYMultValue?.setText(String.format("%.2f", config.eyePositionYMultiplier))
        
        setupValueEditorDirect(eyePosYMultValue,
            { config.eyePositionYMultiplier },
            { config.eyePositionYMultiplier = it })
        
        eyePosYMultMinus?.setOnClickListener {
            eyePosYMultValue?.clearFocus()
            val newValue = (config.eyePositionYMultiplier - 0.1f).coerceIn(-5f, 5f)
            config.eyePositionYMultiplier = newValue
            eyePosYMultValue?.setText(String.format("%.2f", newValue))
        }
        
        eyePosYMultPlus?.setOnClickListener {
            eyePosYMultValue?.clearFocus()
            val newValue = (config.eyePositionYMultiplier + 0.1f).coerceIn(-5f, 5f)
            config.eyePositionYMultiplier = newValue
            eyePosYMultValue?.setText(String.format("%.2f", newValue))
        }
    }
    
    private fun setupDistanceMultipliers() {
        // Distance X Multiplier
        val distanceXValue = findViewById<android.widget.EditText>(R.id.distance_x_value)
        val distanceXMinus = findViewById<android.widget.Button>(R.id.distance_x_minus)
        val distanceXPlus = findViewById<android.widget.Button>(R.id.distance_x_plus)
        
        distanceXValue?.setText(String.format("%.2f", config.distanceXMultiplier))
        
        setupValueEditorDirect(distanceXValue,
            { config.distanceXMultiplier },
            { config.distanceXMultiplier = it })
        
        distanceXMinus?.setOnClickListener {
            distanceXValue?.clearFocus()
            val newValue = (config.distanceXMultiplier - 0.1f).coerceIn(-5f, 5f)
            config.distanceXMultiplier = newValue
            distanceXValue?.setText(String.format("%.2f", newValue))
        }
        
        distanceXPlus?.setOnClickListener {
            distanceXValue?.clearFocus()
            val newValue = (config.distanceXMultiplier + 0.1f).coerceIn(-5f, 5f)
            config.distanceXMultiplier = newValue
            distanceXValue?.setText(String.format("%.2f", newValue))
        }
        
        // Distance Y Multiplier
        val distanceYValue = findViewById<android.widget.EditText>(R.id.distance_y_value)
        val distanceYMinus = findViewById<android.widget.Button>(R.id.distance_y_minus)
        val distanceYPlus = findViewById<android.widget.Button>(R.id.distance_y_plus)
        
        distanceYValue?.setText(String.format("%.2f", config.distanceYMultiplier))
        
        setupValueEditorDirect(distanceYValue,
            { config.distanceYMultiplier },
            { config.distanceYMultiplier = it })
        
        distanceYMinus?.setOnClickListener {
            distanceYValue?.clearFocus()
            val newValue = (config.distanceYMultiplier - 0.1f).coerceIn(-5f, 5f)
            config.distanceYMultiplier = newValue
            distanceYValue?.setText(String.format("%.2f", newValue))
        }
        
        distanceYPlus?.setOnClickListener {
            distanceYValue?.clearFocus()
            val newValue = (config.distanceYMultiplier + 0.1f).coerceIn(-5f, 5f)
            config.distanceYMultiplier = newValue
            distanceYValue?.setText(String.format("%.2f", newValue))
        }
    }
    
    private fun setupValueEditorDirect(
        editText: android.widget.EditText?,
        getter: () -> Float,
        setter: (Float) -> Unit
    ) {
        editText?.apply {
            setOnEditorActionListener { _, _, _ ->
                try {
                    val value = text.toString().toFloatOrNull()
                    if (value != null) {
                        setter(value)
                        clearFocus()
                        true
                    } else {
                        setText(String.format("%.2f", getter()))
                        false
                    }
                } catch (e: Exception) {
                    setText(String.format("%.2f", getter()))
                    false
                }
            }
            
            setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    try {
                        val value = text.toString().toFloatOrNull()
                        if (value != null) {
                            setter(value)
                        } else {
                            setText(String.format("%.2f", getter()))
                        }
                    } catch (e: Exception) {
                        setText(String.format("%.2f", getter()))
                    }
                }
            }
        }
    }
}
