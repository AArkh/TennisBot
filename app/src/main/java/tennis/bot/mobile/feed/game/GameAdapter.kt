package tennis.bot.mobile.feed.game

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import tennis.bot.mobile.R
import tennis.bot.mobile.databinding.RecyclerAcceptedGameItemBinding
import tennis.bot.mobile.databinding.RecyclerEmptyItemBinding
import tennis.bot.mobile.databinding.RecyclerGameRequestResponseBinding
import tennis.bot.mobile.feed.activityfeed.AcceptedGameItem
import tennis.bot.mobile.feed.activityfeed.AcceptedGameItemViewHolder
import tennis.bot.mobile.feed.activityfeed.FeedAdapter
import tennis.bot.mobile.feed.activityfeed.FeedAdapter.Companion.FEED_COMPARATOR
import tennis.bot.mobile.feed.activityfeed.FeedSealedClass
import tennis.bot.mobile.feed.activityfeed.MatchRequestPostItem
import tennis.bot.mobile.feed.activityfeed.showPlayerPhoto
import tennis.bot.mobile.feed.searchopponent.OpponentItem
import tennis.bot.mobile.profile.account.EmptyItemViewHolder
import tennis.bot.mobile.utils.FormattedDate
import tennis.bot.mobile.utils.dpToPx
import tennis.bot.mobile.utils.view.AvatarImage
import javax.inject.Inject

class GameAdapter@Inject constructor(): PagingDataAdapter<FeedSealedClass, RecyclerView.ViewHolder>(FEED_COMPARATOR) {

	var clickListener: ((command: String, id: Long?, targetPlayerId: Long?, isOwned: Boolean?) -> Unit)? = null
	var insertScoreCallback: ((opponentsList: Array<OpponentItem>) -> Unit)? = null
	companion object {
		const val REQUEST_RESPONSE = "REQUEST_RESPONSE"
		const val REQUEST_RESPONSE_ACCEPT = "REQUEST_RESPONSE_ACCEPT"
		const val REQUEST_RESPONSE_DECLINE = "REQUEST_RESPONSE_DECLINE"
		const val REQUEST_OPTIONS_REQUEST = "REQUEST_OPTIONS_REQUEST"
		const val REQUEST_OPTIONS_RESPONSE = "REQUEST_OPTIONS_RESPONSE"
		const val ACCEPTED_GAME_ITEM = 1
		const val GAME_REQUEST_ITEM = 2
		private const val ACCEPTED_GAME_IMAGE_SIZE = 48
	}

	override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
		getItem(position)?.let { item ->
			when(holder) {
				is AcceptedGameItemViewHolder -> bindAcceptedGameItem(item, holder)
				is GameRequestResponseItemViewHolder -> bindGameRequestItem(item, holder)
			}
		}
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
		return when (viewType) {
			ACCEPTED_GAME_ITEM -> {
				val binding = RecyclerAcceptedGameItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
				AcceptedGameItemViewHolder(binding)
			}

			GAME_REQUEST_ITEM -> {
				val binding = RecyclerGameRequestResponseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
				return GameRequestResponseItemViewHolder(binding)
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

	private fun bindGameRequestItem(item: Any, holder: GameRequestResponseItemViewHolder) {
		val matchRequestItem = item as? MatchRequestPostItem ?: error("Item must be MatchRequestPostItem")
		val context = holder.binding.postType.context

		with(holder.binding) {
			playerPhoto.showPlayerPhoto(matchRequestItem.playerPhoto, itemPicture)
			nameSurname.text = matchRequestItem.playerName
			locationSubTitle.text = matchRequestItem.locationSubTitle

			postType.text = when {
				matchRequestItem.targetPlayerId != null -> context.getString(if (matchRequestItem.isOwned == true) R.string.my_invite else R.string.invite)
				matchRequestItem.isResponsed == true -> context.getString(R.string.my_response)
				matchRequestItem.gameOrderId == matchRequestItem.playerId -> context.getString(R.string.my_request)
				matchRequestItem.isOwned == true -> context.getString(R.string.request_response)
				else -> context.getString(R.string.post_type_2).substringBefore(" ")
			}

			postType.setTextColor(context.getColor(
				when (postType.text) {
					context.getString(R.string.request_response),
					context.getString(R.string.invite),
					context.getString(R.string.my_response) -> R.color.tb_primary_green
					else -> if (matchRequestItem.isOwned == true) R.color.tb_red_new else R.color.tb_gray_dark
				}
			))

			acceptButton.isVisible = postType.text in listOf(context.getString(R.string.request_response), context.getString(R.string.invite))
			acceptButton.setOnClickListener {
				clickListener?.invoke(REQUEST_RESPONSE_ACCEPT, matchRequestItem.id, matchRequestItem.playerId, false)
			}

			declineButton.isVisible = acceptButton.isVisible
			declineButton.setOnClickListener {
				clickListener?.invoke(REQUEST_RESPONSE_DECLINE, matchRequestItem.id, matchRequestItem.playerId, false)
			}

			optionsDots.isVisible = !acceptButton.isVisible
			optionsDots.setOnClickListener {
				clickListener?.invoke(
					if (matchRequestItem.isOwned == true) REQUEST_OPTIONS_REQUEST else REQUEST_OPTIONS_RESPONSE,
					matchRequestItem.id, null, null
				)
			}

			bindInfoPanel(matchRequestItem.matchDate, matchRequestItem)
			requestComment.text = matchRequestItem.comment.takeUnless { postType.text == context.getString(R.string.request_response) }
				?: matchRequestItem.responseComment
			requestComment.isVisible = requestComment.text.isNotEmpty()
				date.isVisible = !acceptButton.isVisible
			date.text = matchRequestItem.addedAt

			if (!acceptButton.isVisible) {
				root.setOnClickListener {
					clickListener?.invoke(REQUEST_RESPONSE, matchRequestItem.id, matchRequestItem.targetPlayerId, matchRequestItem.isOwned.takeUnless { matchRequestItem.isOwned == false } ?: matchRequestItem.isResponsed)
				}
			}
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
			insertScoreCallback?.invoke(arrayOf(
				acceptedGameItem.player,
				acceptedGameItem.targetPlayer))
		}
	}
}

fun RecyclerGameRequestResponseBinding.bindInfoPanel(
	formattedMatchDate: FormattedDate?,
	matchRequestItem: MatchRequestPostItem
) {
	infoPanel.month.text = formattedMatchDate?.month
	infoPanel.day.text = formattedMatchDate?.day
	infoPanel.timeAndDay.text = infoPanel.timeAndDay.context.getString(
		R.string.day_and_time,
		formattedMatchDate?.time,
		formattedMatchDate?.dayOfWeek
	)
	infoPanel.ratingAndSkill.text = infoPanel.ratingAndSkill.context.getString(
		R.string.rating_and_skill,
		matchRequestItem.playerRating,
		matchRequestItem.experience
	)
}

class GameRequestResponseItemViewHolder(
	val binding: RecyclerGameRequestResponseBinding
) : RecyclerView.ViewHolder(binding.root)