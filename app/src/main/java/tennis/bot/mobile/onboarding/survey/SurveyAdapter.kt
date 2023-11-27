package tennis.bot.mobile.onboarding.survey

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import tennis.bot.mobile.core.CoreAdapter
import tennis.bot.mobile.core.CoreUtilsItem
import tennis.bot.mobile.databinding.RecyclerSurveyItemBinding
import javax.inject.Inject

class SurveyAdapter @Inject constructor(): CoreAdapter<SurveyItemViewHolder>() {
	var clickListener: ((item: SurveyItem) -> Unit)? = null

	override fun onBindViewHolder(holder: SurveyItemViewHolder, item: Any) {
		val surveyItem = item as? SurveyItem ?: throw IllegalArgumentException("Item must be LoginProposalImage")
		holder.binding.title.text = surveyItem.title
		holder.itemView.setOnClickListener {
			clickListener?.invoke(item)
		}
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SurveyItemViewHolder {
		val binding = RecyclerSurveyItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
		return SurveyItemViewHolder(binding)
	}
}

class SurveyItemViewHolder(
	val binding: RecyclerSurveyItemBinding
) : RecyclerView.ViewHolder(binding.root)

data class SurveyItem(
	val title: String,
	val isSelected: Boolean = false
): CoreUtilsItem()

