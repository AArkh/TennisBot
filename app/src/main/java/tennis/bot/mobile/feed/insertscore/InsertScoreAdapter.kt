package tennis.bot.mobile.feed.insertscore

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import tennis.bot.mobile.R
import tennis.bot.mobile.core.CoreAdapter
import tennis.bot.mobile.core.CoreUtilsItem
import tennis.bot.mobile.databinding.RecyclerInsertSetItemBinding
import javax.inject.Inject

class InsertScoreAdapter @Inject constructor(): CoreAdapter<InsertSetItemViewHolder>() {
	var clickListener: ((item: Int) -> Unit)? = null

	override fun onBindViewHolder(holder: InsertSetItemViewHolder, item: Any) {
		val tennisSetItem = item as? TennisSetItem ?: throw IllegalArgumentException("Item must be TennisSetItem")

		holder.binding.title.text = holder.binding.title.context.getString(R.string.insert_score_set_item_title, tennisSetItem.setNumber)
		holder.binding.setScore.text = tennisSetItem.score

		holder.binding.setScore.setOnClickListener {
			clickListener?.invoke(holder.bindingAdapterPosition)
		}
		holder.binding.clearButton.setOnClickListener {
			clickListener?.invoke(-(holder.bindingAdapterPosition + 1)) // will treat it like a 'delete' order // added +1 because -0 is not a thing
		}
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InsertSetItemViewHolder {
		val binding = RecyclerInsertSetItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
		return InsertSetItemViewHolder(binding)
	}
}

class InsertSetItemViewHolder(
	val binding: RecyclerInsertSetItemBinding
) : RecyclerView.ViewHolder(binding.root)

data class TennisSetItem(
	val setNumber: Int,
	val score: String,
): CoreUtilsItem()