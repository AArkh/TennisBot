package tennis.bot.mobile.profile.editgamedata

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import tennis.bot.mobile.core.CoreAdapter
import tennis.bot.mobile.databinding.RecyclerGamedataItemBinding
import tennis.bot.mobile.onboarding.survey.SurveyResultItem
import javax.inject.Inject

class EditGameDataAdapter @Inject constructor(): CoreAdapter<EditGameDataItemViewHolder>() {
	var clickListener: ((item: Int) -> Unit)? = null

	override fun onBindViewHolder(holder: EditGameDataItemViewHolder, item: Any) {
		val editGameDataItem = item as? SurveyResultItem ?: throw IllegalArgumentException("Item must be SurveyResultItem")

		holder.binding.title.text = editGameDataItem.resultTitle
		holder.binding.value.text = editGameDataItem.resultOption

		holder.binding.root.setOnClickListener {
			clickListener?.invoke(holder.bindingAdapterPosition)
		}
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EditGameDataItemViewHolder {
		val binding = RecyclerGamedataItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
		return EditGameDataItemViewHolder(binding)
	}
}

class EditGameDataItemViewHolder(
	val binding: RecyclerGamedataItemBinding
) : RecyclerView.ViewHolder(binding.root)