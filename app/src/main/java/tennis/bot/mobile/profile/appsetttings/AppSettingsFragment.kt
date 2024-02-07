package tennis.bot.mobile.profile.appsetttings

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewPropertyAnimator
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import android.widget.FrameLayout
import tennis.bot.mobile.core.CoreFragment
import tennis.bot.mobile.core.Inflation
import tennis.bot.mobile.databinding.FragmentAppSettingsBinding


class AppSettingsFragment : CoreFragment<FragmentAppSettingsBinding>() {
	override val bindingInflation: Inflation<FragmentAppSettingsBinding> = FragmentAppSettingsBinding::inflate
	private var isSwitchOn: Boolean = false

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		binding.backButton.setOnClickListener {
			parentFragmentManager.popBackStack()
		}

		binding.customSwitchLayout.setOnClickListener {
			animateSwitchTransition(binding.thumb)
		}

	}

	private fun animateSwitchTransition(thumbView: View) { // refactor to support any case not just one
		val layoutParams = thumbView.layoutParams as FrameLayout.LayoutParams
		layoutParams.gravity =
			if (isSwitchOn) Gravity.END or Gravity.CENTER_VERTICAL else Gravity.START or Gravity.CENTER_VERTICAL
		thumbView.layoutParams = layoutParams

		val targetGravity =
			if (layoutParams.gravity == (Gravity.START or Gravity.CENTER_VERTICAL)) {
				isSwitchOn = true
				Gravity.END or Gravity.CENTER_VERTICAL
			} else {
				isSwitchOn = false
				Gravity.START or Gravity.CENTER_VERTICAL
			}

		val animator: ViewPropertyAnimator = thumbView.animate()
			.setInterpolator(AccelerateInterpolator())
			.setDuration(300)

		animator.translationXBy(
			if (targetGravity == (Gravity.END or Gravity.CENTER_VERTICAL)) thumbView.width.toFloat() else -thumbView.width.toFloat()
		)
		animator.withEndAction {
			layoutParams.gravity = targetGravity
			thumbView.layoutParams = layoutParams
			thumbView.translationX = 0f
		}

		animator.start()
	}

}