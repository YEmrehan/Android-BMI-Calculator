package com.example.bmi

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import androidx.core.graphics.toColorInt

class BmiGaugeView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    var bmiValue: Float = 22.0f
        set(value) {
            field = value
            invalidate()
        }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    @SuppressLint("DrawAllocation", "DefaultLocale")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val width = width.toFloat()
        val height = height.toFloat()
        val radius = min(width, height) / 2.5f
        val centerX = width / 2
        val centerY = height * 0.8f

        val rect = RectF(centerX - radius, centerY - radius, centerX + radius, centerY + radius)

        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 60f

        paint.color = "#64B5F6".toColorInt()
        canvas.drawArc(rect, 180f, 45f, false, paint)

        paint.color = "#81C784".toColorInt()
        canvas.drawArc(rect, 225f, 90f, false, paint)

        paint.color = "#E57373".toColorInt()
        canvas.drawArc(rect, 315f, 45f, false, paint)

        val angle = mapBmiToAngle(bmiValue)
        val arrowLength = radius - 40
        val rad = Math.toRadians(angle.toDouble())
        val endX = (centerX + arrowLength * cos(rad)).toFloat()
        val endY = (centerY + arrowLength * sin(rad)).toFloat()

        paint.color = Color.BLACK
        paint.strokeWidth = 8f
        canvas.drawLine(centerX, centerY, endX, endY, paint)

        paint.apply {
            textSize = 50f
            color = Color.BLUE
            textAlign = Paint.Align.CENTER
            style = Paint.Style.FILL
            strokeWidth = 0f
            isAntiAlias = true
        }
        canvas.drawText("BMI: ${String.format("%.1f", bmiValue)}", centerX, centerY - radius - 60, paint)
    }

    private fun mapBmiToAngle(bmi: Float): Float {
        return when {
            bmi < 18.5f -> {
                180f + ((bmi - 10f) / 8.5f) * 45f
            }

            bmi < 25f -> {
                225f + ((bmi - 18.5f) / 6.5f) * 90f
            }

            else -> {
                val capped = bmi.coerceAtMost(40f)
                315f + ((capped - 25f) / 15f) * 45f
            }

        }
    }

}
