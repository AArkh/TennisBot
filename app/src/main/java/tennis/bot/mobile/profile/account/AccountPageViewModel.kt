package tennis.bot.mobile.profile.account

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import tennis.bot.mobile.R
import tennis.bot.mobile.onboarding.survey.SurveyResultItem
import tennis.bot.mobile.utils.DEFAULT_DATE_TIME
import tennis.bot.mobile.utils.convertDateAndTime
import javax.inject.Inject

@HiltViewModel
class AccountPageViewModel @Inject constructor(
	@ApplicationContext private val context: Context,
	private val repository: UserProfileAndEnumsRepository
): ViewModel() {

	companion object {
		const val EMPTY_STRING = ""
		const val ZERO = 0
		const val IS_RIGHTHAND_TITLE = "isRightHand"
		const val IS_ONE_BACKHAND_TITLE = "isOneBackhand"
		const val SURFACE_TITLE = "surface"
		const val SHOES_TITLE = "shoes"
		const val RACQUET_TITLE = "racquet"
		const val RACQUET_STRINGS_TITLE = "strings"
	}

	private val _uiStateFlow = MutableStateFlow<AccountPageUiState>(
		AccountPageUiState.Loading
	)
	val uiStateFlow = _uiStateFlow.asStateFlow()

	fun onFetchingProfileData(){
		viewModelScope.launch(Dispatchers.IO) {
				val profileData = repository.getProfile()
				val difference = (10 - (profileData.games ?: 10))
				val gamesRemain = if (difference in 0..9) difference else 10

				val basicLayout = listOf(
					BasicInfoAndRating(
						profileData.photo,
						profileData.name,
						profileData.telegram,
						profileData.rating.toString(),
						profileData.universalDoublesRating.toString()
					),
					Calibration(
						if (gamesRemain < 10) context.getString(R.string.calibration_title) else context.getString(R.string.calibration_finished_title),
						progressBarProgress(if (profileData.games!! <= 10) profileData.games else 10 ),
						context.getString(R.string.calibration_matches_remain, if (profileData.games <= 10) profileData.games else 10 ),
						when(gamesRemain){
							0 -> context.getString(R.string.calibration_start)
							in 1..9 -> context.getString(R.string.calibration_rounds_remain_text, gamesRemain)
							else -> context.getString(R.string.calibration_finished)
						}
					),
					MatchesPlayed(
						context.getString(R.string.account_matches_played, profileData.games ?: ZERO),
						if (profileData.lastGame == DEFAULT_DATE_TIME) {
							EMPTY_STRING
						} else if (profileData.lastGame != null) {
							context.getString(R.string.last_game_date, convertDateAndTime(profileData.lastGame))
						} else {
							EMPTY_STRING
						}
					),
					PointsAndPosition(
						context.getString(R.string.account_tournament_points, profileData.bonus ?: ZERO),
						context.getString(R.string.tournament_title),
						(profileData.bonusRank ?: ZERO).toString()
					),
					Tournaments(context.getString(R.string.tournament_title)),
					Friends(
						context.getString(R.string.tournament_title),
						null, null, null, null, // not implemented yet
						false
					),
					ButtonSwitch(true)
				)
				val gameDataList = onProvidingGameData(profileData)

				_uiStateFlow.value =
					AccountPageUiState.ProfileDataReceived(basicLayout, gameDataList, emptyList())

		}
	}

	private suspend fun onProvidingGameData(profileData: ProfileData): List<SurveyResultItem> {
		val defaultGameData = repository.defaultGameData
		val isRightHandInt = if (profileData.isRightHand == true) 1 else if (profileData.isRightHand == false) 0 else null
		val isOneBackhandInt = if (profileData.isOneBackhand == true) 1 else if (profileData.isOneBackhand == false) 0 else null
		val decodedIds = repository.getEnumsById(
			listOf(
				Pair(IS_RIGHTHAND_TITLE, isRightHandInt),
				Pair(IS_ONE_BACKHAND_TITLE, isOneBackhandInt),
				Pair(SURFACE_TITLE, profileData.surface),
				Pair(SHOES_TITLE, profileData.shoes),
				Pair(RACQUET_TITLE, profileData.racquet),
				Pair(RACQUET_STRINGS_TITLE, profileData.racquetStrings)
			)
		)

		val modifiedValues = emptyList<String>() + decodedIds

		val modifiedGameData = defaultGameData.mapIndexed { index, gameDataItem ->
			gameDataItem.copy(resultOption = modifiedValues.getOrElse(index) { gameDataItem.resultOption })
		}

		return modifiedGameData
	}

	fun onTryAgainPressed(){
		_uiStateFlow.value = AccountPageUiState.Loading
		onFetchingProfileData()
	}

	private fun progressBarProgress(matchesRemain: Int?): Int {
		val percentage = when(matchesRemain){
			1 -> 9
			2 -> 19
			3 -> 29
			4 -> 39
			5 -> 49
			6 -> 59
			7 -> 69
			8 -> 79
			9 -> 89
			10 -> 100
			else -> ZERO
		}

		return percentage
	}
}