package tennis.bot.mobile.onboarding.account

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import tennis.bot.mobile.R
import tennis.bot.mobile.onboarding.account.AccountPageViewModel.Companion.ZERO
import tennis.bot.mobile.onboarding.survey.SurveyResultItem
import javax.inject.Inject

@HiltViewModel
class AccountPageViewModel @Inject constructor(
	@ApplicationContext private val context: Context,
	private val repository: UserProfileAndEnumsRepository
): ViewModel() {

	companion object {
		const val EMPTY_STRING = ""
		const val ZERO = 0
	}

	private val _uiStateFlow = MutableStateFlow<AccountPageUiState>(
		AccountPageUiState.Loading( isLoading = true )
	)
	val uiStateFlow = _uiStateFlow.asStateFlow()

	fun onFetchingProfileData(){
		viewModelScope.launch(Dispatchers.IO) {
			kotlin.runCatching {
				val profileData = repository.getProfile()
				val gamesRemain = 10 - (profileData.games ?: ZERO)
				val basicLayout = listOf(
					BasicInfoAndRating(
						profileData.photo,
						profileData.name,
						profileData.telegram,
						profileData.rating.toString(),
						profileData.doublesRating.toString()
					),
					Calibration(
						progressBarProgress(profileData.games),
						context.getString(R.string.calibration_matches_remain, profileData.games ?: ZERO),
						context.getString(R.string.calibration_rounds_remain_text, gamesRemain)
					),
					MatchesPlayed(
						context.getString(R.string.account_matches_played, profileData.games ?: ZERO),
						context.getString(R.string.last_game_date, profileData.lastGame ?: EMPTY_STRING)
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
				val gameDataList: List<SurveyResultItem>
				val contactsList: List<SurveyResultItem>

				_uiStateFlow.value =
					AccountPageUiState.ProfileDataReceived(basicLayout, defaultGameData, dummyContactsForButtons)
			}.onFailure {
				AccountPageUiState.Error( isError = true )
			}
		}
	}

//	fun onProvidingGameData(profileData: ProfileData): List<SurveyResultItem> {
//
//		viewModelScope.launch(Dispatchers.IO) {
//			val enumTypesList = repository.getEnums()
//			val hand = if (profileData.isRightHand == true) "Правая" else "Левая"
//			val backhand = if (profileData.isOneBackhand == true) "Одноручный" else "Двуручный"
//			val decodedIds = repository.getEnumsById(enumTypesList, listOf(
//				Pair("surface", profileData.surface),
//				Pair("shoes", profileData.shoes),
//				Pair("racquet", profileData.racquet),
//				Pair("racquetStrings", profileData.racquetStrings))
//			)
//
//			val modifiedValues = listOf( // all needed for mapping next
//				Pair("isRightHand", if (profileData.isRightHand != null) hand else context.getString(R.string.survey_option_null)),
//				Pair("isOnebackhand", if (profileData.isOneBackhand != null) backhand else context.getString(R.string.survey_option_null)),
//				decodedIds[0],
//				decodedIds[1],
//				decodedIds[2],
//				decodedIds[3],
//
//			)
//			val defaultGameData = repository.defaultGameData
//
////			val modifiedGameData = defaultGameData.map { gameDataItem ->
////				SurveyResultItem(gameDataItem.resultTitle, modifiedValues)
////
////			}
//
//		}
//	}

	val defaultGameData = listOf(
		SurveyResultItem("Стиль игры", ContextCompat.getString(context, R.string.survey_option_null)),
		SurveyResultItem("Ведущая рука", ContextCompat.getString(context, R.string.survey_option_null)),
		SurveyResultItem("Бэкхенд", ContextCompat.getString(context, R.string.survey_option_null)),
		SurveyResultItem("Основное покрытие", ContextCompat.getString(context, R.string.survey_option_null)),
		SurveyResultItem("Обувь", ContextCompat.getString(context, R.string.survey_option_null)),
		SurveyResultItem("Ракетка", ContextCompat.getString(context, R.string.survey_option_null)),
		SurveyResultItem("Струны", ContextCompat.getString(context, R.string.survey_option_null), noUnderline = true),
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
			else -> ZERO
		}

		return percentage
	}
}