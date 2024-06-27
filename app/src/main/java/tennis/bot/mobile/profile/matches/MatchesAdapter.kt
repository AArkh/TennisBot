package tennis.bot.mobile.profile.matches

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import tennis.bot.mobile.R
import tennis.bot.mobile.core.CoreUtilsItem
import tennis.bot.mobile.databinding.RecyclerMatchItemBinding
import tennis.bot.mobile.utils.dpToPx
import tennis.bot.mobile.utils.formRatingChange
import tennis.bot.mobile.utils.view.AvatarImage
import javax.inject.Inject



class MatchesAdapter @Inject constructor(): PagingDataAdapter<MatchItem, MatchItemViewHolder>(MATCHES_COMPARATOR) {
	companion object {
		private const val IMAGE_SIZE = 56

		private val MATCHES_COMPARATOR = object : DiffUtil.ItemCallback<MatchItem>() {
			override fun areItemsTheSame(oldItem: MatchItem, newItem: MatchItem): Boolean =
				oldItem.id == newItem.id

			override fun areContentsTheSame(oldItem: MatchItem, newItem: MatchItem): Boolean =
				oldItem == newItem
		}
	}

	override fun onBindViewHolder(holder: MatchItemViewHolder, position: Int) {
		getItem(position)?.let {match ->

			holder.bindPlayerPhotosNamesAndRating(match, isPlayer1 = true)
			holder.onBindingRating(match, true)

			holder.bindPlayerPhotosNamesAndRating(match, isPlayer1 = false)
			holder.onBindingRating(match, false)

			if(match.isWin) {
				holder.binding.player1Layout.trophy.visibility = View.VISIBLE
				holder.binding.player2Layout.trophy.visibility = View.GONE
			} else {
				holder.binding.player2Layout.trophy.visibility = View.VISIBLE
				holder.binding.player1Layout.trophy.visibility = View.GONE
			}

			val gameSetsAdapter = GameSetsAdapter()
			holder.binding.gameSetsContainer.adapter = gameSetsAdapter
			holder.binding.gameSetsContainer.layoutManager = LinearLayoutManager(
				holder.binding.gameSetsContainer.context, LinearLayoutManager.HORIZONTAL, true)
			gameSetsAdapter.submitList(match.tennisSets)

			holder.binding.score.text = match.score
			holder.binding.dateTime.text = match.dateTime
		}
	}

	private fun MatchItemViewHolder.onBindingRating(match: MatchItem, isPlayer1: Boolean) {
		val playerScoreChange = if (isPlayer1) binding.player1Layout.playerScoreChange else binding.player2Layout.playerScoreChange
		val secondPlayerScoreChange = if (isPlayer1) binding.player1Layout.secondPlayerScoreChange else binding.player2Layout.secondPlayerScoreChange

		if (!match.isDouble) {
			playerScoreChange.formRatingChange(ratingChange(
				if (isPlayer1) match.player1.rating.toString() else match.player2.rating.toString(),
				if (isPlayer1) match.player1.oldRating.toString() else match.player2.oldRating.toString()
			))
		} else {
			playerScoreChange.formRatingChange(ratingChange(
				if (isPlayer1) match.player1.rating.toString() else match.player3?.rating.toString(),
				if (isPlayer1) match.player1.oldRating.toString() else match.player3?.oldRating.toString()
			))
			secondPlayerScoreChange.formRatingChange(
				ratingChange(
					if (isPlayer1) match.player2.rating.toString() else match.player4?.rating.toString(),
					if (isPlayer1) match.player2.oldRating.toString() else match.player4?.oldRating.toString()
				))
		}
	}

	private fun MatchItemViewHolder.bindPlayerPhotosNamesAndRating(match: MatchItem, isPlayer1: Boolean) { // player 1 is the left one
		val context = binding.root.context
		val playerPhoto = if (isPlayer1) binding.player1Layout.playerPhoto else binding.player2Layout.playerPhoto
		val playerNameTitle = if (isPlayer1) binding.player1Layout.playerName else binding.player2Layout.playerName
		val playerNameSets = if (isPlayer1) binding.player1NameSurname else binding.player2NameSurname
		val playerRating = if (isPlayer1) binding.player1Layout.playerRatingValue else binding.player2Layout.playerRatingValue
		val secondPlayerRatingIcon = if (isPlayer1) binding.player1Layout.secondPlayerRatingImage else binding.player2Layout.secondPlayerRatingImage
		val secondPlayerRating = if (isPlayer1) binding.player1Layout.secondPlayerRatingValue else binding.player2Layout.secondPlayerRatingValue

		if (!match.isDouble){
			val playerProfilePic = if (isPlayer1) match.player1.photoUrl else match.player2.photoUrl
			val playerName = if (isPlayer1) match.player1.name else match.player2.name
			val playerRatingValue = if (isPlayer1) match.player1.rating.toString() else match.player2.rating.toString()

			playerPhoto.setImages(listOf(AvatarImage(playerProfilePic)), 0)
			playerPhoto.drawableSize = context.dpToPx(IMAGE_SIZE)
			playerNameTitle.text = playerName.substringBefore(" ")
			playerNameSets.text = playerName
			playerRating.text = playerRatingValue
			secondPlayerRatingIcon.isVisible = false
		} else {
			val playerFirstPhoto = if (isPlayer1) match.player1.photoUrl else match.player3?.photoUrl
			val playerSecondPhoto = if (isPlayer1) match.player2.photoUrl else match.player4?.photoUrl
			val playerFirstName = if (isPlayer1) match.player1.name else match.player3?.name
			val playerSecondName = if (isPlayer1) match.player2.name else match.player4?.name
			val playerRatingValue = if (isPlayer1) match.player1.rating.toString() else match.player3?.rating.toString()
			val secondPlayerRatingValue = if (isPlayer1) match.player2.rating.toString() else match.player4?.rating.toString()

			playerPhoto.setImages(
				listOf(AvatarImage(playerFirstPhoto), AvatarImage(playerSecondPhoto)), 0)
			playerPhoto.drawableSize = context.dpToPx(IMAGE_SIZE)
			playerNameTitle.text = context.getString(
				R.string.insert_score_doubles_names,
				playerFirstName?.substringBefore(" "),
				playerSecondName?.substringBefore(" ")
			)
			playerNameSets.text = context.getString(
				R.string.insert_score_doubles_names,
				playerFirstName?.substringBefore(" "),
				playerSecondName?.substringBefore(" ")
			)
			playerRating.text = playerRatingValue
			secondPlayerRatingIcon.isVisible = true
			secondPlayerRating.text = secondPlayerRatingValue
		}
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchItemViewHolder {
		val binding = RecyclerMatchItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
		return MatchItemViewHolder(binding)
	}
}

fun ratingChange(currentRating: String, previousRating: String): String {
	return (currentRating.toInt() - previousRating.toInt()).toString()
}

class MatchItemViewHolder(
	val binding: RecyclerMatchItemBinding
) : RecyclerView.ViewHolder(binding.root)

data class MatchItem(
	val id: Int,
	val isWin: Boolean,
	val isDouble: Boolean,
	val player1: Player,
	val player2: Player,
	val player3: Player? = null,
	val player4: Player? = null,
	val score: String,
	val tennisSets: List<TennisSetNetwork>,
	val dateTime: String
): CoreUtilsItem()