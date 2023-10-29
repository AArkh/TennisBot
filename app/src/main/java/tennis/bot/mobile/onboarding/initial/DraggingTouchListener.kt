package tennis.bot.mobile.onboarding.initial

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.animation.OvershootInterpolator
import dagger.hilt.android.qualifiers.ApplicationContext
import tennis.bot.mobile.utils.dpToPx
import javax.inject.Inject
import kotlin.math.abs
import kotlin.math.sign
import kotlin.math.sqrt

class DraggingTouchListener @Inject constructor(
    @ApplicationContext context: Context
) : OnTouchListener {

    private var initialX: Float = 0f
    private var touchStartX: Float = 0f
    private var maxDistanceX: Float = context.dpToPx(40f)

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(view: View, event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                initialX = view.x
                touchStartX = event.rawX
            }
            MotionEvent.ACTION_MOVE -> {
                val distanceX = event.rawX - touchStartX
                val adjustedDistance = sqrt(abs(distanceX)) * sign(distanceX)
                val dragDistance = (adjustedDistance + view.x).coerceIn(-maxDistanceX + initialX, maxDistanceX + initialX)
                view.x = dragDistance
            }
            MotionEvent.ACTION_UP -> {
                val animator = ObjectAnimator.ofFloat(view, "x", initialX)
                animator.interpolator = OvershootInterpolator()
                animator.start()
            }
            else -> return false
        }
        return false
    }
}