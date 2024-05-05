package tennis.bot.mobile.feed.requestcreation

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import tennis.bot.mobile.R
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin

class GaugeView(context: Context, attrs: AttributeSet) : View(context, attrs) {

	var rating = 1400

	private var arcPaint = Paint(Paint.ANTI_ALIAS_FLAG)
	private var indicatorPaint = Paint(Paint.ANTI_ALIAS_FLAG)
	private var textPaint = Paint(Paint.ANTI_ALIAS_FLAG)
	private val arcColors = intArrayOf(
		context.getColor(R.color.tb_yellow),
		context.getColor(R.color.tb_new_green),
		context.getColor(R.color.tb_red_new)
	)
	private val gapAngle = 5f // Angle for the gaps between colored arcs
	private var isDragging = false

	private var centerX = 0f
	private var centerY = 0f
	private var radius = 0f
	private var indicatorRadius = 0f

	private var currentAngle = 270f // Initial angle (top)
	var currentRating = 1400
	private var showRatingText = true // Default: show the rating text

	init {
		arcPaint.style = Paint.Style.STROKE
		arcPaint.strokeWidth = 15f  // Adjust thickness as needed
		arcPaint.strokeCap = Paint.Cap.ROUND
		arcPaint.color = Color.LTGRAY

		indicatorPaint.style = Paint.Style.FILL
		indicatorPaint.color = Color.WHITE
		indicatorPaint.setShadowLayer(10f, 5f, 5f, Color.GRAY)

		textPaint.textSize = 40f
		textPaint.color = Color.BLACK
		textPaint.textAlign = Paint.Align.CENTER

		val attributes = context.obtainStyledAttributes(attrs, R.styleable.GaugeView)
		showRatingText = attributes.getBoolean(R.styleable.GaugeView_showRatingText, true)
		attributes.recycle()
	}

//	override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
//		val widthMode = MeasureSpec.getMode(widthMeasureSpec)
//		val widthSize = MeasureSpec.getSize(widthMeasureSpec)
//		val heightMode = MeasureSpec.getMode(heightMeasureSpec)
//		val heightSize = MeasureSpec.getSize(heightMeasureSpec)
//
//		// Calculate desired size based on arc radius and indicator size
//		// fixme это не работает, потому что onSizeChanged вызывается ПОСЛЕ onMeasure, на этот момент radius = 0
//		val desiredDiameter = 2 * radius + paddingLeft + paddingRight + indicatorPaint.strokeWidth
//		val desiredWidth = (desiredDiameter + paddingLeft + paddingRight).toInt()
//		val desiredHeight = (radius + paddingTop + paddingBottom + indicatorPaint.strokeWidth / 2).toInt()
//
//		// Ensure desired size is not smaller than suggested minimum
//		val width = resolveSize(max(desiredWidth, suggestedMinimumWidth), widthMeasureSpec)
//		val height = resolveSize(max(desiredHeight, suggestedMinimumHeight), heightMeasureSpec)
//
//		setMeasuredDimension(width, height)
//	}

	override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
		// fixme все эти переменные нужно выставлять в onMeasure, а этот метод убрать
		centerX = w / 2f
		centerY = h / 2f
		radius = minOf(w, h) / 2f * 0.8f // Calculate radius based on smaller dimension
		indicatorRadius = radius * 0.10f  // Calculate indicator radius as a proportion of view radius
	}


	override fun onDraw(canvas: Canvas) {
		// Draw background arc
		val totalAngle = 180f // Angle for a half-circle
		val sideArcAngle = (totalAngle - 2 * gapAngle) / 4
		val middleArcAngle = 2 * sideArcAngle

		var startAngle = 180f // Start from the leftmost point
		for (color in arcColors.indices) {
			arcPaint.color = arcColors[color]
			val angle = if (color == 1) middleArcAngle else sideArcAngle
			canvas.drawArc(centerX - radius, centerY - radius, centerX + radius, centerY + radius,
				startAngle, angle, false, arcPaint)
			startAngle += angle + gapAngle
		}

		// Calculate indicator position
		val indicatorX = centerX + radius * cos(Math.toRadians(currentAngle.toDouble())).toFloat()
		val indicatorY = centerY + radius * sin(Math.toRadians(currentAngle.toDouble())).toFloat()

		// Draw indicator circle
		canvas.drawCircle(indicatorX, indicatorY, indicatorRadius, indicatorPaint)

		if (showRatingText) {
			canvas.drawText(currentRating.toString(), centerX, centerY, textPaint)
		}

	}

	override fun onTouchEvent(event: MotionEvent): Boolean {
		when (event.action) {
			MotionEvent.ACTION_DOWN -> {
				isDragging = true // Start dragging mode
				updateIndicator(event.x, event.y)
			}
			MotionEvent.ACTION_MOVE -> {
				if (isDragging) {
					updateIndicator(event.x, event.y)
				}
			}
			MotionEvent.ACTION_UP -> {
				isDragging = false // End dragging mode
			}
		}
		return true
	}

	private fun updateIndicator(x: Float, y: Float) {
		val angle = calculateAngle(x, y)
		if (angle in 180f..360f) {
			currentAngle = angle
			currentRating = mapAngleToValue(angle)
			invalidate()
		}
	}

	private fun calculateAngle(x: Float, y: Float): Float {
		val dx = x - centerX
		val dy = y - centerY
		var angle = Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())).toFloat()
		if (angle < 0) angle += 360f
		return angle
	}

	private fun mapAngleToValue(angle: Float): Int {
		val totalAngle = 180f - 2 * gapAngle
		val valueRange = 600
		val anglePerValue = totalAngle / valueRange

		val angleOffset = angle - 270f
		var valueOffset = (angleOffset / anglePerValue + 0.5f).toInt() // Add 0.5f for rounding

		valueOffset = when {
			valueOffset < -300 -> -300 // Limit for yellow section
			valueOffset > 300 -> 300  // Limit for red section
			else -> valueOffset
		}

		return rating + valueOffset
	}
}