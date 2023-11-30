package tennis.bot.mobile.onboarding.survey

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SurveyViewModel @Inject constructor(
	private val repository: AccountInfoRepository
): ViewModel() {

	private val _uiStateFlow = MutableStateFlow<SurveyUiState>(
		SurveyUiState.Loading(
			progressPercent = 0,
			repository.questionsTitlesList[0]
	))

	val uiStateFlow = _uiStateFlow.asStateFlow()

	fun onOverallGameSkill() {
		_uiStateFlow.value = SurveyUiState.OverallGameSkill(
			progressPercent = 0,
			repository.questionsTitlesList[0],
			repository.optionsList,
		)
	}

	fun onForehandLevel() {
		_uiStateFlow.value = SurveyUiState.ForehandLevel(
			progressPercent = 10,
			repository.questionsTitlesList[1],
		)
	}

	fun onBackhandLevel() {
		_uiStateFlow.value = SurveyUiState.BackhandLevel(
			progressPercent = 18,
			repository.questionsTitlesList[2],
		)
	}

	fun onSliceShotLevel() {
		_uiStateFlow.value = SurveyUiState.SliceShotLevel(
			progressPercent = 32,
			repository.questionsTitlesList[3],
		)
	}

	fun onServeLevel() {
		_uiStateFlow.value = SurveyUiState.ServeLevel(
			progressPercent = 41,
			repository.questionsTitlesList[4],
		)
	}

	fun onNetGameLevel() {
		_uiStateFlow.value = SurveyUiState.NetGameLevel(
			progressPercent = 50,
			repository.questionsTitlesList[5],
		)
	}

	fun onGameSpeedLevel() {
		_uiStateFlow.value = SurveyUiState.GameSpeedLevel(
			progressPercent = 63,
			repository.questionsTitlesList[6],
		)
	}

	fun onTournamentParticipation() {
		_uiStateFlow.value = SurveyUiState.TournamentParticipation(
			progressPercent = 78,
			repository.questionsTitlesList[7],
		)
	}

	fun onTournamentTopPlaces() {
		_uiStateFlow.value = SurveyUiState.TournamentTopPlaces(
			progressPercent = 99,
			repository.questionsTitlesList[8],
		)
	}


}