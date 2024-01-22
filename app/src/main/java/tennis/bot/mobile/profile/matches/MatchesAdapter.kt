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
import androidx.recyclerview.widget.RecyclerView
import coil.load
import tennis.bot.mobile.R
import tennis.bot.mobile.core.CoreAdapter
import tennis.bot.mobile.core.CoreUtilsItem
import tennis.bot.mobile.databinding.RecyclerMatchItemBinding
import tennis.bot.mobile.profile.account.AccountPageAdapter
import tennis.bot.mobile.profile.account.getDefaultDrawableResourceId
import javax.inject.Inject

class MatchesAdapter @Inject constructor(): CoreAdapter<MatchItemViewHolder>() {
	override fun onBindViewHolder(holder: MatchItemViewHolder, item: Any) {
		val match = item as? MatchItem ?: throw IllegalArgumentException("Item must be MatchItem")

		if(match.playerOneProfilePic != null) {
			if (match.playerOneProfilePic.contains("default")) {
				val resourceId = getDefaultDrawableResourceId(holder.binding.player1Image.context, match.playerOneProfilePic.removeSuffix(".png"))
				if (resourceId != null) holder.binding.player1Image.setImageResource(resourceId)
				holder.binding.player1Photo.setPadding(0)
			} else {
				holder.binding.player1Image.load(AccountPageAdapter.IMAGES_LINK + match.playerOneProfilePic) { crossfade(true) }
				holder.binding.player1Photo.setPadding(0)
			}
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

		if(match.playerTwoProfilePic != null) {
			if (match.playerTwoProfilePic.contains("default")) {
				val resourceId = getDefaultDrawableResourceId(holder.binding.player1Image.context, match.playerTwoProfilePic.removeSuffix(".png"))
				if (resourceId != null) holder.binding.player2Image.setImageResource(resourceId)
				holder.binding.player2Photo.setPadding(0)
			} else {
				holder.binding.player2Image.load(AccountPageAdapter.IMAGES_LINK + match.playerTwoProfilePic) { crossfade(true) }
				holder.binding.player2Photo.setPadding(0)
			}
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
	val set11: String,
	val set12: String,
	val set13: String,
	val set21: String,
	val set22: String,
	val set23: String,
	val dateTime: String
): CoreUtilsItem()