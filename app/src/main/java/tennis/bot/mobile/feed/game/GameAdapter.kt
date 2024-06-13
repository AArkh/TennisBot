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

	var clickListener: ((command: String, id: Long) -> Unit)? = null

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
			holder.binding.optionsDots.isVisible = true
			holder.binding.optionsDots.setOnClickListener {
				clickListener?.invoke(REQUEST_OPTIONS_REQUEST, matchRequestItem.id)
			}
		} else if (matchRequestItem.isResponsed == true) {
			holder.binding.postType.text = context.getString(R.string.my_response)
			holder.binding.optionsDots.isVisible = true
			holder.binding.optionsDots.setOnClickListener {
				clickListener?.invoke(REQUEST_OPTIONS_RESPONSE, matchRequestItem.id)
			}
		} else {
			holder.binding.postType.setTextColor(context.getColor(R.color.tb_gray_dark))
			holder.binding.postType.text = context.getString(R.string.post_type_2).substringBefore(" ")
			holder.binding.optionsDots.isVisible = false
		}

		holder.bindInfoPanel(formattedMatchDate, matchRequestItem)
		holder.binding.requestComment.text = matchRequestItem.comment
		holder.binding.date.text = matchRequestItem.addedAt?.let { formatDateForFeed(it, holder.binding.date.context) }
		holder.binding.likeButton.isVisible = false

		holder.binding.root.setOnClickListener {
			clickListener?.invoke(REQUEST_RESPONSE, matchRequestItem.id)
		}
	}

	companion object {
		const val REQUEST_RESPONSE = "REQUEST_RESPONSE"
		const val REQUEST_OPTIONS_REQUEST = "REQUEST_OPTIONS_REQUEST"
		const val REQUEST_OPTIONS_RESPONSE = "REQUEST_OPTIONS_RESPONSE"
	}
}