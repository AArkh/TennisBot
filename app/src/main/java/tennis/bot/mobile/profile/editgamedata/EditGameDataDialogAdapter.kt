package tennis.bot.mobile.profile.editgamedata

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import tennis.bot.mobile.core.CoreAdapter
import tennis.bot.mobile.core.CoreUtilsItem
import tennis.bot.mobile.databinding.RecyclerTextOnlyItemBinding
import javax.inject.Inject

class EditGameDataDialogAdapter @Inject constructor(): CoreAdapter<TextOnlyItemViewHolder>() {
	var clickListener: ((item: Int) -> Unit)? = null

	override fun onBindViewHolder(holder: TextOnlyItemViewHolder, item: Any) {
		val textOnlyItem = item as? TextOnlyItem ?: throw IllegalArgumentException("Item must be EditProfileItem")

		holder.binding.title.text = textOnlyItem.title
		if (textOnlyItem.isChecked) holder.binding.check.visibility = View.VISIBLE else holder.binding.check.visibility = View.GONE

		holder.binding.root.setOnClickListener {
			clickListener?.invoke(textOnlyItem.id)
		}
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TextOnlyItemViewHolder {
		val binding = RecyclerTextOnlyItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
		return TextOnlyItemViewHolder(binding)
	}
}

data class TextOnlyItem(
	val id: Int,
	val title: String?,
	val isChecked: Boolean = false
): CoreUtilsItem()

class TextOnlyItemViewHolder(
	val binding: RecyclerTextOnlyItemBinding
) : RecyclerView.ViewHolder(binding.root)