package tennis.bot.mobile.feed.game

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import tennis.bot.mobile.R
import tennis.bot.mobile.databinding.FeedPostOneNewPlayerBinding
import tennis.bot.mobile.feed.activityfeed.NewPlayerPostItemViewHolder
import tennis.bot.mobile.feed.activityfeed.showPlayerPhoto
import tennis.bot.mobile.feed.searchopponent.OpponentItem
import tennis.bot.mobile.feed.searchopponent.SearchOpponentsAdapter
import javax.inject.Inject

class PlayersAdapter@Inject constructor(): PagingDataAdapter<OpponentItem, NewPlayerPostItemViewHolder>(SearchOpponentsAdapter.OPPONENTS_COMPARATOR) {
	var clickListener: ((item: OpponentItem) -> Unit)? = null


	override fun onBindViewHolder(holder: NewPlayerPostItemViewHolder, position: Int) {
		getItem(position)?.let { opponentItem ->

			holder.binding.playerPhoto.showPlayerPhoto(opponentItem.profilePicture, holder.binding.itemPicture)
			holder.binding.nameSurname.text = opponentItem.nameSurname
			holder.binding.subTitle.isVisible = false //todo add location info here
			holder.binding.infoPanel.text = opponentItem.infoPanel

			if(opponentItem.isInvited == true) {
				holder.binding.postType.text = "Приглашен"
				holder.binding.postType.setTextColor(holder.binding.postType.context.getColor(R.color.tb_white))
				holder.binding.postType.setBackgroundResource(R.drawable.background_corners_10dp)
				holder.binding.postType.backgroundTintList = holder.binding.postType.context.getColorStateList(R.color.tb_primary_green)
			} else {
				holder.binding.postType.text = "Пригласить"
				holder.binding.postType.setTextColor(holder.binding.postType.context.getColor(R.color.tb_black))
				holder.binding.postType.setBackgroundResource(R.drawable.background_corners_10dp)
				holder.binding.postType.backgroundTintList = holder.binding.postType.context.getColorStateList(R.color.tb_gray_border_new)
			}

			holder.binding.likeButton.isVisible = false
			holder.binding.likeAnim.isVisible = false
			holder.binding.messageButton.isVisible = false
			holder.binding.date.isVisible = false
		}
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewPlayerPostItemViewHolder {
		val binding = FeedPostOneNewPlayerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
		return NewPlayerPostItemViewHolder(binding)
	}


}