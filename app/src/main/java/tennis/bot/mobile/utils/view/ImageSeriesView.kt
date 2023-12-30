package tennis.bot.mobile.utils.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize
import tennis.bot.mobile.R
import tennis.bot.mobile.utils.dpToPx
import kotlin.math.roundToInt

/**
 * @property imageOffsetFactor - % of covering drawable by the next one. 0 - fully covering, 1 - not
 * covering, 2 - not even overlapping. Default is 0.66
 */
class ImageSeriesView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    var drawableSize = context.dpToPx(32)
        set(value) {
            field = value
            requestLayout()
        }
    private var drawables = Array<Drawable?>(3) { null }
    private val borderWidth = context.dpToPx(2)
    private val borderPaint = Paint().apply {
        color = Color.RED
        style = Paint.Style.STROKE
        strokeWidth = borderWidth.toFloat()
    }

    private var imageOffsetFactor = DEFAULT_VIEW_OFFSET_FACTOR
    private var withBorder: Boolean = false

    private var tokenImages: List<AvatarImage>? = null
    private var additionalCount = 0

    init {
        attrs?.let {
            val styledAttrs = context.obtainStyledAttributes(it, R.styleable.ImageSeriesView)
            imageOffsetFactor =
                styledAttrs.getFloat(R.styleable.ImageSeriesView_imageOffsetFactor, DEFAULT_VIEW_OFFSET_FACTOR)
            withBorder = styledAttrs.getBoolean(R.styleable.ImageSeriesView_withBorder, true)
            styledAttrs.recycle()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val imageCount = drawables.filterNotNull().size
        val borderPadding = if (withBorder) borderWidth * 2 else 0
        val width = drawableSize + (drawableSize * (imageCount - 1) * imageOffsetFactor).roundToInt() + borderPadding
        setMeasuredDimension(width, drawableSize + borderPadding)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        var xOffset = 0f
        for (i in drawables.indices) {
            drawables[i]?.let { drawable ->
                val left = xOffset.roundToInt()
                val borderPadding = if (withBorder) borderWidth else 0
                val drawableLeft = left + borderPadding
                val drawableTop = paddingTop + borderPadding
                val drawableRight = drawableLeft + drawableSize
                val drawableBottom = drawableTop + drawableSize

                if (withBorder) {
                    val cx = drawableLeft + drawableSize / 2f
                    val cy = drawableTop + drawableSize / 2f
                    val radius = drawableSize / 2f + borderPadding / 2f
                    canvas.drawCircle(cx, cy, radius, borderPaint)
                }

                drawable.setBounds(drawableLeft, drawableTop, drawableRight, drawableBottom)
                drawable.draw(canvas)
            }
            xOffset += drawableSize * imageOffsetFactor
        }
    }

    fun setImages(drawables: List<Drawable>) {
        if (this.drawables.toList().containsAll(drawables) && this.drawables.size == drawables.size) {
            return
        }
        this.drawables.forEachIndexed { index, _ ->
            if (index < drawables.size) {
                this.drawables[index] = drawables[index]
            } else {
                this.drawables[index] = null
            }
        }

        requestLayout()
    }

    fun setImages(avatarImages: List<AvatarImage>, additionalCount: Int) {
        if (this.tokenImages == avatarImages && this.additionalCount == additionalCount) {
            return
        }

        this.tokenImages = avatarImages
        this.additionalCount = additionalCount

        val drawablesCount = avatarImages.size + if (additionalCount == 0) 0 else 1
        drawables = Array(drawablesCount) { null }
        if (additionalCount > 0) {
            drawables[drawables.lastIndex] = TextShapeDrawable("+${additionalCount}", Color.BLUE)
        }

        avatarImages.forEachIndexed { index, tokenImage ->
            val drawables = drawables // to avoid index out of bounds exception when set images called twice
            drawables[index] = TextShapeDrawable(tokenImage.shortName, Color.MAGENTA)
            if (!tokenImage.imageUrl.isNullOrEmpty()) {
                // todo тут загружаем
//                Glide.with(context).asDrawable()
//                    .load(tokenImage.logoUrl)
//                    .circleCrop()
//                    .into(object : CustomTarget<Drawable>(drawableSize, drawableSize) {
//                        override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
//                            drawables[index] = resource
//                            invalidate()
//                        }
//
//                        override fun onLoadCleared(placeholder: Drawable?) = Unit
//                    })
            }
        }
        requestLayout()
    }

    fun setImageOffsetFactor(factor: Float) {
        imageOffsetFactor = factor
        requestLayout()
    }

    private companion object {
        private const val DEFAULT_VIEW_OFFSET_FACTOR = 0.66f
    }
}

@Keep
@Parcelize
data class AvatarImage(
    val imageUrl: String?,
    val shortName: String
) : Parcelable