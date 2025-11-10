package com.qali.vision.services

import android.app.ActivityManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.core.app.NotificationCompat
import com.qali.vision.R
import com.qali.vision.helpers.Config
import com.qali.vision.managers.LogcatManager
import com.qali.vision.views.PointerView

/**
 * Service that displays a floating pointer overlay on top of all apps
 * This allows the pointer to be visible even when the app is in background
 */
class PointerOverlayService : Service() {
    
    companion object {
        private const val TAG = "PointerOverlayService"
        private const val NOTIFICATION_ID = 2
        private const val CHANNEL_ID = "pointer_overlay_channel"
        private var instance: PointerOverlayService? = null
        
        fun getInstance(): PointerOverlayService? = instance
        
        /**
         * Check if the app is currently in the foreground
         * Required for Android 15 overlay update restrictions
         */
        fun isAppInForeground(context: Context): Boolean {
            val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
                ?: return false
            
            val appProcesses = activityManager.runningAppProcesses ?: return false
            val packageName = context.packageName
            
            for (appProcess in appProcesses) {
                if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND &&
                    appProcess.processName == packageName) {
                    return true
                }
            }
            return false
        }
        
        fun updatePointerPosition(x: Float, y: Float) {
            instance?.let { service ->
                Log.d(TAG, "updatePointerPosition called: x=$x, y=$y")
                if (x < 0 || y < 0) {
                    Log.d(TAG, "updatePointerPosition: Hiding pointer (invalid coordinates)")
                    service.hidePointer()
                } else {
                    service.pointerView?.visibility = View.VISIBLE
                    service.updatePointer(x, y)
                }
            } ?: run {
                Log.w(TAG, "updatePointerPosition: Service instance is null - cannot update pointer")
            }
        }
        
        fun indicateClick() {
            instance?.pointerView?.indicateClick()
        }

        fun indicateDragStart() {
            instance?.pointerView?.indicateDragStart()
        }

        fun indicateDragEnd() {
            instance?.pointerView?.indicateDragEnd()
        }
    }
    
    var pointerView: PointerView? = null
        private set
    
    private var windowManager: WindowManager? = null
    private var pointerLayout: FrameLayout? = null
    
    override fun onCreate() {
        super.onCreate()
        instance = this
        
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        
        createPointerView()
        
        // Try to start as foreground service if possible (for Android O+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                createNotificationChannel()
                startForeground(NOTIFICATION_ID, createNotification())
                Log.d(TAG, "Started as foreground service")
            } catch (e: Exception) {
                Log.w(TAG, "Could not start as foreground service: ${e.message}")
            }
        }
        
        Log.d(TAG, "PointerOverlayService created")
    }
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Vision Pointer Overlay",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Displays pointer overlay on screen"
                setShowBadge(false)
            }
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Vision Pointer")
            .setContentText("Pointer overlay is active")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .build()
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                startForeground(NOTIFICATION_ID, createNotification())
            } catch (e: Exception) {
                Log.e(TAG, "Failed to start foreground in onStartCommand: ${e.message}", e)
            }
        }
        return START_STICKY
    }
    
    override fun onBind(intent: Intent?): IBinder? = null
    
    private fun createPointerView() {
        pointerLayout = FrameLayout(this).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            )
            visibility = View.GONE
        }
        
        // Create custom pointer view
        pointerView = PointerView(this).apply {
            layoutParams = FrameLayout.LayoutParams(60, 60)
            // Apply colors from settings
            val config = Config(this@PointerOverlayService)
            setCursorColor(config.cursorColor)
            setClickColor(config.clickColor)
            setDragColor(config.dragColor)
        }
        
        pointerLayout?.addView(pointerView)
        
        // Determine window type based on Android version
        val windowType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (Build.VERSION.SDK_INT >= 35 && MouseControlService.isAccessibilityServiceEnabled(this)) {
                try {
                    WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY
                } catch (e: Exception) {
                    Log.w(TAG, "TYPE_ACCESSIBILITY_OVERLAY not available, using TYPE_APPLICATION_OVERLAY: ${e.message}")
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                }
            } else {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            }
        } else {
            @Suppress("DEPRECATION")
            WindowManager.LayoutParams.TYPE_PHONE
        }
        
        Log.d(TAG, "Using window type: $windowType (SDK=${Build.VERSION.SDK_INT}, accessibility=${MouseControlService.isAccessibilityServiceEnabled(this)})")
        
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            windowType,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = -1000 // Start off-screen
            y = -1000
        }
        
        try {
            windowManager?.addView(pointerLayout, params)
            Log.d(TAG, "Pointer overlay added (initially hidden)")
        } catch (e: Exception) {
            Log.e(TAG, "Error adding pointer overlay: ${e.message}", e)
        }
    }
    
    fun hidePointer() {
        pointerLayout?.let { view ->
            try {
                view.visibility = View.GONE
                val params = view.layoutParams as? WindowManager.LayoutParams
                params?.let {
                    it.x = -1000
                    it.y = -1000
                    try {
                        windowManager?.updateViewLayout(view, it)
                    } catch (e: Exception) {
                        Log.e(TAG, "Error updating pointer layout: ${e.message}", e)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error hiding pointer: ${e.message}", e)
            }
        }
    }
    
    fun updatePointer(x: Float, y: Float) {
        Log.d(TAG, "updatePointer() called: x=$x, y=$y")
        
        if (x < 0 || y < 0) {
            Log.d(TAG, "updatePointer: Invalid coordinates, hiding pointer")
            hidePointer()
            return
        }
        
        pointerLayout?.let { view ->
            val params = view.layoutParams as? WindowManager.LayoutParams
            params?.let {
                val screenX = x.toInt() - 30 // Center the pointer (60/2)
                val screenY = y.toInt() - 30
                
                val isAccessibilityEnabled = MouseControlService.isAccessibilityServiceEnabled(this)
                val isForeground = isAppInForeground(this)
                val canUpdateOverlay = isAccessibilityEnabled || isForeground
                
                val canDrawOverlays = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    Settings.canDrawOverlays(this)
                } else {
                    true
                }
                
                Log.d(TAG, "updatePointer: screenX=$screenX, screenY=$screenY, accessibility=$isAccessibilityEnabled, foreground=$isForeground, canDrawOverlays=$canDrawOverlays")
                
                if (!canDrawOverlays) {
                    Log.w(TAG, "updatePointer: Overlay permission not granted")
                    return
                }
                
                if (!canUpdateOverlay) {
                    Log.w(TAG, "updatePointer: Overlay update blocked (accessibility=$isAccessibilityEnabled, foreground=$isForeground)")
                    return
                }
                
                it.x = screenX
                it.y = screenY
                
                try {
                    if (view.visibility != View.VISIBLE) {
                        view.visibility = View.VISIBLE
                        Log.d(TAG, "updatePointer: Pointer view made visible")
                    }
                    
                    android.os.Handler(android.os.Looper.getMainLooper()).post {
                        try {
                            windowManager?.updateViewLayout(view, it)
                            Log.d(TAG, "Overlay UPDATED: Pointer at ($screenX, $screenY)")
                            LogcatManager.addLog("Overlay UPDATED: Pointer at ($screenX, $screenY)", "PointerOverlayService")
                        } catch (e: Exception) {
                            Log.e(TAG, "updatePointer: Error updating pointer position on main thread: ${e.message}", e)
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "updatePointer: Error updating pointer position: ${e.message}", e)
                }
            } ?: run {
                Log.w(TAG, "updatePointer: Layout params are null")
            }
        } ?: run {
            Log.w(TAG, "updatePointer: Pointer layout is null")
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        instance = null
        
        pointerLayout?.let {
            try {
                windowManager?.removeView(it)
            } catch (e: Exception) {
                Log.e(TAG, "Error removing pointer overlay: ${e.message}", e)
            }
        }
        
        Log.d(TAG, "PointerOverlayService destroyed")
    }
}

