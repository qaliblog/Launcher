package com.qali.vision.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.util.AttributeSet
import android.view.View

class CursorView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var cursorPosition = PointF(0f, 0f)
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = context.config.cursorColor
    }

    fun updatePosition(x: Float, y: Float) {
        cursorPosition.x = x
        cursorPosition.y = y
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        
        if (!context.config.showCursor) return
        
        val size = context.config.cursorSize.toFloat()
        val radius = size / 2f
        
        // Draw cursor as a circle
        canvas.drawCircle(cursorPosition.x, cursorPosition.y, radius, paint)
        
        // Draw crosshair
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 2f
        canvas.drawLine(
            cursorPosition.x - size,
            cursorPosition.y,
            cursorPosition.x + size,
            cursorPosition.y,
            paint
        )
        canvas.drawLine(
            cursorPosition.x,
            cursorPosition.y - size,
            cursorPosition.x,
            cursorPosition.y + size,
            paint
        )
        
        paint.style = Paint.Style.FILL
    }
}

