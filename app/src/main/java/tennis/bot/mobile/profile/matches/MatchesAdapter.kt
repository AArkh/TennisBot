package tennis.bot.mobile.profile.matches

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import tennis.bot.mobile.core.CoreAdapter
import tennis.bot.mobile.core.CoreUtilsItem
import tennis.bot.mobile.databinding.RecyclerMatchItemBinding
import javax.inject.Inject

class MatchesAdapter @Inject constructor(): CoreAdapter<MatchItemViewHolder>() {
	override fun onBindViewHolder(holder: MatchItemViewHolder, item: Any) {
		val match = item as? MatchItem ?: throw IllegalArgumentException("Item must be MatchItem")
		holder.binding.player1Image.load(match.playerOneProfilePic) { crossfade(true) }
		holder.binding.player1Name.text = match.playerOneName
		holder.binding.player1RatingValue.text = match.playerOneCurrentRating
		holder.binding.player1ScoreChange.text = match.playerOnePreviousRating // todo

		// Bind player two data
		holder.binding.player2Image.load(match.playerTwoProfilePic) { crossfade(true) }
		holder.binding.player2Name.text = match.playerTwoName
		holder.binding.player2RatingValue.text = match.playerTwoCurrentRating
		holder.binding.player2ScoreChange.text = match.playerTwoPreviousRating // todo

		// Bind other data
		holder.binding.score.text = match.score
		holder.binding.set11.text = match.set11
		holder.binding.set12.text = match.set12
		holder.binding.set13.text = match.set13
		holder.binding.set21.text = match.set21
		holder.binding.set22.text = match.set22
		holder.binding.set23.text = match.set23
		holder.binding.dateTime.text = match.dateTime
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchItemViewHolder {
		val binding = RecyclerMatchItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
		return MatchItemViewHolder(binding)
	}

}

class MatchItemViewHolder(
	val binding: RecyclerMatchItemBinding
) : RecyclerView.ViewHolder(binding.root)

data class MatchItem(
	val playerOneProfilePic: String?,
	val playerOneName: String,
	val playerOneCurrentRating: String,
	val playerOnePreviousRating:String,
	val playerTwoProfilePic: String?,
	val playerTwoName: String,
	val playerTwoCurrentRating: String,
	val playerTwoPreviousRating:String,
	val score: String,
	val set11: String,
	val set12: String,
	val set13: String,
	val set21: String,
	val set22: String,
	val set23: String,
	val dateTime: String
): CoreUtilsItem()