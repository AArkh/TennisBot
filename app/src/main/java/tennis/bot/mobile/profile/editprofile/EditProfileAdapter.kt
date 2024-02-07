package tennis.bot.mobile.profile.editprofile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import tennis.bot.mobile.core.CoreAdapter
import tennis.bot.mobile.core.CoreUtilsItem
import tennis.bot.mobile.databinding.RecyclerEditProfileItemBinding
import javax.inject.Inject

open class EditProfileAdapter @Inject constructor(): CoreAdapter<EditProfileItemViewHolder>() {
	var clickListener: ((item: Int) -> Unit)? = null

	companion object {
		const val CHANGE_NAME_INDEX = 0
		const val CHANGE_BIRTHDAY_INDEX = 1
		const val CHANGE_LOCATION_INDEX = 2
		const val CHANGE_PHONE_INDEX = 3
		const val CHANGE_TELEGRAM_INDEX = 4

	}

	override fun onBindViewHolder(holder: EditProfileItemViewHolder, item: Any) {
		val editProfileItem = item as? EditProfileItem ?: throw IllegalArgumentException("Item must be EditProfileItem")

		holder.binding.icon.setImageResource(editProfileItem.icon)
		holder.binding.title.text = editProfileItem.title

		if(editProfileItem.isRadioButton) {

		}

		if (editProfileItem.noUnderline) {
			holder.binding.underline.visibility = View.INVISIBLE
		} else {
			holder.binding.underline.visibility = View.VISIBLE
		}

		holder.binding.root.setOnClickListener {
			clickListener?.invoke(holder.bindingAdapterPosition)
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
	val title: String?,
	val isRadioButton: Boolean = false,
	val noUnderline: Boolean = false
): CoreUtilsItem()