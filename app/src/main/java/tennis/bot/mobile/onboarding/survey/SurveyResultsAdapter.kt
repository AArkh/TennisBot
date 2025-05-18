package tennis.bot.mobile.onboarding.survey

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import tennis.bot.mobile.core.CoreAdapter
import tennis.bot.mobile.core.CoreUtilsItem
import tennis.bot.mobile.databinding.RecyclerSurveyResultsItemBinding
import javax.inject.Inject

open class SurveyResultsAdapter @Inject constructor(): CoreAdapter<SurveyResultItemViewHolder>() {

	override fun onBindViewHolder(holder: SurveyResultItemViewHolder, item: Any) {
		val surveyResultItem = item as? SurveyResultItem ?: throw IllegalArgumentException("Item must be SurveyResultItem")
		holder.binding.resultTitle.text = surveyResultItem.resultTitle
		holder.binding.resultOption.text = surveyResultItem.resultOption

		if (surveyResultItem.noUnderline) {
			holder.binding.underline.visibility = View.GONE
		} else {
			holder.binding.underline.visibility = View.VISIBLE
		}
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SurveyResultItemViewHolder {
		val binding = RecyclerSurveyResultsItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
		return SurveyResultItemViewHolder(binding)
	}
}

class SurveyResultItemViewHolder(
	val binding: RecyclerSurveyResultsItemBinding
) : RecyclerView.ViewHolder(binding.root)

data class SurveyResultItem(
	val resultTitle: String,
	val resultOption: String = "",
	val noUnderline: Boolean = false
): CoreUtilsItem()
