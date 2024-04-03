package tennis.bot.mobile.profile.account

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getColor
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import tennis.bot.mobile.R
import tennis.bot.mobile.core.CoreAdapter
import tennis.bot.mobile.core.CoreUtilsItem
import tennis.bot.mobile.databinding.AccountBasicInfoAndRatingItemBinding
import tennis.bot.mobile.databinding.AccountButtonSwitchBinding
import tennis.bot.mobile.databinding.AccountCalibrationBinding
import tennis.bot.mobile.databinding.AccountFriendsBinding
import tennis.bot.mobile.databinding.AccountMatchesPlayedBinding
import tennis.bot.mobile.databinding.AccountPointsAndPositionBinding
import tennis.bot.mobile.databinding.AccountTournamentsBinding
import tennis.bot.mobile.databinding.RecyclerEmptyItemBinding
import tennis.bot.mobile.onboarding.survey.SurveyResultsAdapter
import tennis.bot.mobile.utils.animateButtonTransition
import javax.inject.Inject

class AccountPageAdapter @Inject constructor(): CoreAdapter<RecyclerView.ViewHolder>(){

	var clickListener: ((item: String) -> Unit)? = null
	val childAdapter = SurveyResultsAdapter()

	companion object { // todo there should be a place for PRO item
		private const val BASIC_INFO_AND_RATING = 0
		private const val CALIBRATION = 1
		private const val MATCHES_PLAYED = 2
		private const val POINTS_AND_POSITION = 3
		private const val TOURNAMENTS = 4
		private const val FRIENDS = 5
		private const val BUTTON_SWITCH = 6
		private const val OTHER = 7
		const val IMAGES_LINK = "http://bugz.su:9000/profilepictures/"
		const val NULL_STRING = "null"
	}

	override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Any) {
		when(holder){
			is AccountBasicInfoAndRatingItemViewHolder -> bindAccountBasicInfoAndRating(item, holder)
			is AccountCalibrationViewHolder -> {
				val calibration = item as? Calibration ?: throw IllegalArgumentException("Item must be Calibration")
				holder.binding.title.text = calibration.title
				holder.binding.progressBar.progress = calibration.progressBarValue
				holder.binding.calibrationProgressNumber.text = calibration.matchesRemains
				holder.binding.calibrationRoundsRemainText.text = calibration.matchesRemainsText
			}
			is AccountMatchesPlayedViewHolder -> {
				val matchesPlayed = item as? MatchesPlayed ?: throw IllegalArgumentException("Item must be MatchesPlayed")
				holder.binding.matchesPlayedWithNumber.text = matchesPlayed.numberOfMatchesPlayed
				holder.binding.lastGameDate.text = matchesPlayed.lastGameDate

				holder.binding.root.setOnClickListener {
					clickListener?.invoke(AccountPageFragment.GO_TO_MATCHES)
				}
			}
			is AccountPointsAndPositionViewHolder -> {
				val pointsAndPosition = item as? PointsAndPosition ?: throw IllegalArgumentException("Item must be MatchesPlayed")
				holder.binding.points.text = pointsAndPosition.points
				holder.binding.tournamentTitle.text = pointsAndPosition.tournamentTitle
				holder.binding.positionNumber.text = pointsAndPosition.positionNumber
			}
			is AccountTournamentsItemViewHolder -> {
				val tournaments = item as? Tournaments ?: throw IllegalArgumentException("Item must be MatchesPlayed")
				holder.binding.tournamentTitle.text = tournaments.tournamentTitle
				holder.binding.root.setOnClickListener {
					clickListener?.invoke(AccountPageFragment.GO_TO_TOURNAMENTS)
				}
			}
			is AccountFriendsItemViewHolder -> {
				val friends = item as? Friends ?: throw IllegalArgumentException("Item must be Friends")
				holder.binding.tournamentTitle.text = friends.tournamentTitle
				holder.binding.friend1Photo.load(friends.friendOnePhoto) { crossfade(true) }
				holder.binding.friend2Photo.load(friends.friendTwoPhoto) { crossfade(true) }
				holder.binding.friend3Photo.load(friends.friendThreePhoto) { crossfade(true) }
				if (friends.isMoreThanThree){
					holder.binding.friendsElse.setBackgroundColor(Color.WHITE)
				} else {
					holder.binding.friendsElse.setBackgroundColor(getColor(holder.binding.friendsElse.context, R.color.tb_bg_card))
				}
				holder.binding.friendsElseNumber.text = friends.friendsElseNumber
			}
			is AccountButtonSwitchViewHolder -> {
				holder.binding.recyclerView.adapter = childAdapter
				holder.binding.recyclerView.layoutManager = LinearLayoutManager(holder.binding.recyclerView.context)

				holder.binding.gameData.setOnClickListener {
					clickListener?.invoke(AccountPageFragment.INFLATE_GAMEDATA)

					animateButtonTransition(holder.binding.buttonsBackground, holder.binding.gameData)
					holder.binding.gameData.setTextColor(getColor(holder.binding.gameData.context, R.color.tb_white))
					holder.binding.contacts.setTextColor(getColor(holder.binding.contacts.context, R.color.tb_gray_active))
				}
				holder.binding.contacts.setOnClickListener {
					clickListener?.invoke(AccountPageFragment.INFLATE_CONTACTS)

					animateButtonTransition(holder.binding.buttonsBackground, holder.binding.contacts)
					holder.binding.contacts.setTextColor(getColor(holder.binding.contacts.context, R.color.tb_white))
					holder.binding.gameData.setTextColor(getColor(holder.binding.gameData.context, R.color.tb_gray_active))
				}
			}
		}
	}

	private fun bindAccountBasicInfoAndRating(item: Any, holder: AccountBasicInfoAndRatingItemViewHolder) {
		val basicInfoAndRating = item as? BasicInfoAndRating ?: throw IllegalArgumentException("Item must be BasicInfoAndRating")

		holder.binding.nameSurname.text = basicInfoAndRating.nameSurname
		holder.binding.ratingLayout.singleRatingValue.text = basicInfoAndRating.singleRating
		holder.showImage(basicInfoAndRating.profileImageUrl)
		if (basicInfoAndRating.telegram != NULL_STRING) {
			holder.binding.telegramId.text = basicInfoAndRating.telegram
		} else {
			holder.binding.telegramId.visibility = View.GONE
		}
		if (basicInfoAndRating.doublesRating != NULL_STRING) {
			holder.binding.ratingLayout.doublesRatingValue.text = basicInfoAndRating.doublesRating
		} else {
			holder.binding.ratingLayout.doublesRatingValue.text = basicInfoAndRating.singleRating
		}

		holder.binding.chartButton.setOnClickListener {
			// todo going to Chart
		}
		holder.binding.faqButton.setOnClickListener {
			// todo going to FAQ
		}
		holder.binding.accountPhotoFrame.setOnClickListener {
			clickListener?.invoke(AccountPageFragment.PICK_IMAGE)
		}
	}

	private fun AccountBasicInfoAndRatingItemViewHolder.showImage(profileImageUrl: String?) {
		if (profileImageUrl == null) return

		if (profileImageUrl.contains("default")) {
			val resourceId = getDefaultDrawableResourceId(
				binding.accountPhoto.context,
				profileImageUrl.removeSuffix(".png")
			)
			binding.accountPhoto.visibility = View.VISIBLE
			if (resourceId != null) binding.accountPhoto.setImageResource(resourceId)
			binding.placeholderPhoto.visibility = View.GONE
		} else {
			binding.accountPhoto.visibility = View.VISIBLE
			binding.accountPhoto.load(IMAGES_LINK + profileImageUrl) { crossfade(true) }
			binding.placeholderPhoto.visibility = View.GONE
		}
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
		val inflater = LayoutInflater.from(parent.context)

		return when (viewType) {
			BASIC_INFO_AND_RATING -> {
				val binding = AccountBasicInfoAndRatingItemBinding.inflate(inflater, parent, false)
				AccountBasicInfoAndRatingItemViewHolder(binding)
			}
			CALIBRATION -> {
				val binding = AccountCalibrationBinding.inflate(inflater, parent, false)
				AccountCalibrationViewHolder(binding)
			}
			MATCHES_PLAYED -> {
				val binding = AccountMatchesPlayedBinding.inflate(inflater, parent, false)
				AccountMatchesPlayedViewHolder(binding)
			}
			POINTS_AND_POSITION -> {
				val binding = AccountPointsAndPositionBinding.inflate(inflater, parent, false)
				AccountPointsAndPositionViewHolder(binding)
			}
			TOURNAMENTS -> {
				val binding = AccountTournamentsBinding.inflate(inflater, parent, false)
				AccountTournamentsItemViewHolder(binding)
			}
			FRIENDS -> {
				val binding = AccountFriendsBinding.inflate(inflater, parent, false)
				AccountFriendsItemViewHolder(binding)
			}
			BUTTON_SWITCH -> {
				val binding = AccountButtonSwitchBinding.inflate(inflater, parent, false)
				AccountButtonSwitchViewHolder(binding)
			}
			else -> {
				val binding = RecyclerEmptyItemBinding.inflate(inflater, parent, false)
				EmptyItemViewHolder(binding)
			}
		}
	}

	override fun getItemViewType(position: Int): Int {
		return when(position) {
			0 -> BASIC_INFO_AND_RATING
			1 -> CALIBRATION
			2 -> MATCHES_PLAYED
			3 -> POINTS_AND_POSITION
			4 -> TOURNAMENTS
			5 -> FRIENDS
			6 -> BUTTON_SWITCH
			else -> OTHER
		}
	}
}

fun getDefaultDrawableResourceId(context: Context, drawableName: String): Int? {
	val resourceId = context.resources.getIdentifier(drawableName, "drawable", context.packageName)

	return if (resourceId != 0) {
		resourceId
	} else {
		Log.d("123456", "couldn't find the drawable")
		null
	}
}

class AccountBasicInfoAndRatingItemViewHolder(
	val binding: AccountBasicInfoAndRatingItemBinding
) : RecyclerView.ViewHolder(binding.root)

class AccountCalibrationViewHolder(
	val binding: AccountCalibrationBinding
) : RecyclerView.ViewHolder(binding.root)

class AccountMatchesPlayedViewHolder(
	val binding: AccountMatchesPlayedBinding
) : RecyclerView.ViewHolder(binding.root)

class AccountPointsAndPositionViewHolder(
	val binding: AccountPointsAndPositionBinding
) : RecyclerView.ViewHolder(binding.root)

class AccountTournamentsItemViewHolder(
	val binding: AccountTournamentsBinding
) : RecyclerView.ViewHolder(binding.root)

class AccountFriendsItemViewHolder(
	val binding: AccountFriendsBinding
) : RecyclerView.ViewHolder(binding.root)

class  AccountButtonSwitchViewHolder(
	val binding: AccountButtonSwitchBinding
) : RecyclerView.ViewHolder(binding.root)

class EmptyItemViewHolder(
	val binding: RecyclerEmptyItemBinding
) : RecyclerView.ViewHolder(binding.root)

data class BasicInfoAndRating(
	val profileImageUrl: String?,
	val nameSurname: String,
	val telegram: String?,
	val singleRating: String,
	val doublesRating: String?
) : CoreUtilsItem()

data class Calibration(
	val title: String,
	val progressBarValue: Int,
	val matchesRemains: String,
	val matchesRemainsText: String
) : CoreUtilsItem()

data class MatchesPlayed(
	val numberOfMatchesPlayed: String,
	val lastGameDate: String,
) : CoreUtilsItem()

data class PointsAndPosition(
	val points: String,
	val tournamentTitle: String,
	val positionNumber: String
) : CoreUtilsItem()

data class Tournaments(
	val tournamentTitle: String
) : CoreUtilsItem()

data class Friends(
	val tournamentTitle: String,
	val friendOnePhoto: String?,
	val friendTwoPhoto: String?,
	val friendThreePhoto: String?,
	val friendsElseNumber: String?,
	val isMoreThanThree: Boolean
) : CoreUtilsItem()

data class ButtonSwitch(
	val isGameData: Boolean
) : CoreUtilsItem()
