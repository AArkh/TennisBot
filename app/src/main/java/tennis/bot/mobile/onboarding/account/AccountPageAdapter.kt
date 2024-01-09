package tennis.bot.mobile.onboarding.account

import android.animation.ObjectAnimator
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
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
import javax.inject.Inject

class AccountPageAdapter @Inject constructor(): CoreAdapter<RecyclerView.ViewHolder>(){

	var clickListener: ((item: String) -> Unit)? = null // todo remake for different use cases
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
	}

	override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Any) {
		when(holder){
			is AccountBasicInfoAndRatingItemViewHolder -> {
				val basicInfoAndRating = item as? BasicInfoAndRating ?: throw IllegalArgumentException("Item must be BasicInfoAndRating")
				holder.binding.nameSurname.text = basicInfoAndRating.nameSurname
				holder.binding.ratingLayout.singleRatingValue.text = basicInfoAndRating.singleRating
				if(basicInfoAndRating.profileImageUrl != null) {
					holder.binding.accountPhoto.load(basicInfoAndRating.profileImageUrl) { crossfade(true) }
				}
				if (basicInfoAndRating.telegramId != null) {
					holder.binding.telegramId.text = basicInfoAndRating.telegramId
				} else {
					holder.binding.telegramId.visibility = View.GONE
				}
				if (basicInfoAndRating.doublesRating != null) {
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
			}
			is AccountCalibrationViewHolder -> {
				val calibration = item as? Calibration ?: throw IllegalArgumentException("Item must be Calibration")
				holder.binding.progressBar.progress = calibration.progressBarValue
				holder.binding.calibrationProgressNumber.text = calibration.matchesRemains
				holder.binding.calibrationRoundsRemainText.text = calibration.matchesRemainsText
			}
			is AccountMatchesPlayedViewHolder -> {
				val matchesPlayed = item as? MatchesPlayed ?: throw IllegalArgumentException("Item must be MatchesPlayed")
				holder.binding.matchesPlayedWithNumber.text = matchesPlayed.numberOfMatchesPlayed
				holder.binding.lastGameDate.text = matchesPlayed.lastGameDate
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
					holder.binding.friendsElse.setBackgroundColor(Color.TRANSPARENT)
				}
				holder.binding.friendsElseNumber.text = friends.friendsElseNumber
			}
			is AccountButtonSwitchViewHolder -> {
				val buttonSwitch = item as? ButtonSwitch ?: throw IllegalArgumentException("Item must be MatchesPlayed")

				holder.binding.recyclerView.adapter = childAdapter
				holder.binding.recyclerView.layoutManager = LinearLayoutManager(holder.binding.recyclerView.context)

				holder.binding.gameData.setOnClickListener {
					clickListener?.invoke(AccountPageFragment.INFLATE_GAMEDATA)

					holder.binding.gameData.setBackgroundResource(R.drawable.btn_bkg_enabled)
					holder.binding.gameData.setTextColor(ContextCompat.getColor(holder.binding.gameData.context, R.color.tb_white))
					holder.binding.contacts.setBackgroundResource(R.drawable.search_background)
					holder.binding.contacts.setTextColor(ContextCompat.getColor(holder.binding.contacts.context, R.color.tb_gray_active))
				}
				holder.binding.contacts.setOnClickListener { //todo add animation
					clickListener?.invoke(AccountPageFragment.INFLATE_CONTACTS)

					holder.binding.contacts.setBackgroundResource(R.drawable.btn_bkg_enabled)
					holder.binding.contacts.setTextColor(ContextCompat.getColor(holder.binding.contacts.context, R.color.tb_white))
					holder.binding.gameData.setBackgroundResource(R.drawable.search_background)
					holder.binding.gameData.setTextColor(ContextCompat.getColor(holder.binding.gameData.context, R.color.tb_gray_active))
				}
			}
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
	val telegramId: String?,
	val singleRating: String,
	val doublesRating: String?
) : CoreUtilsItem()

data class Calibration(
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
