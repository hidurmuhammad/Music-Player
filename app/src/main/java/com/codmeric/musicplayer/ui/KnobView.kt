package com.codmeric.musicplayer.ui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.atan2
import kotlin.math.min
import androidx.core.graphics.toColorInt

class KnobView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var progress = 0f // 0 to 1
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var listener: ((Float) -> Unit)? = null

    var progressValue: Float
        get() = progress
        set(value) {
            progress = value.coerceIn(0f, 1f)
            invalidate()
        }

    fun setOnProgressListener(l: (Float) -> Unit) {
        listener = l
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val w = width.toFloat()
        val h = height.toFloat()
        val cx = w / 2
        val cy = h / 2
        val radius = min(w, h) / 2 * 0.8f

        // Draw background circle
        paint.color = "#4DFFFFFF".toColorInt() // Half-transparent white
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 12f
        canvas.drawCircle(cx, cy, radius, paint)

        // Draw progress arc or line
        paint.color = "#00CEC9".toColorInt() // Accent Cyan
        paint.strokeWidth = 16f
        
        // Simple indicator line
        val angle = 135f + progress * 270f
        val rad = Math.toRadians(angle.toDouble())
        val indicatorRadius = radius * 0.9f
        val lx = cx + indicatorRadius * Math.cos(rad).toFloat()
        val ly = cy + indicatorRadius * Math.sin(rad).toFloat()
        
        canvas.drawLine(cx, cy, lx, ly, paint)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_MOVE || event.action == MotionEvent.ACTION_DOWN) {
            val angle = atan2(event.y - height / 2, event.x - width / 2)
            var degree = Math.toDegrees(angle.toDouble()).toFloat()
            
            // Adjust degree to 0-360 starting from bottom-left (135 deg)
            degree = (degree + 360) % 360
            
            // Map 135-405 to 0-1
            var relative = (degree - 135 + 360) % 360
            if (relative > 270) {
                relative = if (relative < 315) 270f else 0f
            }
            
            progressValue = relative / 270f
            listener?.invoke(progressValue)
            return true
        }
        return super.onTouchEvent(event)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val size = min(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec))
        setMeasuredDimension(size, size)
    }
}
