package tennis.bot.mobile.utils

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.View
import tennis.bot.mobile.utils.Const.BUTTON_ANIMATION_DURATION

object Const{
	const val BUTTON_ANIMATION_DURATION = 300L
}
fun animateButtonTransition(backgroundView: View, targetButton: View) {
	val widthChangeAnimator = ObjectAnimator.ofInt(
		backgroundView.layoutParams.width,
		targetButton.width
	)
	widthChangeAnimator.addUpdateListener { valueAnimator ->
		val params = backgroundView.layoutParams
		params.width = valueAnimator.animatedValue as Int
		backgroundView.layoutParams = params
	}

	val translationAnimator = ObjectAnimator.ofFloat(
		backgroundView,
		View.X,
		backgroundView.x,
		targetButton.x
	)

	val animatorSet = AnimatorSet()
	animatorSet.playTogether(widthChangeAnimator, translationAnimator)
	animatorSet.duration = BUTTON_ANIMATION_DURATION
	animatorSet.start()
}