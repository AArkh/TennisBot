package tennis.bot.mobile.feed.game

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import tennis.bot.mobile.R
import tennis.bot.mobile.core.CoreAdapter
import tennis.bot.mobile.databinding.FeedPostTwoMatchRequestBinding
import tennis.bot.mobile.feed.activityfeed.MatchRequestPostItem
import tennis.bot.mobile.feed.activityfeed.MatchRequestPostItemViewHolder
import tennis.bot.mobile.feed.activityfeed.bindInfoPanel
import tennis.bot.mobile.feed.activityfeed.showPlayerPhoto
import tennis.bot.mobile.utils.formatDateForFeed
import tennis.bot.mobile.utils.formatDateForMatchPostItem
import javax.inject.Inject

class GameAdapter@Inject constructor(): CoreAdapter<MatchRequestPostItemViewHolder>() {

	override fun onBindViewHolder(holder: MatchRequestPostItemViewHolder, item: Any) {
		bindGameRequestItem(item, holder)
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchRequestPostItemViewHolder {
		val binding = FeedPostTwoMatchRequestBinding.inflate(LayoutInflater.from(parent.context), parent, false)
		return MatchRequestPostItemViewHolder(binding)
	}

	private fun bindGameRequestItem(item: Any,holder: MatchRequestPostItemViewHolder) {
		val matchRequestItem = item as? MatchRequestPostItem ?: throw IllegalArgumentException("Item must be MatchRequestPostItem")
		val formattedMatchDate = matchRequestItem.matchDate?.let { formatDateForMatchPostItem(it) }
		val context = holder.binding.postType.context

		holder.binding.playerPhoto.showPlayerPhoto(matchRequestItem.playerPhoto, holder.binding.itemPicture)
		holder.binding.nameSurname.text = matchRequestItem.playerName
		holder.binding.locationSubTitle.text = matchRequestItem.locationSubTitle

		if (matchRequestItem.isOwned == true) {
			holder.binding.postType.setTextColor(context.getColor(R.color.tb_red_new))
			holder.binding.postType.text = context.getString(R.string.my_request)
		} else if (matchRequestItem.isResponsed == true) {
			holder.binding.postType.text = context.getString(R.string.my_response)
		} else {
			holder.binding.postType.setTextColor(context.getColor(R.color.tb_gray_dark))
			holder.binding.postType.text = context.getString(R.string.post_type_2).substringBefore(" ")
		}

		holder.bindInfoPanel(formattedMatchDate, matchRequestItem)
		holder.binding.requestComment.text = matchRequestItem.comment
		holder.binding.date.text = matchRequestItem.addedAt?.let { formatDateForFeed(it, holder.binding.date.context) }
		holder.binding.likeButton.isVisible = false
	}
}