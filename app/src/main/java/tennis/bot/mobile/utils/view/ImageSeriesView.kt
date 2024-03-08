package tennis.bot.mobile.utils.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import androidx.annotation.Keep
import androidx.core.content.ContextCompat.getColor
import androidx.core.view.setPadding
import coil.ImageLoader
import coil.load
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import kotlinx.parcelize.Parcelize
import tennis.bot.mobile.R
import tennis.bot.mobile.profile.account.AccountPageAdapter
import tennis.bot.mobile.profile.account.getDefaultDrawableResourceId
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

    var drawableSize = context.dpToPx(36)
        set(value) {
            field = value
            requestLayout()
        }
    private var drawables = Array<Drawable?>(3) { null }
    private val borderWidth = context.dpToPx(2)
    private val borderPaint = Paint().apply {
        color = Color.WHITE
        style = Paint.Style.STROKE
        strokeWidth = borderWidth.toFloat()
    }

    private var imageOffsetFactor = DEFAULT_VIEW_OFFSET_FACTOR
    private var withBorder: Boolean = false

    private var avatarImages: List<AvatarImage>? = null
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
        if (this.avatarImages == avatarImages && this.additionalCount == additionalCount) {
            return
        }

        this.avatarImages = avatarImages
        this.additionalCount = additionalCount

        val drawablesCount = avatarImages.size + if (additionalCount == 0) 0 else 1
        drawables = Array(drawablesCount) { null }
        if (additionalCount > 0) {
            drawables[drawables.lastIndex] = TextShapeDrawable("+${additionalCount}", Color.BLUE)
        }

        avatarImages.forEachIndexed { index, avatarImage ->
            val drawables = drawables // to avoid index out of bounds exception when set images called twice
            drawables[index] = TextShapeDrawable(avatarImage.shortName, getColor(context, R.color.tb_gray_border))
            val imageLoader = ImageLoader(context)
            val request = ImageRequest.Builder(context)
                .data(buildImageRequest(avatarImage.imageUrl))
                .target { result ->
                    drawables[index] = result
                    invalidate()
                }
                .transformations(CircleCropTransformation())
                .build()
            imageLoader.enqueue(request)
        }
        requestLayout()
    }

    fun setImageOffsetFactor(factor: Float) {
        imageOffsetFactor = factor
        requestLayout()
    }

    private fun buildImageRequest(profileImageUrl: String?): Any? {
        var result: Any? = null

        if (profileImageUrl == null) {
            result = R.drawable.null_placeholder
        } else if (profileImageUrl.contains("default")) {
            val resourceId = getDefaultDrawableResourceId(context, profileImageUrl.removeSuffix(".png"))
            if (resourceId != null) result = resourceId
        } else {
            result = AccountPageAdapter.IMAGES_LINK + profileImageUrl
        }

        return result
    }

    private companion object {
        private const val DEFAULT_VIEW_OFFSET_FACTOR = 0.66f
    }
}

@Keep
@Parcelize
data class AvatarImage(
    val imageUrl: String?,
    val shortName: String = ""
) : Parcelable