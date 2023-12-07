package tennis.bot.mobile.onboarding.sport

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import tennis.bot.mobile.R
import tennis.bot.mobile.core.CoreAdapter
import tennis.bot.mobile.core.CoreUtilsItem
import tennis.bot.mobile.databinding.PagerSurveyItemBinding
import tennis.bot.mobile.databinding.RecyclerSportItemBinding
import javax.inject.Inject

class SportAdapter @Inject constructor(): CoreAdapter<SportItemViewHolder>() {
	var clickListener: ((item: SportItem) -> Unit)? = null

	override fun onBindViewHolder(holder: SportItemViewHolder, item: Any) {
		val sportItem = item as? SportItem ?: throw IllegalArgumentException("Item must be LoginProposalImage")
		holder.binding.title.text = sportItem.title

		holder.itemView.setOnClickListener {
			clickListener?.invoke(item)
		}
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SportItemViewHolder {
		val binding = RecyclerSportItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
		return SportItemViewHolder(binding)
	}
}

class SportItemViewHolder(
	val binding: RecyclerSportItemBinding
) : RecyclerView.ViewHolder(binding.root)

data class SportItem(
	val title: String,
): CoreUtilsItem()

