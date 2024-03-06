package tennis.bot.mobile.feed.searchopponent

import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.core.view.setPadding
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import kotlinx.parcelize.Parcelize
import tennis.bot.mobile.R
import tennis.bot.mobile.databinding.RecyclerOpponentItemBinding
import tennis.bot.mobile.profile.account.AccountPageAdapter
import tennis.bot.mobile.profile.account.getDefaultDrawableResourceId
import javax.inject.Inject

class SearchOpponentsAdapter @Inject constructor(): PagingDataAdapter<OpponentItem, OpponentItemViewHolder>(OPPONENTS_COMPARATOR) {
	var clickListener: ((item: OpponentItem) -> Unit)? = null
	private var selectedItem = -1


	companion object {
		private val OPPONENTS_COMPARATOR = object : DiffUtil.ItemCallback<OpponentItem>() {
			override fun areItemsTheSame(oldItem: OpponentItem, newItem: OpponentItem): Boolean =
				oldItem.id == newItem.id

			override fun areContentsTheSame(oldItem: OpponentItem, newItem: OpponentItem): Boolean =
				oldItem == newItem
		}
	}

	fun clearOutlinePosition() {
		selectedItem = -1
	}

	override fun onBindViewHolder(holder: OpponentItemViewHolder, position: Int) {
		getItem(position)?.let { player ->
			holder.showPlayerPhoto(player.profilePicture)
			holder.binding.nameSurname.text = player.nameSurname
			holder.binding.infoPanel.text = player.infoPanel
			if (holder.absoluteAdapterPosition == selectedItem){
				holder.binding.root.foreground = getDrawable(holder.binding.root.context, R.drawable.survey_option_outline_picked)
			} else {
				holder.binding.root.foreground = null
			}

			holder.binding.root.setOnClickListener {
				if (holder.absoluteAdapterPosition == selectedItem) {
					selectedItem = -1
				} else {
					selectedItem = holder.absoluteAdapterPosition
					clickListener?.invoke(player)
				}
				notifyDataSetChanged() // more specific options doesn't provide the intended result
			}
		}
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OpponentItemViewHolder {
		val binding = RecyclerOpponentItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
		return OpponentItemViewHolder(binding)
	}
}

private fun OpponentItemViewHolder.showPlayerPhoto(profileImageUrl: String?) {
	if (profileImageUrl == null) {
		binding.itemPicture.visibility = View.GONE
		return
	}

	if (profileImageUrl.contains("default")) {
		val resourceId = getDefaultDrawableResourceId(binding.playerImage.context, profileImageUrl.removeSuffix(".png"))
		if (resourceId != null) binding.playerImage.load(resourceId)
		binding.playerPhoto.setPadding(0)
		binding.itemPicture.load(resourceId)
	} else {
		binding.playerImage.load(AccountPageAdapter.IMAGES_LINK + profileImageUrl) { crossfade(true) }
		binding.playerPhoto.setPadding(0)
		binding.itemPicture.load(AccountPageAdapter.IMAGES_LINK + profileImageUrl)
	}
}

class OpponentItemViewHolder (
	val binding: RecyclerOpponentItemBinding
) : RecyclerView.ViewHolder(binding.root)

@Parcelize
data class OpponentItem(
	val id: Long,
	val profilePicture: String?,
	val nameSurname: String,
	val infoPanel: String, // rating/doublesRating experience | games + string
	val isPicked: Boolean = false
): Parcelable