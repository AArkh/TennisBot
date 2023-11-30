package tennis.bot.mobile.onboarding.survey

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import tennis.bot.mobile.R
import tennis.bot.mobile.core.CoreAdapter
import tennis.bot.mobile.core.CoreUtilsItem
import tennis.bot.mobile.databinding.PagerSurveyItemBinding
import javax.inject.Inject

class SurveyAdapter @Inject constructor(): CoreAdapter<SurveyItemViewHolder>() {
	var clickListener: ((item: String) -> Unit)? = null

	override fun onBindViewHolder(holder: SurveyItemViewHolder, item: Any) {
		val surveyOptionsItem = item as? SurveyOptionsItem ?: throw IllegalArgumentException("Item must be LoginProposalImage")
		holder.binding.option1Title.text = surveyOptionsItem.option1
		holder.binding.option2Title.text = surveyOptionsItem.option2
		holder.binding.option3Title.text = surveyOptionsItem.option3
		holder.binding.option4Title.text = surveyOptionsItem.option4
		holder.binding.option1.setOnClickListener {
			clickListener?.invoke(item.option1)
			holder.binding.option1.setBackgroundResource(R.drawable.survey_option_outline_picked)
			holder.binding.option2.setBackgroundResource(R.drawable.survey_option_outline)
			holder.binding.option3.setBackgroundResource(R.drawable.survey_option_outline)
			holder.binding.option4.setBackgroundResource(R.drawable.survey_option_outline)
			Log.d("ViewPager", "option1 is picked")
		}
		holder.binding.option2.setOnClickListener {
			clickListener?.invoke(item.option2)
			holder.binding.option2.setBackgroundResource(R.drawable.survey_option_outline_picked)
			holder.binding.option1.setBackgroundResource(R.drawable.survey_option_outline)
			holder.binding.option3.setBackgroundResource(R.drawable.survey_option_outline)
			holder.binding.option4.setBackgroundResource(R.drawable.survey_option_outline)
			Log.d("ViewPager", "option2 is picked")
		}
		holder.binding.option3.setOnClickListener {
			clickListener?.invoke(item.option3)
			holder.binding.option3.setBackgroundResource(R.drawable.survey_option_outline_picked)
			holder.binding.option2.setBackgroundResource(R.drawable.survey_option_outline)
			holder.binding.option1.setBackgroundResource(R.drawable.survey_option_outline)
			holder.binding.option4.setBackgroundResource(R.drawable.survey_option_outline)
			Log.d("ViewPager", "option3 is picked")
		}
		holder.binding.option4.setOnClickListener {
			clickListener?.invoke(item.option4)
			holder.binding.option4.setBackgroundResource(R.drawable.survey_option_outline_picked)
			holder.binding.option2.setBackgroundResource(R.drawable.survey_option_outline)
			holder.binding.option3.setBackgroundResource(R.drawable.survey_option_outline)
			holder.binding.option1.setBackgroundResource(R.drawable.survey_option_outline)
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

data class SurveyOptionsItem(
	val option1: String,
	val option2: String,
	val option3: String,
	val option4: String,
	var isSelected: Boolean = false
): CoreUtilsItem()

