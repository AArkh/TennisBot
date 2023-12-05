package tennis.bot.mobile.onboarding.survey

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import tennis.bot.mobile.R
import tennis.bot.mobile.core.CoreAdapter
import tennis.bot.mobile.core.CoreUtilsItem
import tennis.bot.mobile.databinding.PagerSurveyItemBinding
import javax.inject.Inject

class SurveyAdapter @Inject constructor(): CoreAdapter<SurveyItemViewHolder>() {
	var clickListener: ((item: String) -> Unit)? = null // fixme вместо строк сразу передавать id: Int как для бэка нужно

	override fun onBindViewHolder(holder: SurveyItemViewHolder, item: Any) {
		val surveyItem = item as? SurveyItem ?: throw IllegalArgumentException("Item must be LoginProposalImage")
		holder.binding.option1Title.text = surveyItem.option1
		holder.binding.option2Title.text = surveyItem.option2
		holder.binding.option3Title.text = surveyItem.option3
		holder.binding.option4Title.text = surveyItem.option4
		holder.binding.sideNoteTitle.text = surveyItem.sideNoteTitle
		holder.binding.sideNoteText.text = surveyItem.sideNoteText

		holder.binding.option1.setBackgroundResource(R.drawable.survey_option_outline)
		holder.binding.option2.setBackgroundResource(R.drawable.survey_option_outline)
		holder.binding.option3.setBackgroundResource(R.drawable.survey_option_outline)
		holder.binding.option4.setBackgroundResource(R.drawable.survey_option_outline)
//		when (surveyItem.itemSelectedIndex) { // fixme тот же самый индекс, что и в clickListener кидаем
//			1 -> holder.binding.option1.setBackgroundResource(R.drawable.survey_option_outline_picked)
//			2 -> holder.binding.option2.setBackgroundResource(R.drawable.survey_option_outline_picked)
//			3 -> holder.binding.option3.setBackgroundResource(R.drawable.survey_option_outline_picked)
//			4 -> holder.binding.option4.setBackgroundResource(R.drawable.survey_option_outline_picked)
//		}

		if (surveyItem.isTwoOptions) {
			holder.binding.mainPagerLayout2.visibility = View.GONE
		}
		holder.binding.option1.setOnClickListener {
			clickListener?.invoke(item.option1)
			Log.d("ViewPager", "option1 is picked")
		}
		holder.binding.option2.setOnClickListener {
			clickListener?.invoke(item.option2)
			Log.d("ViewPager", "option2 is picked")
		}
		holder.binding.option3.setOnClickListener {
//			clickListener?.invoke(item.option3)
			Log.d("ViewPager", "option3 is picked")
		}
		holder.binding.option4.setOnClickListener {
//			clickListener?.invoke(item.option4)
			Log.d("ViewPager", "option4 is picked")
		}
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SurveyItemViewHolder {
		val binding = PagerSurveyItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
		return SurveyItemViewHolder(binding)
	}
}

class SurveyItemViewHolder(
	val binding: PagerSurveyItemBinding
) : RecyclerView.ViewHolder(binding.root)

data class SurveyItem(
	val option1: String,
	val option2: String,
	val option3: String? = null,
	val option4: String? = null,
	val sideNoteTitle: String,
	val sideNoteText: String,
	var isTwoOptions: Boolean = false
): CoreUtilsItem()

