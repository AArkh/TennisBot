package tennis.bot.mobile.profile.matches

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.core.content.ContextCompat.getColor
import androidx.core.content.ContextCompat.getColorStateList
import androidx.core.view.setPadding
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import tennis.bot.mobile.R
import tennis.bot.mobile.core.CoreUtilsItem
import tennis.bot.mobile.databinding.RecyclerMatchItemBinding
import tennis.bot.mobile.profile.account.AccountPageAdapter
import tennis.bot.mobile.profile.account.getDefaultDrawableResourceId
import tennis.bot.mobile.utils.dpToPx
import javax.inject.Inject

class MatchesAdapter @Inject constructor(): PagingDataAdapter<MatchItem, MatchItemViewHolder>(MATCHES_COMPARATOR) {
	companion object {
		private val MATCHES_COMPARATOR = object : DiffUtil.ItemCallback<MatchItem>() {
			override fun areItemsTheSame(oldItem: MatchItem, newItem: MatchItem): Boolean =
				oldItem.id == newItem.id

			override fun areContentsTheSame(oldItem: MatchItem, newItem: MatchItem): Boolean =
				oldItem == newItem
		}
	}

	override fun onBindViewHolder(holder: MatchItemViewHolder, position: Int) {
		getItem(position)?.let {match ->
			holder.loadPlayerImage(match, true)
			holder.binding.player1Layout.playerName.text = match.playerOneName.substringBefore(" ")
			holder.binding.player1NameSurname.text = match.playerOneName
			holder.binding.player1Layout.playerRatingValue.text = match.playerOneCurrentRating
			holder.onBindingRating(match, true)

			holder.loadPlayerImage(match, false)
			holder.binding.player2Layout.playerName.text = match.playerTwoName.substringBefore(" ")
			holder.binding.player2NameSurname.text = match.playerTwoName
			holder.binding.player2Layout.playerRatingValue.text = match.playerTwoCurrentRating
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

		val difference = ratingChange(
			if (isPlayer1) match.playerOneCurrentRating else match.playerTwoCurrentRating,
			if (isPlayer1) match.playerOnePreviousRating else match.playerTwoPreviousRating
		)

		if (difference.contains("-")) {
			playerScoreChange.backgroundTintList = getColorStateList(playerScoreChange.context, R.color.tb_bland_red)
			playerScoreChange.setTextColor(getColor(playerScoreChange.context, R.color.tb_red))
			val drawable: Drawable? = getDrawable(playerScoreChange.context, R.drawable.vector_down)
			playerScoreChange.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
		} else {
			playerScoreChange.backgroundTintList = getColorStateList(playerScoreChange.context, R.color.tb_bland_green)
			playerScoreChange.setTextColor(getColor(playerScoreChange.context, R.color.tb_primary_green))
			val drawable: Drawable? = getDrawable(playerScoreChange.context, R.drawable.vector_up)
			playerScoreChange.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
		}

		playerScoreChange.text = difference.trimStart('-')
	}

	private fun MatchItemViewHolder.loadPlayerImage(match: MatchItem, isPlayer1: Boolean) {
		val playerImage = if (isPlayer1) binding.player1Layout.playerImage else binding.player2Layout.playerImage
		val playerPhoto = if (isPlayer1) binding.player1Layout.playerPhoto else binding.player2Layout.playerPhoto
		val playerProfilePic = if (isPlayer1) match.playerOneProfilePic else match.playerTwoProfilePic

		playerImage.load(R.drawable.user) { crossfade(true) }
		playerPhoto.setPadding(playerPhoto.context.dpToPx(15))
		
		if (playerProfilePic != null) {
			if (playerProfilePic.contains("default")) {
				val resourceId = getDefaultDrawableResourceId(playerImage.context, playerProfilePic.removeSuffix(".png"))
				if (resourceId != null) playerImage.load(resourceId)
				playerPhoto.setPadding(0)
			} else {
				playerImage.load(AccountPageAdapter.IMAGES_LINK + playerProfilePic) { crossfade(true) }
				playerPhoto.setPadding(0)
			}
		}
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchItemViewHolder {
		val binding = RecyclerMatchItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
		return MatchItemViewHolder(binding)
	}
}

fun ratingChange(currentRating: String, previousRating:String): String {
	return (currentRating.toInt() - previousRating.toInt()).toString()
}

class MatchItemViewHolder(
	val binding: RecyclerMatchItemBinding
) : RecyclerView.ViewHolder(binding.root)

data class MatchItem(
	val id: Int,
	val isWin: Boolean,
	val isDouble: Boolean,
	val playerOneProfilePic: String?,
	val playerOneName: String,
	val playerOneCurrentRating: String,
	val playerOnePreviousRating:String,
	val playerTwoProfilePic: String?,
	val playerTwoName: String,
	val playerTwoCurrentRating: String,
	val playerTwoPreviousRating:String,
	val score: String,
	val tennisSets: List<TennisSetNetwork>,
	val dateTime: String
): CoreUtilsItem()