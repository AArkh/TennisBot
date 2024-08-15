package tennis.bot.mobile.feed.requestcreation

import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import tennis.bot.mobile.R
import tennis.bot.mobile.core.CoreAdapter
import tennis.bot.mobile.core.CoreUtilsItem
import tennis.bot.mobile.databinding.RecyclerGamedataItemBinding
import tennis.bot.mobile.databinding.RecyclerGaugeItemBinding
import tennis.bot.mobile.onboarding.survey.SurveyResultItem
import tennis.bot.mobile.profile.editgamedata.EditGameDataItemViewHolder
import tennis.bot.mobile.utils.dpToPx
import javax.inject.Inject



class RequestAdapter @Inject constructor(): CoreAdapter<RecyclerView.ViewHolder>() {

	companion object {
		private const val OTHER = 0
		const val REGULAR_ITEM = 1
		const val RATING_SLIDER = 2
	}
	var clickListener: ((position: Int) -> Unit)? = null
	var currentRating: ((rating: Int) -> Unit)? = null

	override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Any) {
		when(holder) {
			is EditGameDataItemViewHolder -> { bindRegularItem(holder, item) }
			is GaugeItemViewHolder -> { bindGaugeAndCommentItem(holder, item) }
		}
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
		return when (viewType) {
			REGULAR_ITEM -> {
				val binding = RecyclerGamedataItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
				EditGameDataItemViewHolder(binding)
			}
			RATING_SLIDER -> {
				val binding = RecyclerGaugeItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
				GaugeItemViewHolder(binding)
			}
			else -> throw IllegalArgumentException("Unknown view type: $viewType")
		}
	}

	override fun getItemViewType(position: Int): Int {
		return when (items[position]) {
			is SurveyResultItem -> REGULAR_ITEM
			is GaugeAndCommentItem -> RATING_SLIDER
			else -> OTHER
		}
	}

	private fun bindRegularItem(holder: EditGameDataItemViewHolder, item: Any) {
		val regularItem = item as? SurveyResultItem ?: throw IllegalArgumentException("Item must be SurveyResultItem")
		val layoutParams = holder.binding.root.layoutParams as ViewGroup.MarginLayoutParams
		val margin = holder.binding.title.context.dpToPx(16)

		holder.binding.title.text = regularItem.resultTitle
		holder.binding.value.text = regularItem.resultOption
		layoutParams.setMargins(margin, margin, margin, 0)

		holder.binding.root.setOnClickListener {
			clickListener?.invoke(holder.bindingAdapterPosition)
		}
	}

	private fun bindGaugeAndCommentItem(holder: GaugeItemViewHolder, item: Any) {
		val gaugeAndCommentItem = item as? GaugeAndCommentItem ?: throw IllegalArgumentException("Item must be GaugeAndCommentItem")
		val context = holder.binding.commentText.context

		holder.binding.gaugeItem.gaugeView.rating = gaugeAndCommentItem.rating
		holder.binding.gaugeItem.lowerValues.text = formatTextToTwoColors(context, gaugeAndCommentItem.lowerValues)
		holder.binding.gaugeItem.recommendedValues.text = formatTextToTwoColors(context, gaugeAndCommentItem.recommendedValues)
		holder.binding.gaugeItem.higherValues.text = formatTextToTwoColors(context, gaugeAndCommentItem.higherValues)
		holder.binding.commentText.setText(gaugeAndCommentItem.comment)

		holder.binding.gaugeItem.gaugeView.setOnIndicatorChangeListener(object : OnIndicatorChangeListener {
			override fun onIndicatorChanged(newRating: Int) {
				currentRating?.invoke(holder.binding.gaugeItem.gaugeView.currentRating)
			}
		})
	}

	private fun formatTextToTwoColors(context: Context, formattedText: String): SpannableString {
		val spannableString = SpannableString(formattedText)
		val startIndex = formattedText.indexOf("\n")

		if (startIndex != -1) {
			val endIndex = formattedText.lastIndex + 1

			if (endIndex != -1) {
				val color = context.getColor(R.color.tb_gray_active)
				spannableString.setSpan(
					ForegroundColorSpan(color),
					startIndex,
					endIndex,
					Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
				)
			}
		}
		return spannableString
	}
}

class GaugeItemViewHolder(
	val binding: RecyclerGaugeItemBinding
) : RecyclerView.ViewHolder(binding.root)

data class GaugeAndCommentItem (
	val rating: Int,
	val lowerValues: String,
	val recommendedValues: String,
	val higherValues: String,
	val comment: String
): CoreUtilsItem()



