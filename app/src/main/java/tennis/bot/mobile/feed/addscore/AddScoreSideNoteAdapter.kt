package tennis.bot.mobile.feed.addscore

import android.view.ViewGroup
import androidx.core.view.marginStart
import androidx.core.view.setMargins
import tennis.bot.mobile.onboarding.survey.SurveyResultItemViewHolder
import tennis.bot.mobile.onboarding.survey.SurveyResultsAdapter
import tennis.bot.mobile.utils.dpToPx
import javax.inject.Inject

class AddScoreSideNoteAdapter @Inject constructor(): SurveyResultsAdapter() {

	override fun onBindViewHolder(holder: SurveyResultItemViewHolder, position: Int) {
		super.onBindViewHolder(holder, position)
		val context = holder.binding.root.context
		val layoutParams = holder.binding.resultLayout.layoutParams as ViewGroup.MarginLayoutParams
		val underlineParams = holder.binding.underline.layoutParams as ViewGroup.MarginLayoutParams

		holder.binding.resultTitle.textSize = 12f
		holder.binding.resultOption.textSize = 12f
		holder.binding.resultLayout.setPadding(0, context.dpToPx(16), 0, context.dpToPx(16))
		layoutParams.setMargins(context.dpToPx(16), 0, context.dpToPx(16), 0)
		holder.binding.resultLayout.layoutParams = layoutParams
		underlineParams.setMargins(context.dpToPx(16), 0, context.dpToPx(16), 0)
		holder.binding.underline.layoutParams = underlineParams
	}
}