package tennis.bot.mobile.feed.activityfeed

import android.animation.ObjectAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.getColor
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
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
	val mediaSliderAdapter = ImageSliderAdapter()
	private val matchResultsAdapter = MatchResultsAdapter()

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

		holder.binding.playerPhoto.showPlayerPhoto(newPlayerItem.playerPhoto, holder.binding.itemPicture)
		holder.binding.nameSurname.text = newPlayerItem.playerName
		holder.binding.infoPanel.text = newPlayerItem.infoPanel

		holder.binding.likeButton.isLikeActive(newPlayerItem.liked, newPlayerItem.totalLikes)

		holder.binding.date.text = newPlayerItem.addedAt?.let { formatDateForFeed(it) }
	}

	private fun bindMatchRequestPost(item: Any,holder: MatchRequestPostItemViewHolder) {
		val matchRequestItem = item as? MatchRequestPostItem ?: throw IllegalArgumentException("Item must be MatchRequestPostItem")
		val formattedMatchDate = matchRequestItem.matchDate?.let { formatDateForMatchPostItem(it) }

		holder.binding.playerPhoto.showPlayerPhoto(matchRequestItem.playerPhoto, holder.binding.itemPicture)
		holder.binding.nameSurname.text = matchRequestItem.playerName
		holder.binding.locationSubTitle.text = matchRequestItem.locationSubTitle

		holder.bindInfoPanel(formattedMatchDate, matchRequestItem)
		holder.binding.requestComment.text = matchRequestItem.comment

		holder.binding.likeButton.isLikeActive(matchRequestItem.liked, matchRequestItem.totalLikes)

		holder.binding.date.text = matchRequestItem.addedAt?.let { formatDateForFeed(it) }
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
			matchRequestItem.playerRating,
			matchRequestItem.experience
		)
	}

	private fun bindScorePost(item: Any, holder: ScorePostItemViewHolder) {
		val scorePostItem = item as? ScorePostItem ?: throw IllegalArgumentException("Item must be ScorePostItem")

		holder.binding.playerPhoto.showPlayerPhoto(scorePostItem.player1?.photo, null)
		holder.binding.itemPicture.isVisible = false
		holder.binding.itemPicturesPager.isVisible = true
		holder.binding.itemPicturesPager.adapter = mediaSliderAdapter
		mediaSliderAdapter.submitList(scorePostItem.mediaItemsList)

		TabLayoutMediator(holder.binding.tabLayout, holder.binding.itemPicturesPager) { tab, _ ->
			tab.setCustomView(R.layout.tab_image_switch_indicator)
		}.attach()
		holder.binding.tabLayout.addOnTabSelectedListener(this)

		if (scorePostItem.player3 == null) {
			holder.binding.matchTypeTitle.text = holder.binding.matchTypeTitle.context.getString(R.string.match_result_single)
			holder.binding.nameSurname.text = scorePostItem.player1?.name
			if (scorePostItem.matchWon) {
				holder.binding.subTitle.text = holder.binding.subTitle.context.getString(R.string.won_subtitle, scorePostItem.player2?.name)
			} else {
				holder.binding.subTitle.text = holder.binding.subTitle.context.getString(R.string.lost_to_subtitle, scorePostItem.player2?.name)
			}
		} else {
			holder.binding.matchTypeTitle.text = holder.binding.matchTypeTitle.context.getString(R.string.match_result_double)
			holder.binding.nameSurname.text = holder.binding.nameSurname.context.getString(
				R.string.post_three_doubles_title,
				scorePostItem.player1?.name,
				scorePostItem.player2?.name)
			if (scorePostItem.matchWon) {
				holder.binding.subTitle.text = holder.binding.subTitle.context.getString(
					R.string.won_doubles_subtitle,
					scorePostItem.player3.name,
					scorePostItem.player4?.name)
			} else {
				holder.binding.subTitle.text = holder.binding.subTitle.context.getString(
					R.string.lost_doubles_subtitle,
					scorePostItem.player3.name,
					scorePostItem.player4?.name)
			}
		}

		holder.binding.likeButton.isLikeActive(scorePostItem.liked, scorePostItem.totalLikes)

		holder.binding.resultsContainer.adapter = matchResultsAdapter
		matchResultsAdapter.submitList(scorePostItem.matchResultsList)
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

data class NewPlayerPostItem( // 1
	val id: Long, // i don't want to store two id's so for now let's pick the post one
	val postType: Int,
	val totalLikes: Int,
	val liked: Boolean,
	val addedAt: String?,
	val infoPanel: String, // location, experience and rating are here
	val playerName: String,
	val isMale: Boolean,
	val playerPhoto: String?
): CoreUtilsItem()

data class MatchRequestPostItem( // 2
	val id: Long, // i don't want to store two id's so for now let's pick the post one
	val postType: Int,
	val totalLikes: Int,
	val liked: Boolean,
	val addedAt: String?,
	val matchDate: String?,
	val playerPhoto: String?,
	val playerName: String,
	val playerRating: Int,
	val locationSubTitle: String?,
	val experience: String,
	val comment: String
): CoreUtilsItem()

data class ScorePostItem( // 3
	val id: Long, // i don't want to store two id's so for now let's pick the post one
	val postType: Int,
	val totalLikes: Int,
	val liked: Boolean,
	val addedAt: String?,
	val creatorId: Long,
	val photo: String?,
	val video: String?,
	val player1: PostParent.PlayerPostData?,
	val player2: PostParent.PlayerPostData?,
	val player3: PostParent.PlayerPostData?,
	val player4: PostParent.PlayerPostData?,
	val surface: String?,
	val duration: String?,
	val matchWon: Boolean,
	val sets: List<TennisSetNetwork>,
	val mediaItemsList: List<MediaItem>,
	val matchResultsList: List<CoreUtilsItem>
): CoreUtilsItem()