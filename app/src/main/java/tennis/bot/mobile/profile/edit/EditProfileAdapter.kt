package tennis.bot.mobile.profile.edit

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import tennis.bot.mobile.core.CoreAdapter
import tennis.bot.mobile.core.CoreUtilsItem
import tennis.bot.mobile.databinding.RecyclerEditProfileItemBinding
import javax.inject.Inject

class EditProfileAdapter @Inject constructor(): CoreAdapter<EditProfileItemViewHolder>() {
	var clickListener: ((item: String) -> Unit)? = null

	companion object {
		const val CHANGE_NAME = "CHANGE_NAME"
		const val CHANGE_BIRTHDAY = "CHANGE_BIRTHDAY"
		const val CHANGE_LOCATION = "CHANGE_LOCATION"
		const val CHANGE_PHONE = "CHANGE_PHONE"
		const val CHANGE_TELEGRAM = "CHANGE_TELEGRAM"

	}

	override fun onBindViewHolder(holder: EditProfileItemViewHolder, item: Any) {
		val editProfileItem = item as? EditProfileItem ?: throw IllegalArgumentException("Item must be EditProfileItem")

		holder.binding.icon.setImageResource(editProfileItem.icon)
		holder.binding.title.text = editProfileItem.title

		if (editProfileItem.noUnderline) {
			holder.binding.underline.visibility = View.INVISIBLE
		} else {
			holder.binding.underline.visibility = View.VISIBLE
		}

		holder.binding.root.setOnClickListener {
			clickListener?.invoke(holder.binding.title.text.toString()) // figure out whether to find a unique identifier or add a vield with one
		}
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
	val title: String,
	val noUnderline: Boolean = false
): CoreUtilsItem()