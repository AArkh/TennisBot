package tennis.bot.mobile.utils.view

import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import androidx.annotation.Px
import androidx.core.graphics.withSave
import kotlin.math.min

class TextShapeDrawable(
    text: String,
    @ColorInt backgroundColor: Int,
    val shape: Shape = Shape.CIRCLE,
    @Px private var width: Int = -1,
    @Px private var height: Int = -1
) : Drawable(), Drawable.Callback {

    @Px
    var squareRadius = 0f
        set(value) {
            field = value
            invalidateSelf()
        }

    private val textDrawable = TextDrawable(text = text)
    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL_AND_STROKE
        color = backgroundColor
    }

    private val boundsRectF = RectF()
    @Px
    private var circleRadius = 0f

    init {
        textDrawable.callback = this
    }

    override fun onBoundsChange(bounds: Rect) {
        super.onBoundsChange(bounds)
        boundsRectF.set(bounds)
        circleRadius = min(bounds.width(), bounds.height()) * 0.5f
        textDrawable.setSize(min(bounds.width(), bounds.height()) * 0.35f)
        textDrawable.setBounds(0, 0, textDrawable.intrinsicWidth, textDrawable.intrinsicHeight)
    }

    override fun draw(canvas: Canvas) {
        when (shape) {
            Shape.CIRCLE -> canvas.drawCircle(bounds.exactCenterX(), bounds.exactCenterY(), circleRadius, backgroundPaint)
            Shape.SQUARE -> canvas.drawRoundRect(boundsRectF, squareRadius, squareRadius, backgroundPaint)
        }

        canvas.withSave {
            val xTranslation = (bounds.width() - textDrawable.bounds.width()) * 0.5f
            val yTranslation = (bounds.height() - textDrawable.bounds.height()) * 0.5f
            canvas.translate(xTranslation, yTranslation)
            textDrawable.draw(canvas)
        }
    }

    override fun setAlpha(alpha: Int) {
        textDrawable.alpha = alpha
        backgroundPaint.alpha = alpha
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        textDrawable.colorFilter = colorFilter
        backgroundPaint.colorFilter = colorFilter
    }

    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }

    override fun invalidateDrawable(who: Drawable) {
        callback?.invalidateDrawable(this)
    }

    override fun scheduleDrawable(who: Drawable, what: Runnable, `when`: Long) {
        callback?.scheduleDrawable(this, what, `when`)
    }

    override fun unscheduleDrawable(who: Drawable, what: Runnable) {
        callback?.unscheduleDrawable(this, what)
    }

    override fun getIntrinsicWidth(): Int {
        return if (width == -1) super.getIntrinsicWidth() else width
    }

    override fun getIntrinsicHeight(): Int {
        return if (height == -1) super.getIntrinsicHeight() else height
    }

    fun setTextSize(@Px size: Float) {
        textDrawable.setSize(size)
    }

    fun setSize(@Px width: Int, @Px height: Int) {
        this.width = width
        this.height = height
    }

    enum class Shape {
        CIRCLE,
        SQUARE
    }
}