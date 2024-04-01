package tennis.bot.mobile.feed

import android.animation.ObjectAnimator
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.getColor
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import coil.load
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import tennis.bot.mobile.R
import tennis.bot.mobile.core.CoreAdapter
import tennis.bot.mobile.core.CoreUtilsItem
import tennis.bot.mobile.databinding.FeedPostOneNewPlayerBinding
import tennis.bot.mobile.databinding.FeedPostThreeMatchScoreBinding
import tennis.bot.mobile.databinding.FeedPostTwoMatchRequestBinding
import tennis.bot.mobile.databinding.RecyclerEmptyItemBinding
import tennis.bot.mobile.profile.account.AccountPageAdapter
import tennis.bot.mobile.profile.account.EmptyItemViewHolder
import tennis.bot.mobile.profile.account.getDefaultDrawableResourceId
import tennis.bot.mobile.profile.matches.TennisSetNetwork
import tennis.bot.mobile.profile.matches.ratingChange
import tennis.bot.mobile.utils.FormattedDate
import tennis.bot.mobile.utils.dpToPx
import tennis.bot.mobile.utils.formatDateForFeed
import tennis.bot.mobile.utils.formatDateForMatchPostItem
import tennis.bot.mobile.utils.view.AvatarImage
import tennis.bot.mobile.utils.view.ImageSeriesView
import javax.inject.Inject

class FeedAdapter @Inject constructor(): CoreAdapter<RecyclerView.ViewHolder>(), TabLayout.OnTabSelectedListener {

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

		holder.binding.playerPhoto.showPlayerPhoto(newPlayerItem.newPlayerPost.picFile, holder.binding.itemPicture)
		holder.binding.nameSurname.text = newPlayerItem.newPlayerPost.name
		holder.binding.infoPanel.text = holder.binding.infoPanel.context.getString(
			R.string.player_new_info_panel,
			newPlayerItem.newPlayerPost.rating,
			newPlayerItem.experience,
			newPlayerItem.location)

		holder.binding.likeButton.isLikeActive(newPlayerItem.postData.liked, newPlayerItem.postData.totalLikes)

		holder.binding.date.text = newPlayerItem.postData.addedAt?.let { formatDateForFeed(it) }
	}

	private fun bindMatchRequestPost(item: Any,holder: MatchRequestPostItemViewHolder) {
		val matchRequestItem = item as? MatchRequestPostItem ?: throw IllegalArgumentException("Item must be MatchRequestPostItem")
		val formattedMatchDate = matchRequestItem.matchRequestPost.date?.let { formatDateForMatchPostItem(it) }

		holder.binding.playerPhoto.showPlayerPhoto(matchRequestItem.matchRequestPost.playerPhoto, holder.binding.itemPicture)
		holder.binding.nameSurname.text = matchRequestItem.matchRequestPost.playerName
		holder.binding.locationSubTitle.text = matchRequestItem.locationSubTitle

		holder.bindInfoPanel(formattedMatchDate, matchRequestItem)
		holder.binding.requestComment.text = matchRequestItem.matchRequestPost.comment

		holder.binding.likeButton.isLikeActive(matchRequestItem.postData.liked, matchRequestItem.postData.totalLikes)

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

		holder.binding.playerPhoto.showPlayerPhoto(scorePostItem.scorePost.player1?.photo, null)
		holder.binding.itemPicture.isVisible = false
		holder.binding.itemPicturesPager.isVisible = true
		holder.binding.itemPicturesPager.initializeImagesAdapter(createListOfImages(scorePostItem.scorePost))
		TabLayoutMediator(holder.binding.tabLayout, holder.binding.itemPicturesPager) { tab, _ ->
			tab.setCustomView(R.layout.tab_image_switch_indicator)
		}.attach()
		holder.binding.tabLayout.addOnTabSelectedListener(this)

		if (scorePostItem.scorePost.player3 == null) {
			holder.binding.matchTypeTitle.text = holder.binding.matchTypeTitle.context.getString(R.string.match_result_single)
			holder.binding.nameSurname.text = scorePostItem.scorePost.player1?.name
			if (scorePostItem.scorePost.matchWon) {
				holder.binding.subTitle.text = holder.binding.subTitle.context.getString(R.string.won_subtitle, scorePostItem.scorePost.player2?.name)
			} else {
				holder.binding.subTitle.text = holder.binding.subTitle.context.getString(R.string.lost_to_subtitle, scorePostItem.scorePost.player2?.name)
			}
		} else {
			holder.binding.matchTypeTitle.text = holder.binding.matchTypeTitle.context.getString(R.string.match_result_double)
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

		holder.binding.likeButton.isLikeActive(scorePostItem.postData.liked, scorePostItem.postData.totalLikes)

		val matchResultsAdapter = MatchResultsAdapter()
		holder.binding.resultsContainer.adapter = matchResultsAdapter
		matchResultsAdapter.submitList(formMatchResultsList(scorePostItem, holder.binding.resultsContainer.context))
	}

	private fun ViewPager2.initializeImagesAdapter(listOfImages: List<MediaItem>) {
		val adapter = ImageSliderAdapter()
		this.adapter = adapter
		orientation = ViewPager2.ORIENTATION_HORIZONTAL
		adapter.submitList(listOfImages)
	}

	private fun createListOfImages(item: PostParent.ScorePost): List<MediaItem> {
		val theList = mutableListOf<MediaItem>()
		if (item.video != null && item.photo != null) {
			theList.add(MediaItem(item.video, isVideo = true))
			theList.add(MediaItem(item.photo))
		} else if (item.video != null) {
			theList.add(MediaItem(item.video, isVideo = true))
		} else if (item.photo != null) {
			theList.add(MediaItem(item.photo))
		}
		theList.add(MediaItem(item.player1?.photo))
		theList.add(MediaItem(item.player2?.photo))
		if (item.player3 != null) {
			theList.add(MediaItem(item.player3.photo))
			theList.add(MediaItem(item.player4?.photo))
		}

		return theList.toList()
	}

	private fun TextView.isLikeActive(isLiked: Boolean, totalLikes: Int?) {
		if(isLiked) {
			setCompoundDrawablesWithIntrinsicBounds(R.drawable.fire_active, 0, 0, 0)
			setTextColor(getColor(context, R.color.tb_red))
		} else {
			setCompoundDrawablesWithIntrinsicBounds(R.drawable.fire, 0, 0, 0)

		}

		text = if (totalLikes != null && totalLikes != 0) totalLikes.toString() else ""
	}

	private fun ImageSeriesView.showPlayerPhoto(profileImageUrl: String?, itemPicture: ImageView?) {
		setImage(AvatarImage(profileImageUrl))
		drawableSize = context.dpToPx(40)

		if (itemPicture != null) {
			if (profileImageUrl == null) {
				itemPicture.load(R.drawable.null_placeholder)
				return
			}

			if (profileImageUrl.contains("default")) {
				val resourceId = getDefaultDrawableResourceId(itemPicture.context, profileImageUrl.removeSuffix(".png"))
				if (resourceId != null) itemPicture.load(resourceId)
			} else {
				itemPicture.load(AccountPageAdapter.IMAGES_LINK + profileImageUrl)
			}
		}

	}

	private fun formMatchResultsList(item: ScorePostItem, context: Context): List<CoreUtilsItem> {
		val theList: MutableList<CoreUtilsItem> = mutableListOf()

		val (winner, loser) = if (item.scorePost.matchWon) {
			if (item.scorePost.player3 == null) item.scorePost.player1!! to item.scorePost.player2!! else item.scorePost.player1!! to item.scorePost.player3
		} else {
			if (item.scorePost.player3 == null) item.scorePost.player2!! to item.scorePost.player1!! else item.scorePost.player3 to item.scorePost.player1!!
		}

		val (winnerNames, loserNames) = if (item.scorePost.matchWon) {
			context.getString(
				R.string.post_three_doubles_title,
				item.scorePost.player1.name.substringBefore(" "),
				item.scorePost.player2?.name?.substringBefore(" ")) to context.getString(
				R.string.post_three_doubles_title,
				item.scorePost.player3?.name?.substringBefore(" "),
				item.scorePost.player4?.name?.substringBefore(" "))
		} else {
			context.getString(
				R.string.post_three_doubles_title,
				item.scorePost.player3?.name?.substringBefore(" "),
				item.scorePost.player4?.name?.substringBefore(" ")) to context.getString(
				R.string.post_three_doubles_title,
				item.scorePost.player1.name.substringBefore(" "),
				item.scorePost.player2?.name?.substringBefore(" "))
		}

		theList.add(MatchScoreItem(
			winnerName = if (item.scorePost.player3 == null) winner.name else winnerNames,
			loserName = if (item.scorePost.player3 == null) loser.name else loserNames,
			sets = item.scorePost.sets.formSetsList(item.scorePost.matchWon)
		))
		theList.add(RatingItem(
			player1Rating = winner.powerNew,
			player2Rating = loser.powerNew,
			player1RatingDifference = ratingChange(winner.powerNew.toString(), winner.powerOld.toString()),
			player2RatingDifference = ratingChange(loser.powerNew.toString(), loser.powerOld.toString())
		))
		if (item.scorePost.player3 == null) {
			theList.add(HeadToHeadItem(
				score = "${item.scorePost.player1.headToHead} - ${item.scorePost.player2?.headToHead}",
				playersLeftPhotos = listOf(AvatarImage(item.scorePost.player1.photo)),
				playersRightPhotos = listOf(AvatarImage(item.scorePost.player2?.photo)),
				))
		} else {
			theList.add(HeadToHeadItem(
				score = "${item.scorePost.player1.headToHead} - ${item.scorePost.player2?.headToHead}",
				playersLeftPhotos = listOf(AvatarImage(item.scorePost.player1.photo), AvatarImage(item.scorePost.player2?.photo)),
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

	private fun List<TennisSetNetwork>.formSetsList(isWinner: Boolean): List<TennisSetNetwork> {
		val newList: MutableList<TennisSetNetwork> = mutableListOf()

		if (isWinner) {
			return this
		} else {
			for (set in this) {
				newList.add(TennisSetNetwork(
					score1 = set.score2,
					score2 = set.score1,
					scoreTie1 = set.scoreTie2,
					scoreTie2 = set.scoreTie1
				))
				return newList
			}
		}
		return emptyList()
	}

	override fun getItemViewType(position: Int): Int {
		return when (items[position]) {
			is NewPlayerPostItem -> NEW_PLAYER
			is MatchRequestPostItem -> MATCH_REQUEST
			is ScorePostItem -> SCORE
			else -> OTHER
		}
	}

	override fun onTabSelected(tab: TabLayout.Tab?) {
		animateDotColor(tab?.customView, true)
	}

	override fun onTabUnselected(tab: TabLayout.Tab?) {
		animateDotColor(tab?.customView, false)
	}

	override fun onTabReselected(tab: TabLayout.Tab?) {}

	private fun animateDotColor(customView: View?, selected: Boolean) {
		val alphaAnimator = ObjectAnimator.ofFloat(customView, View.ALPHA, if (selected) 1f else 0.5f)
		alphaAnimator.duration = 300
		alphaAnimator.start()
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
	val postData: PostData,
	val scorePost: PostParent.ScorePost
): CoreUtilsItem()