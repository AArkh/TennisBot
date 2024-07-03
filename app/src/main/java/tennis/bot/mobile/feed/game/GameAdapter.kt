package tennis.bot.mobile.feed.game

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import tennis.bot.mobile.R
import tennis.bot.mobile.databinding.FeedPostTwoMatchRequestBinding
import tennis.bot.mobile.databinding.RecyclerAcceptedGameItemBinding
import tennis.bot.mobile.databinding.RecyclerEmptyItemBinding
import tennis.bot.mobile.feed.activityfeed.AcceptedGameItem
import tennis.bot.mobile.feed.activityfeed.AcceptedGameItemViewHolder
import tennis.bot.mobile.feed.activityfeed.FeedAdapter
import tennis.bot.mobile.feed.activityfeed.FeedAdapter.Companion.FEED_COMPARATOR
import tennis.bot.mobile.feed.activityfeed.FeedSealedClass
import tennis.bot.mobile.feed.activityfeed.MatchRequestPostItem
import tennis.bot.mobile.feed.activityfeed.MatchRequestPostItemViewHolder
import tennis.bot.mobile.feed.activityfeed.bindInfoPanel
import tennis.bot.mobile.feed.activityfeed.showPlayerPhoto
import tennis.bot.mobile.feed.searchopponent.OpponentItem
import tennis.bot.mobile.profile.account.EmptyItemViewHolder
import tennis.bot.mobile.utils.dpToPx
import tennis.bot.mobile.utils.formatDateForFeed
import tennis.bot.mobile.utils.view.AvatarImage
import javax.inject.Inject

class GameAdapter@Inject constructor(): PagingDataAdapter<FeedSealedClass, RecyclerView.ViewHolder>(FEED_COMPARATOR) {

	var clickListener: ((command: String, id: Long) -> Unit)? = null
	var insertScoreCallback: ((opponentsList: Array<OpponentItem>) -> Unit)? = null
	companion object {
		const val REQUEST_RESPONSE = "REQUEST_RESPONSE"
		const val REQUEST_OPTIONS_REQUEST = "REQUEST_OPTIONS_REQUEST"
		const val REQUEST_OPTIONS_RESPONSE = "REQUEST_OPTIONS_RESPONSE"
		const val ACCEPTED_GAME_INSERT_SCORE = "ACCEPTED_GAME_INSERT_SCORE"
		const val ACCEPTED_GAME_ITEM = 1
		private const val ACCEPTED_GAME_IMAGE_SIZE = 48
	}

	override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
		getItem(position)?.let { item ->
			when(holder) {
				is AcceptedGameItemViewHolder -> bindAcceptedGameItem(item, holder)
				is MatchRequestPostItemViewHolder -> bindGameRequestItem(item, holder)
			}
		}
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
		return when (viewType) {
			ACCEPTED_GAME_ITEM -> {
				val binding = RecyclerAcceptedGameItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
				AcceptedGameItemViewHolder(binding)
			}

			FeedAdapter.MATCH_REQUEST -> {
				val binding = FeedPostTwoMatchRequestBinding.inflate(LayoutInflater.from(parent.context), parent, false)
				return MatchRequestPostItemViewHolder(binding)
			}

			else -> {
				val binding = RecyclerEmptyItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
				EmptyItemViewHolder(binding)
			}
		}
	}

	override fun getItemViewType(position: Int): Int {
		return when (getItem(position)) {
			is AcceptedGameItem -> ACCEPTED_GAME_ITEM
			is MatchRequestPostItem -> FeedAdapter.MATCH_REQUEST
			else -> FeedAdapter.OTHER
		}
	}

	private fun bindGameRequestItem(item: Any, holder: MatchRequestPostItemViewHolder) {
		val matchRequestItem = item as? MatchRequestPostItem ?: throw IllegalArgumentException("Item must be MatchRequestPostItem")
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
			holder.binding.postType.setTextColor(context.getColor(R.color.tb_primary_green))
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

		holder.bindInfoPanel(matchRequestItem.matchDate, matchRequestItem)
		holder.binding.requestComment.text = matchRequestItem.comment
		holder.binding.date.text = matchRequestItem.addedAt?.let { formatDateForFeed(it, holder.binding.date.context) }
		holder.binding.likeButton.isVisible = false

		holder.binding.root.setOnClickListener {
			clickListener?.invoke(REQUEST_RESPONSE, matchRequestItem.id)
		}
	}

	private fun bindAcceptedGameItem(item: Any, holder: AcceptedGameItemViewHolder) {
		val acceptedGameItem = item as? AcceptedGameItem ?: throw IllegalArgumentException("Item must be AcceptedGameItem")
		val context = holder.binding.root.context

		holder.binding.itemId.text = acceptedGameItem.id.toString()

		holder.binding.playersLeftPhotos.setImage(AvatarImage(acceptedGameItem.player.profilePicture))
		holder.binding.playersLeftPhotos.drawableSize = context.dpToPx(ACCEPTED_GAME_IMAGE_SIZE)
		holder.binding.playersLeftNames.text = acceptedGameItem.player.nameSurname
		holder.binding.playersLeftInfo.text = acceptedGameItem.player.infoPanel

		holder.binding.playersRightPhotos.setImage(AvatarImage(acceptedGameItem.targetPlayer.profilePicture))
		holder.binding.playersRightPhotos.drawableSize = context.dpToPx(ACCEPTED_GAME_IMAGE_SIZE)
		holder.binding.playersRightNames.text = acceptedGameItem.targetPlayer.nameSurname
		holder.binding.playersRightInfo.text = acceptedGameItem.targetPlayer.infoPanel

		holder.binding.insertScoreButton.setOnClickListener {
			insertScoreCallback?.invoke(arrayOf(acceptedGameItem.player)) // prone to error since we don't know who's the main player and how's the opponent. todo discuss
		}
	}
}