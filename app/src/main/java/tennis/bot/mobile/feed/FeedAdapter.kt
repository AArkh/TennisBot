package tennis.bot.mobile.feed

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getColor
import androidx.recyclerview.widget.RecyclerView
import coil.load
import tennis.bot.mobile.R
import tennis.bot.mobile.core.CoreAdapter
import tennis.bot.mobile.core.CoreUtilsItem
import tennis.bot.mobile.databinding.FeedPostOneNewPlayerBinding
import tennis.bot.mobile.databinding.FeedPostThreeMatchScoreBinding
import tennis.bot.mobile.databinding.FeedPostTwoMatchRequestBinding
import tennis.bot.mobile.databinding.RecyclerEmptyItemBinding
import tennis.bot.mobile.profile.account.EmptyItemViewHolder
import tennis.bot.mobile.profile.matches.ratingChange
import tennis.bot.mobile.utils.FormattedDate
import tennis.bot.mobile.utils.buildImageRequest
import tennis.bot.mobile.utils.formatDateForFeed
import tennis.bot.mobile.utils.formatDateForMatchPostItem
import tennis.bot.mobile.utils.view.AvatarImage
import javax.inject.Inject

class FeedAdapter @Inject constructor(): CoreAdapter<RecyclerView.ViewHolder>() {

	companion object {
		private const val OTHER = 0
		const val NEW_PLAYER = 1
		const val MATCH_REQUEST = 2
		const val SCORE = 3
	}

	override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Any) {
		when(holder) {
			is NewPlayerPostItemViewHolder -> bindNewPlayerPost(item, holder)
			is MatchRequestPostItemViewHolder -> bindMatchRequestPost(item, holder)
			is ScorePostItemViewHolder -> bindScorePost(item, holder)
		}
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
		val inflater = LayoutInflater.from(parent.context)

		return when(viewType) {
			NEW_PLAYER -> {
				val binding = FeedPostOneNewPlayerBinding.inflate(inflater, parent, false)
				NewPlayerPostItemViewHolder(binding)
			}
			MATCH_REQUEST -> {
				val binding = FeedPostTwoMatchRequestBinding.inflate(inflater, parent, false)
				MatchRequestPostItemViewHolder(binding)
			}
			SCORE -> {
				val binding = FeedPostThreeMatchScoreBinding.inflate(inflater, parent, false)
				ScorePostItemViewHolder(binding)
			}
			else -> {
				val binding = RecyclerEmptyItemBinding.inflate(inflater, parent, false)
				EmptyItemViewHolder(binding)
			}
		}
	}

	private fun bindNewPlayerPost(item: Any, holder: NewPlayerPostItemViewHolder) {
		val newPlayerItem = item as? NewPlayerPostItem ?: throw IllegalArgumentException("Item must be NewPlayerPostItem")

		holder.binding.playerPhoto.setImage(AvatarImage(newPlayerItem.newPlayerPost.picFile))
		holder.binding.nameSurname.text = newPlayerItem.newPlayerPost.name
		holder.binding.infoPanel.text = holder.binding.infoPanel.context.getString(
			R.string.player_new_info_panel,
			newPlayerItem.newPlayerPost.rating,
			newPlayerItem.experience,
			newPlayerItem.location)

		if(newPlayerItem.postData.liked) {
			holder.binding.likeButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.fire_active, 0, 0, 0)
			holder.binding.likeButton.setTextColor(getColor(holder.binding.likeButton.context, R.color.tb_red))
		} else {
			holder.binding.likeButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.fire, 0, 0, 0)
			holder.binding.likeButton.text = ""
		}

		holder.binding.date.text = newPlayerItem.postData.addedAt?.let { formatDateForFeed(it) }
	}

	private fun bindMatchRequestPost(item: Any,holder: MatchRequestPostItemViewHolder) {
		val matchRequestItem = item as? MatchRequestPostItem ?: throw IllegalArgumentException("Item must be MatchRequestPostItem")
		val formattedMatchDate = matchRequestItem.matchRequestPost.date?.let { formatDateForMatchPostItem(it) }

		holder.binding.playerPhoto.setImage(AvatarImage(matchRequestItem.matchRequestPost.playerPhoto))
		holder.binding.nameSurname.text = matchRequestItem.matchRequestPost.playerName
		holder.binding.locationSubTitle.text = matchRequestItem.locationSubTitle
//			holder.binding.locationSubTitle.context.getString( // more to ViewModel
//			R.string.post_two_location_subtitle,
//			matchResultItem.cityId,
//			matchResultItem.districtId)

		holder.binding.itemPicture.load(buildImageRequest(holder.binding.itemPicture.context, matchRequestItem.matchRequestPost.playerPhoto))

		holder.bindInfoPanel(formattedMatchDate, matchRequestItem)
		holder.binding.requestComment.text = matchRequestItem.matchRequestPost.comment

		if(matchRequestItem.postData.liked) {
			holder.binding.likeButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.fire_active, 0, 0, 0)
			holder.binding.likeButton.setTextColor(getColor(holder.binding.likeButton.context, R.color.tb_red))
		} else {
			holder.binding.likeButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.fire, 0, 0, 0)
			holder.binding.likeButton.text = ""
		}

		holder.binding.date.text = matchRequestItem.postData.addedAt?.let { formatDateForFeed(it) }
	}

	private fun MatchRequestPostItemViewHolder.bindInfoPanel(
		formattedMatchDate: FormattedDate?,
		matchRequestItem: MatchRequestPostItem
	) {
		binding.infoPanel.month.text = formattedMatchDate?.month
		binding.infoPanel.day.text = formattedMatchDate?.day.toString()
		binding.infoPanel.timeAndDay.text = binding.infoPanel.timeAndDay.context.getString(
			R.string.day_and_time,
			formattedMatchDate?.time,
			formattedMatchDate?.dayOfWeek
		)
		binding.infoPanel.ratingAndSkill.text = binding.infoPanel.ratingAndSkill.context.getString(
			R.string.rating_and_skill,
			matchRequestItem.matchRequestPost.playerRating,
			matchRequestItem.experience
		)
	}

	private fun bindScorePost(item: Any, holder: ScorePostItemViewHolder) {
		val scorePostItem = item as? ScorePostItem ?: throw IllegalArgumentException("Item must be ScorePostItem")

		holder.binding.playerPhoto.setImage(AvatarImage(scorePostItem.scorePost.player1?.photo))
		if (scorePostItem.scorePost.player3 == null) {
			holder.binding.nameSurname.text = scorePostItem.scorePost.player1?.name
			if (scorePostItem.scorePost.matchWon) {
				holder.binding.subTitle.text = holder.binding.subTitle.context.getString(R.string.won_subtitle, scorePostItem.scorePost.player2?.name)
			} else {
				holder.binding.subTitle.text = holder.binding.subTitle.context.getString(R.string.lost_to_subtitle, scorePostItem.scorePost.player2?.name)
			}
		} else {
			holder.binding.nameSurname.text = holder.binding.nameSurname.context.getString(
				R.string.post_three_doubles_title,
				scorePostItem.scorePost.player1?.name,
				scorePostItem.scorePost.player2?.name)
			if (scorePostItem.scorePost.matchWon) {
				holder.binding.subTitle.text = holder.binding.subTitle.context.getString(
					R.string.won_doubles_subtitle,
					scorePostItem.scorePost.player3.name,
					scorePostItem.scorePost.player4?.name)
			} else {
				holder.binding.subTitle.text = holder.binding.subTitle.context.getString(
					R.string.lost_doubles_subtitle,
					scorePostItem.scorePost.player3.name,
					scorePostItem.scorePost.player4?.name)
			}
		}

		val matchResultsAdapter = MatchResultsAdapter()
		holder.binding.resultsContainer.adapter = matchResultsAdapter
		matchResultsAdapter.submitList(formMatchResultsList(scorePostItem))
	}

	private fun formMatchResultsList(item: ScorePostItem): List<CoreUtilsItem> {
		val theList: MutableList<CoreUtilsItem> = mutableListOf()

		val (winner, loser) = if (item.scorePost.matchWon) {
			item.scorePost.player1!! to item.scorePost.player2!!
		} else {
			item.scorePost.player2!! to item.scorePost.player1!!
		}

		theList.add(MatchScoreItem(
			winnerName = winner.name,
			loserName = loser.name,
			sets = item.scorePost.sets
		))
		theList.add(RatingItem(
			player1Rating = winner.powerNew,
			player2Rating = loser.powerNew,
			player1RatingDifference = ratingChange(winner.powerNew.toString(), winner.powerOld.toString()),
			player2RatingDifference = ratingChange(loser.powerNew.toString(), loser.powerOld.toString())
		))
		if (item.scorePost.player3 == null) {
			theList.add(HeadToHeadItem(
				score = "${item.scorePost.player1.headToHead} - ${item.scorePost.player2.headToHead}",
				playersLeftPhotos = listOf(AvatarImage(item.scorePost.player1.photo)),
				playersRightPhotos = listOf(AvatarImage(item.scorePost.player2.photo)),
				))
		} else {
			theList.add(HeadToHeadItem(
				score = "${item.scorePost.player1.headToHead} - ${item.scorePost.player2.headToHead}",
				playersLeftPhotos = listOf(AvatarImage(item.scorePost.player1.photo), AvatarImage(item.scorePost.player2.photo)),
				playersRightPhotos = listOf(AvatarImage(item.scorePost.player3.photo), AvatarImage(item.scorePost.player4?.photo)),
			))
		}
		theList.add(BonusItem(
			player1Bonus = winner.bonus,
			player2Bonus = loser.bonus,
			player1BonusDifference = winner.bonusChange,
			player2BonusDifference = loser.bonusChange
		))

		return theList
	}

	override fun getItemViewType(position: Int): Int {
		return when((items[position] as PostParent).postType) {
			1 -> NEW_PLAYER
			2 -> MATCH_REQUEST
			3 -> SCORE
			else -> OTHER
		}
	}
}

class NewPlayerPostItemViewHolder(
	val binding: FeedPostOneNewPlayerBinding
) : RecyclerView.ViewHolder(binding.root)

class MatchRequestPostItemViewHolder(
	val binding: FeedPostTwoMatchRequestBinding
) : RecyclerView.ViewHolder(binding.root)

class ScorePostItemViewHolder(
	val binding: FeedPostThreeMatchScoreBinding
) : RecyclerView.ViewHolder(binding.root)

data class NewPlayerPostItem(
	val postData: PostData,
	val newPlayerPost: PostParent.NewPlayerPost,
	val location: String,
	val experience: String,
): CoreUtilsItem()

data class MatchRequestPostItem(
	val postData: PostData,
	val matchRequestPost: PostParent.MatchRequestPost,
	val locationSubTitle: String,
	val experience: String
): CoreUtilsItem()

data class ScorePostItem(
	val postItem: PostData,
	val scorePost: PostParent.ScorePost
): CoreUtilsItem()