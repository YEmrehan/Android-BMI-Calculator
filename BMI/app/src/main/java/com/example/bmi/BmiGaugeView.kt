package com.example.bmi

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import java.text.DecimalFormat
import androidx.core.graphics.toColorInt

class BmiGaugeView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    var bmiValue: Float = 22.0f
        set(value) {
            field = value
            invalidate()
        }

    private val arcRect = RectF()
    private val bmiFormat = DecimalFormat("#0.0")

    private val arcPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 60f
    }

    private val arrowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        strokeWidth = 8f
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = 50f
        color = Color.BLUE
        textAlign = Paint.Align.CENTER
        style = Paint.Style.FILL
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val width = width.toFloat()
        val height = height.toFloat()
        val radius = min(width, height) / 2.5f
        val centerX = width / 2
        val centerY = height * 0.8f

        arcRect.set(centerX - radius, centerY - radius, centerX + radius, centerY + radius)

        arcPaint.color = "#64B5F6".toColorInt()
        canvas.drawArc(arcRect, 180f, 45f, false, arcPaint)

        arcPaint.color = "#81C784".toColorInt()
        canvas.drawArc(arcRect, 225f, 90f, false, arcPaint)

        arcPaint.color = "#E57373".toColorInt()
        canvas.drawArc(arcRect, 315f, 45f, false, arcPaint)

        val angle = mapBmiToAngle(bmiValue)
        val arrowLength = radius - 40
        val rad = Math.toRadians(angle.toDouble())
        val endX = (centerX + arrowLength * cos(rad)).toFloat()
        val endY = (centerY + arrowLength * sin(rad)).toFloat()
        canvas.drawLine(centerX, centerY, endX, endY, arrowPaint)

        val bmiText = "BMI: ${bmiFormat.format(bmiValue)}"
        canvas.drawText(bmiText, centerX, centerY - radius - 60, textPaint)
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
