package tennis.bot.mobile.utils.view

import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.text.TextPaint
import androidx.annotation.Px

class TextDrawable(
    private val text: CharSequence,
    @Px size: Float? = null,
) : Drawable() {

    private val textBounds = Rect()
    private val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG or TextPaint.LINEAR_TEXT_FLAG or TextPaint.SUBPIXEL_TEXT_FLAG)

    init {
        textPaint.textAlign = Paint.Align.CENTER
        textPaint.textSize = size ?: textPaint.textSize
        invalidateBounds()
    }

    override fun draw(canvas: Canvas) {
        canvas.drawText(text, 0, text.length, bounds.exactCenterX(), bounds.centerY() - textBounds.exactCenterY(), textPaint)
    }

    override fun setAlpha(alpha: Int) {
        textPaint.alpha = alpha
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        textPaint.colorFilter = colorFilter
    }

    override fun getOpacity(): Int = PixelFormat.TRANSPARENT

    override fun getIntrinsicWidth(): Int {
        return textBounds.width()
    }

    override fun getIntrinsicHeight(): Int {
        return textBounds.height()
    }

    fun setSize(@Px size: Float) {
        textPaint.textSize = size
        invalidateBounds()
    }

    private fun invalidateBounds() {
        textPaint.getTextBounds(text.toString(), 0, text.length, textBounds)
        invalidateSelf()
    }
}