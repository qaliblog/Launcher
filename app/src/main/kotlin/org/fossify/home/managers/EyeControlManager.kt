package com.qali.vision.managers

import android.content.Context
import android.graphics.PointF
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.util.Log
import android.view.MotionEvent
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.facedetector.FaceDetector
import com.google.mediapipe.tasks.vision.facedetector.FaceDetectorOptions
import com.qali.vision.extensions.config
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.math.abs

class EyeControlManager(
    private val context: Context,
    private val lifecycleOwner: LifecycleOwner,
    private val previewView: PreviewView,
    private val onCursorUpdate: (x: Float, y: Float) -> Unit,
    private val onClick: (x: Float, y: Float) -> Unit,
    private val onDrag: (x: Float, y: Float) -> Unit
) {
    private var cameraProvider: ProcessCameraProvider? = null
    private var faceDetector: FaceDetector? = null
    private val executor: ExecutorService = Executors.newSingleThreadExecutor()
    
    private var lastEyePosition = PointF(0f, 0f)
    private var smoothedPosition = PointF(0f, 0f)
    private var isBlinking = false
    private var blinkStartTime = 0L
    private var isDragging = false
    
    private val screenWidth: Int
        get() = context.resources.displayMetrics.widthPixels
    private val screenHeight: Int
        get() = context.resources.displayMetrics.heightPixels

    fun initialize() {
        try {
            val baseOptions = BaseOptions.builder()
                .setModelAssetPath("face_detection_short_range.tflite")
                .setDelegate(BaseOptions.Delegate.CPU)
                .build()

            val options = FaceDetectorOptions.builder()
                .setBaseOptions(baseOptions)
                .setRunningMode(RunningMode.LIVE_STREAM)
                .setResultListener { result, inputImage ->
                    processFaceDetectionResult(result, inputImage)
                }
                .build()

            faceDetector = FaceDetector.createFromOptions(context, options)
            startCamera()
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing face detector", e)
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        
        cameraProviderFuture.addListener({
            try {
                cameraProvider = cameraProviderFuture.get()
                bindCameraUseCases()
            } catch (e: Exception) {
                Log.e(TAG, "Error starting camera", e)
            }
        }, ContextCompat.getMainExecutor(context))
    }

    private fun bindCameraUseCases() {
        val cameraProvider = cameraProvider ?: return
        
        val preview = Preview.Builder().build().also {
            it.setSurfaceProvider(previewView.surfaceProvider)
        }

        val imageAnalysis = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
            .also {
                it.setAnalyzer(executor) { imageProxy ->
                    processImage(imageProxy)
                }
            }

        val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                imageAnalysis
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error binding camera use cases", e)
        }
    }

    private fun processImage(imageProxy: androidx.camera.core.ImageProxy) {
        val faceDetector = faceDetector ?: return
        
        // Convert ImageProxy to MediaPipe Image format
        // This is a simplified version - actual implementation would need proper conversion
        // MediaPipe Tasks Vision API requires proper image format conversion
        try {
            // TODO: Convert ImageProxy to MediaPipe Image and call faceDetector.detectAsync()
            // For now, this is a placeholder that needs proper MediaPipe integration
        } catch (e: Exception) {
            Log.e(TAG, "Error processing image", e)
        } finally {
            imageProxy.close()
        }
    }

    private fun processFaceDetectionResult(
        result: com.google.mediapipe.tasks.vision.facedetector.FaceDetectorResult,
        inputImage: com.google.mediapipe.formats.proto.ImageProto.Image
    ) {
        if (result.detections().isEmpty()) return

        val detection = result.detections().first()
        val landmarks = detection.landmarks()
        
        if (landmarks.size < 10) return // Need at least eye landmarks

        // Extract eye positions (simplified - actual implementation would use proper landmark indices)
        val leftEye = landmarks[33] // Approximate left eye center
        val rightEye = landmarks[263] // Approximate right eye center
        
        val eyeCenterX = (leftEye.x() + rightEye.x()) / 2f
        val eyeCenterY = (leftEye.y() + rightEye.y()) / 2f
        
        // Calculate eye aspect ratio for blink detection
        val eyeAspectRatio = calculateEyeAspectRatio(landmarks)
        
        // Detect blink
        val threshold = config.halfBlinkClickThreshold
        val isHalfBlink = eyeAspectRatio < threshold
        
        if (isHalfBlink && !isBlinking) {
            isBlinking = true
            blinkStartTime = System.currentTimeMillis()
        } else if (!isHalfBlink && isBlinking) {
            val blinkDuration = System.currentTimeMillis() - blinkStartTime
            isBlinking = false
            
            // Determine if it's a click or drag based on duration
            if (blinkDuration < 300) {
                // Click
                onClick(smoothedPosition.x, smoothedPosition.y)
            } else if (blinkDuration >= 300 && blinkDuration < 1000) {
                // Start drag
                isDragging = true
            }
        }
        
        if (!isHalfBlink && isDragging) {
            // Continue drag
            onDrag(smoothedPosition.x, smoothedPosition.y)
        } else if (isHalfBlink && isDragging) {
            // End drag
            isDragging = false
        }
        
        // Convert normalized coordinates to screen coordinates
        val sensitivity = config.eyeControlSensitivity
        val newX = eyeCenterX * screenWidth * sensitivity
        val newY = eyeCenterY * screenHeight * sensitivity
        
        // Apply smoothing
        val smoothing = config.eyeControlSmoothing
        smoothedPosition.x = smoothedPosition.x * smoothing + newX * (1 - smoothing)
        smoothedPosition.y = smoothedPosition.y * smoothing + newY * (1 - smoothing)
        
        lastEyePosition = PointF(newX, newY)
        
        // Update cursor position
        onCursorUpdate(smoothedPosition.x, smoothedPosition.y)
    }

    private fun calculateEyeAspectRatio(landmarks: List<com.google.mediapipe.formats.proto.LandmarkProto.NormalizedLandmark>): Float {
        // Simplified eye aspect ratio calculation
        // In a real implementation, you would use specific landmark points
        if (landmarks.size < 6) return 1.0f
        
        // This is a placeholder - actual implementation would calculate based on eye landmarks
        val verticalDistance = abs(landmarks[1].y() - landmarks[5].y())
        val horizontalDistance = abs(landmarks[0].x() - landmarks[3].x())
        
        return if (horizontalDistance > 0) {
            verticalDistance / horizontalDistance
        } else {
            1.0f
        }
    }

    fun stop() {
        cameraProvider?.unbindAll()
        faceDetector?.close()
        executor.shutdown()
    }

    companion object {
        private const val TAG = "EyeControlManager"
    }
}

