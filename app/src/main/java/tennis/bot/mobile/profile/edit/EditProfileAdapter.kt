package tennis.bot.mobile.profile.edit

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import tennis.bot.mobile.core.CoreAdapter
import tennis.bot.mobile.core.CoreUtilsItem
import tennis.bot.mobile.databinding.RecyclerEditProfileItemBinding
import javax.inject.Inject

class EditProfileAdapter @Inject constructor(): CoreAdapter<EditProfileItemViewHolder>() {
	var clickListener: ((item: String) -> Unit)? = null

	override fun onBindViewHolder(holder: EditProfileItemViewHolder, item: Any) {
		val editProfileItem = item as? EditProfileItem ?: throw IllegalArgumentException("Item must be EditProfileItem")

		holder.binding.icon.setImageResource(editProfileItem.icon)
		holder.binding.title.text = editProfileItem.title
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EditProfileItemViewHolder {
		val binding = RecyclerEditProfileItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
		return EditProfileItemViewHolder(binding)
	}
}

class EditProfileItemViewHolder(
	val binding: RecyclerEditProfileItemBinding
) : RecyclerView.ViewHolder(binding.root)

data class EditProfileItem(
	val icon: Int,
	val title: String
): CoreUtilsItem()