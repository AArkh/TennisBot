package tennis.bot.mobile.feed.activityfeed

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import tennis.bot.mobile.R
import tennis.bot.mobile.core.CoreAdapter
import tennis.bot.mobile.core.CoreUtilsItem
import tennis.bot.mobile.databinding.PostThreeBonusBinding
import tennis.bot.mobile.databinding.PostThreeHeadToHeadBinding
import tennis.bot.mobile.databinding.PostThreeMatchScoreBinding
import tennis.bot.mobile.databinding.PostThreeRatingBinding
import tennis.bot.mobile.databinding.RecyclerEmptyItemBinding
import tennis.bot.mobile.profile.account.EmptyItemViewHolder
import tennis.bot.mobile.profile.matches.TennisSetNetwork
import tennis.bot.mobile.utils.formRatingChange
import tennis.bot.mobile.utils.view.AvatarImage
import javax.inject.Inject

class MatchResultsAdapter @Inject constructor(): CoreAdapter<RecyclerView.ViewHolder>() {

	companion object {
		private const val MATCH_SCORE = 0
		private const val RATING = 1
		private const val HEAD_TO_HEAD = 2
		private const val BONUS = 3
		private const val OTHER = 4
	}

	override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Any) {
		when(holder) {
			is MatchScoreItemViewHolder -> bindMatchScore(item, holder, FeedGameSetsAdapter())
			is RatingItemViewHolder -> bindRating(item, holder)
			is HeadToHeadItemViewHolder -> bindHeadToHead(item, holder)
			is BonusItemViewHolder -> bindBonus(item, holder)
		}
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
		val inflater = LayoutInflater.from(parent.context)

		return when(viewType) {
			MATCH_SCORE -> {
				val binding = PostThreeMatchScoreBinding.inflate(inflater, parent, false)
				MatchScoreItemViewHolder(binding)
			}
			RATING -> {
				val binding = PostThreeRatingBinding.inflate(inflater, parent, false)
				RatingItemViewHolder(binding)
			}
			HEAD_TO_HEAD -> {
				val binding = PostThreeHeadToHeadBinding.inflate(inflater, parent, false)
				HeadToHeadItemViewHolder(binding)
			}
			BONUS -> {
				val binding = PostThreeBonusBinding.inflate(inflater, parent, false)
				BonusItemViewHolder(binding)
			}
			else -> {
				val binding = RecyclerEmptyItemBinding.inflate(inflater, parent, false)
				EmptyItemViewHolder(binding)
			}
		}
	}

	private fun bindMatchScore(item: Any, holder: MatchScoreItemViewHolder, gameSetsAdapter: FeedGameSetsAdapter) {
		val matchScoreItem = item as? MatchScoreItem ?: throw IllegalArgumentException("Item must be MatchScoreItem")

		holder.binding.player1NameSurname.text = matchScoreItem.winnerName
		holder.binding.player2NameSurname.text = matchScoreItem.loserName

		holder.binding.gameSetsContainer.adapter = gameSetsAdapter
		holder.binding.gameSetsContainer.layoutManager = LinearLayoutManager(
			holder.binding.gameSetsContainer.context, LinearLayoutManager.HORIZONTAL, true)
		gameSetsAdapter.submitList(matchScoreItem.sets)
	}

	private fun bindRating(item: Any, holder: RatingItemViewHolder) {
		val ratingItem = item as? RatingItem ?: throw IllegalArgumentException("Item must be RatingItem")

		holder.binding.ratings.text = holder.binding.ratings.context.getString(
			R.string.two_ratings,
			ratingItem.player1Rating,
			ratingItem.player2Rating)
		holder.binding.player1ScoreChange.formRatingChange(ratingItem.player1RatingDifference)
		holder.binding.player2ScoreChange.formRatingChange(ratingItem.player2RatingDifference)

	}

	private fun bindHeadToHead(item: Any, holder: HeadToHeadItemViewHolder) {
		val headToHeadItem = item as? HeadToHeadItem ?: throw IllegalArgumentException("Item must be HeadToHeadItem")

		holder.binding.playersLeftPhotos.setImages(headToHeadItem.playersLeftPhotos, 0)
		holder.binding.playersRightPhotos.setImages(headToHeadItem.playersRightPhotos, 0)
		holder.binding.score.text = headToHeadItem.score
	}

	private fun bindBonus(item: Any, holder: BonusItemViewHolder) {
		val bonusItem = item as? BonusItem ?: throw IllegalArgumentException("Item must be BonusItem")
		val context = holder.binding.bonuses.context

		holder.binding.bonuses.text = holder.binding.bonuses.context.getString(
			R.string.two_ratings,
			bonusItem.player1Bonus,
			bonusItem.player2Bonus,)
		holder.binding.bonusesChange.text = context.getString(
			R.string.bonuses_change,
			bonusItem.player1BonusDifference,
			bonusItem.player2BonusDifference)
	}

	override fun getItemViewType(position: Int): Int {
		return when(position) {
			0 -> MATCH_SCORE
			1 -> RATING
			2 -> HEAD_TO_HEAD
			3 -> BONUS
			else -> OTHER
		}
	}
}

class MatchScoreItemViewHolder(
	val binding: PostThreeMatchScoreBinding
) : RecyclerView.ViewHolder(binding.root)

class RatingItemViewHolder(
	val binding: PostThreeRatingBinding
) : RecyclerView.ViewHolder(binding.root)

class HeadToHeadItemViewHolder(
	val binding: PostThreeHeadToHeadBinding
) : RecyclerView.ViewHolder(binding.root)

class BonusItemViewHolder(
	val binding: PostThreeBonusBinding
) : RecyclerView.ViewHolder(binding.root)

data class MatchScoreItem(
	val winnerName: String,
	val loserName: String,
	val sets: List<TennisSetNetwork>
): CoreUtilsItem()

data class RatingItem(
	val player1Rating: Int,
	val player2Rating: Int,
	val player1RatingDifference: String,
	val player2RatingDifference: String
): CoreUtilsItem()

data class HeadToHeadItem(
	val score: String,
	val playersLeftPhotos: List<AvatarImage>,
	val playersRightPhotos: List<AvatarImage>,
): CoreUtilsItem()

data class BonusItem(
	val player1Bonus: Int,
	val player2Bonus: Int,
	val player1BonusDifference: Int,
	val player2BonusDifference: Int
): CoreUtilsItem()