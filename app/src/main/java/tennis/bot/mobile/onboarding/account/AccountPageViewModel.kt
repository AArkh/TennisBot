package tennis.bot.mobile.onboarding.account

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
import tennis.bot.mobile.onboarding.location.LocationDialogUiState
import tennis.bot.mobile.onboarding.survey.SurveyResultItem
import javax.inject.Inject

@HiltViewModel
class AccountPageViewModel @Inject constructor(
	@ApplicationContext private val context: Context,
	private val repository: UserProfileAndEnumsRepository
): ViewModel() {

	private val _uiStateFlow = MutableStateFlow<AccountPageUiState>(
		AccountPageUiState.Loading( isLoading = true )
	)
	val uiStateFlow = _uiStateFlow.asStateFlow()

	fun onFetchingProfileData(){
		viewModelScope.launch(Dispatchers.IO) {
			kotlin.runCatching {
				val profileData = repository.getProfile()
				val gamesRemain = 10 - (profileData.games ?: 0)
				val basicLayout = listOf( // todo replace int's with const or whatever suitable
					BasicInfoAndRating(
						profileData.photo,
						profileData.name,
						profileData.telegram,
						profileData.rating.toString(),
						profileData.doublesRating.toString() ),
					Calibration(
						progressBarProgress(profileData.games),
						context.getString(R.string.calibration_matches_remain, profileData.games),
						context.getString(R.string.calibration_rounds_remain_text, gamesRemain)),
					MatchesPlayed(
						context.getString(R.string.account_matches_played, profileData.games),
						context.getString(R.string.last_game_date, "6 Января 2024") ),
					PointsAndPosition(
						context.getString(R.string.account_tournament_points, profileData.bonus),
						context.getString(R.string.tournament_title), // should get it elsewhere
						profileData.bonusRank.toString()
					),
					Tournaments(context.getString(R.string.tournament_title) ),
					Friends(
						context.getString(R.string.tournament_title),
						null, null, null, null, // not implemented yet
						false),
					ButtonSwitch(true)
				)
				val gameDataList: List<SurveyResultItem>
				val contactsList: List<SurveyResultItem>

				_uiStateFlow.value = AccountPageUiState.ProfileDataReceived(basicLayout, dummyGameDataForButtons, dummyContactsForButtons)
			}
		}
	}

	fun onFetchingGamedataAndContacts(){
		val currentState = uiStateFlow.value as AccountPageUiState.ProfileDataReceived

		viewModelScope.launch(Dispatchers.IO) {
			kotlin.runCatching {
				val profileData = repository.getProfile()

			}
		}
//		currentState.copy{
//
//		}

	}

	val dummyGameDataForButtons = listOf(
		SurveyResultItem("something", "something's value"),
		SurveyResultItem("something", "something's value"),
		SurveyResultItem("something", "something's value"),
		SurveyResultItem("something", "something's value"),
		SurveyResultItem("something", "something's value", noUnderline = true),
	)

	val dummyContactsForButtons = listOf(
		SurveyResultItem("something else", "something else's value"),
		SurveyResultItem("something else", "something else's value"),
		SurveyResultItem("something else", "something else's value"),
		SurveyResultItem("something else", "something else's value"),
		SurveyResultItem("something else", "something else's value", noUnderline = true),
	)

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
			10 -> 99
			else -> 0
		}

		return percentage
	}
}