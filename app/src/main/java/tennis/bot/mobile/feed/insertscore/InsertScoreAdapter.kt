package tennis.bot.mobile.feed.insertscore

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import tennis.bot.mobile.R
import tennis.bot.mobile.core.CoreAdapter
import tennis.bot.mobile.core.CoreUtilsItem
import tennis.bot.mobile.databinding.RecyclerInsertSetItemBinding
import tennis.bot.mobile.utils.dpToPx
import javax.inject.Inject

class InsertScoreAdapter @Inject constructor(): CoreAdapter<InsertSetItemViewHolder>() {
	var clickListener: ((position: Int, value: String, isSuperTieBreak: Boolean) -> Unit)? = null

	override fun onBindViewHolder(holder: InsertSetItemViewHolder, item: Any) {
		val tennisSetItem = item as? TennisSetItem ?: throw IllegalArgumentException("Item must be TennisSetItem")

		holder.binding.setScore.text = tennisSetItem.score

		if (!tennisSetItem.isSuperTieBreak) {
			holder.binding.title.text = holder.binding.title.context.getString(R.string.insert_score_set_item_title, holder.bindingAdapterPosition + 1)
		} else {
			holder.binding.title.text = holder.binding.title.context.getString(R.string.super_tie_break)
		}

		if (tennisSetItem.isActive) { // last
			holder.binding.underline.visibility = View.INVISIBLE
			holder.binding.setScore.isEnabled = true
			holder.binding.setScore.setPaddingRelative(
				holder.binding.setScore.context.dpToPx(14),
				holder.binding.setScore.context.dpToPx(6),
				holder.binding.setScore.context.dpToPx(14),
				holder.binding.setScore.context.dpToPx(6)
			)

			holder.binding.setScore.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.chevron_right_wide, 0)
			holder.binding.setScore.setOnClickListener {
				clickListener?.invoke(holder.bindingAdapterPosition, holder.binding.setScore.text.toString(), tennisSetItem.isSuperTieBreak)
			}
		} else {
			holder.binding.underline.visibility = View.VISIBLE
			holder.binding.setScore.isEnabled = false
			holder.binding.setScore.setPaddingRelative(
				holder.binding.setScore.context.dpToPx(14),
				holder.binding.setScore.context.dpToPx(6),
				holder.binding.setScore.context.dpToPx(9),
				holder.binding.setScore.context.dpToPx(6)
			)
			holder.binding.setScore.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
		}

		holder.binding.clearButton.setOnClickListener {
			clickListener?.invoke(-(holder.bindingAdapterPosition + 1), holder.binding.setScore.text.toString(), tennisSetItem.isSuperTieBreak) // will treat it like a 'delete' order // added +1 because -0 is not a thing
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
	val isSuperTieBreak: Boolean = false,
	val isActive: Boolean
): CoreUtilsItem()