package tennis.bot.mobile.profile.matches

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.Log
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
import tennis.bot.mobile.core.CoreAdapter
import tennis.bot.mobile.core.CoreUtilsItem
import tennis.bot.mobile.databinding.RecyclerMatchItemBinding
import tennis.bot.mobile.profile.account.AccountPageAdapter
import tennis.bot.mobile.profile.account.AccountPageAdapter.Companion.NULL_STRING
import tennis.bot.mobile.profile.account.getDefaultDrawableResourceId
import javax.inject.Inject

class MatchesAdapter @Inject constructor(): PagingDataAdapter<MatchItem, MatchItemViewHolder>(MATCHES_COMPARATOR) {

	private val gameSetsAdapter = GameSetsAdapter()
	companion object {
		private val MATCHES_COMPARATOR = object : DiffUtil.ItemCallback<MatchItem>() {
			override fun areItemsTheSame(oldItem: MatchItem, newItem: MatchItem): Boolean =
				oldItem == newItem // should choose a field for this one

			override fun areContentsTheSame(oldItem: MatchItem, newItem: MatchItem): Boolean =
				oldItem == newItem
		}
	}

	override fun onBindViewHolder(holder: MatchItemViewHolder, position: Int) {
		getItem(position)?.let {match ->

			if(match.playerOneProfilePic != null && match.playerOneProfilePic != NULL_STRING) {
				if (match.playerOneProfilePic.contains("default")) {
					val resourceId = getDefaultDrawableResourceId(holder.binding.player1Image.context, match.playerOneProfilePic.removeSuffix(".png"))
					if (resourceId != null) holder.binding.player1Image.setImageResource(resourceId)
					holder.binding.player1Photo.setPadding(0)
				} else {
					holder.binding.player1Image.load(AccountPageAdapter.IMAGES_LINK + match.playerOneProfilePic) { crossfade(true) }
					holder.binding.player1Photo.setPadding(0)
				}
			} else {
				holder.binding.player1Image.setImageResource(R.drawable.user)
			}
			holder.binding.player1Name.text = match.playerOneName.substringBefore(" ")
			holder.binding.player1NameSurname.text = match.playerOneName
			holder.binding.player1RatingValue.text = match.playerOneCurrentRating
			val differenceP1 = ratingChange(match.playerOneCurrentRating, match.playerOnePreviousRating)
			if(differenceP1.contains("-")) {
				holder.binding.player1ScoreChange.backgroundTintList = getColorStateList(holder.binding.player1ScoreChange.context, R.color.tb_bland_red)
				holder.binding.player1ScoreChange.setTextColor(getColor(holder.binding.player1ScoreChange.context, R.color.tb_red))
				val drawable: Drawable? = getDrawable(holder.binding.player1ScoreChange.context, R.drawable.vector_down)
				holder.binding.player1ScoreChange.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
			} else {
				holder.binding.player1ScoreChange.backgroundTintList = getColorStateList(holder.binding.player1ScoreChange.context, R.color.tb_bland_green)
				holder.binding.player1ScoreChange.setTextColor(getColor(holder.binding.player1ScoreChange.context, R.color.tb_primary_green))
				val drawable: Drawable? = getDrawable(holder.binding.player1ScoreChange.context, R.drawable.vector_up)
				holder.binding.player1ScoreChange.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
			}
			holder.binding.player1ScoreChange.text = differenceP1.trimStart('-')

			if(match.playerTwoProfilePic != null && match.playerOneProfilePic != NULL_STRING) {
				if (match.playerTwoProfilePic.contains("default")) {
					val resourceId = getDefaultDrawableResourceId(holder.binding.player1Image.context, match.playerTwoProfilePic.removeSuffix(".png"))
					if (resourceId != null) holder.binding.player2Image.setImageResource(resourceId)
					holder.binding.player2Photo.setPadding(0)
				} else {
					holder.binding.player2Image.load(AccountPageAdapter.IMAGES_LINK + match.playerTwoProfilePic) { crossfade(true) }
					holder.binding.player2Photo.setPadding(0)
				}
			} else {
				holder.binding.player1Image.setImageResource(R.drawable.user)
			}
			holder.binding.player2Name.text = match.playerTwoName.substringBefore(" ")
			holder.binding.player2NameSurname.text = match.playerTwoName
			holder.binding.player2RatingValue.text = match.playerTwoCurrentRating
			val differenceP2 = ratingChange(match.playerTwoCurrentRating, match.playerTwoPreviousRating)
			if(differenceP2.contains("-")) {
				holder.binding.player2ScoreChange.backgroundTintList = getColorStateList(holder.binding.player2ScoreChange.context, R.color.tb_bland_red)
				holder.binding.player2ScoreChange.setTextColor(getColor(holder.binding.player2ScoreChange.context, R.color.tb_red))
				val drawable: Drawable? = getDrawable(holder.binding.player2ScoreChange.context, R.drawable.vector_down)
				holder.binding.player2ScoreChange.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
			} else {
				holder.binding.player2ScoreChange.backgroundTintList = getColorStateList(holder.binding.player2ScoreChange.context, R.color.tb_bland_green)
				holder.binding.player2ScoreChange.setTextColor(getColor(holder.binding.player2ScoreChange.context, R.color.tb_primary_green))
				val drawable: Drawable? = getDrawable(holder.binding.player2ScoreChange.context, R.drawable.vector_up)
				holder.binding.player2ScoreChange.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
			}
			holder.binding.player2ScoreChange.text = differenceP2.trimStart('-')

			holder.binding.gameSetsContainer.adapter = gameSetsAdapter // decide whether to keep this pattern or come up with a new one
			holder.binding.gameSetsContainer.layoutManager = LinearLayoutManager(
				holder.binding.gameSetsContainer.context, LinearLayoutManager.HORIZONTAL, true)
			gameSetsAdapter.submitList(match.gameSets)
			val recycledViewPool = RecyclerView.RecycledViewPool()
			recycledViewPool.setMaxRecycledViews(0,10)
			holder.binding.gameSetsContainer.setRecycledViewPool(recycledViewPool)

			holder.binding.score.text = match.score
			holder.binding.dateTime.text = match.dateTime
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
	val gameSets: List<GameSet>,
	val dateTime: String
): CoreUtilsItem()