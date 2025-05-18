package tennis.bot.mobile.onboarding.survey

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import tennis.bot.mobile.R
import tennis.bot.mobile.core.CoreAdapter
import tennis.bot.mobile.databinding.PagerSurveyItemBinding
import javax.inject.Inject

class SurveyAdapter @Inject constructor(): CoreAdapter<SurveyItemViewHolder>() {
	var clickListener: ((id: Int, option: String) -> Unit)? = null

	override fun onBindViewHolder(holder: SurveyItemViewHolder, item: Any) {
		val surveyItem = item as? SurveyItem ?: throw IllegalArgumentException("Item must be SurveyItem")
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

		if (surveyItem.isTwoOptions) {
			holder.binding.mainPagerLayout2.visibility = View.GONE
			when (surveyItem.pickedOptionId) {
				0 -> holder.binding.option1.setBackgroundResource(R.drawable.survey_option_outline_picked)
				1 -> holder.binding.option2.setBackgroundResource(R.drawable.survey_option_outline_picked)
				}
		} else {
			holder.binding.mainPagerLayout2.visibility = View.VISIBLE
			when (surveyItem.pickedOptionId) {
				1 -> holder.binding.option1.setBackgroundResource(R.drawable.survey_option_outline_picked)
				2 -> holder.binding.option2.setBackgroundResource(R.drawable.survey_option_outline_picked)
				3 -> holder.binding.option3.setBackgroundResource(R.drawable.survey_option_outline_picked)
				4 -> holder.binding.option4.setBackgroundResource(R.drawable.survey_option_outline_picked)
			}
		}
		holder.binding.option1.setOnClickListener {
			if (surveyItem.isTwoOptions){
				clickListener?.invoke(0, holder.binding.option1Title.text.toString())
			} else {
				clickListener?.invoke(1, holder.binding.option1Title.text.toString())
			}
			Log.d("ViewPager", "option1 is picked")
		}
		holder.binding.option2.setOnClickListener {
			if (surveyItem.isTwoOptions){
				clickListener?.invoke(1, holder.binding.option2Title.text.toString())
			} else {
				clickListener?.invoke(2, holder.binding.option2Title.text.toString())
			}
			Log.d("ViewPager", "option2 is picked")
		}
		holder.binding.option3.setOnClickListener {
			clickListener?.invoke(3, holder.binding.option3Title.text.toString())
			Log.d("ViewPager", "option3 is picked")
		}
		holder.binding.option4.setOnClickListener {
			clickListener?.invoke(4, holder.binding.option4Title.text.toString())
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

