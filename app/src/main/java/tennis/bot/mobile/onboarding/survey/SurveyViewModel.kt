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
			repository.questionsTitlesList[0],
			repository.sideNotesList[0].sideNoteTitle,
			repository.sideNotesList[0].sideNoteText) // todo decide what shows in a Loading
	)

	val uiStateFlow = _uiStateFlow.asStateFlow()

	fun onOverallGameSkill() {
		_uiStateFlow.value = SurveyUiState.OverallGameSkill(
			progressPercent = 0,
			repository.questionsTitlesList[0],
			repository.optionsList,
			repository.sideNotesList[0].sideNoteTitle,
			repository.sideNotesList[0].sideNoteText
		)
	}

	fun onForehandLevel() {
		_uiStateFlow.value = SurveyUiState.ForehandLevel(
			progressPercent = 10,
			repository.questionsTitlesList[1],
			repository.sideNotesList[1].sideNoteTitle,
			repository.sideNotesList[1].sideNoteText
		)
	}

	fun onBackhandLevel() {
		_uiStateFlow.value = SurveyUiState.BackhandLevel(
			progressPercent = 18,
			repository.questionsTitlesList[2],
			repository.sideNotesList[2].sideNoteTitle,
			repository.sideNotesList[2].sideNoteText
		)
	}

	fun onSliceShotLevel() {
		_uiStateFlow.value = SurveyUiState.SliceShotLevel(
			progressPercent = 32,
			repository.questionsTitlesList[3],
			repository.sideNotesList[3].sideNoteTitle,
			repository.sideNotesList[3].sideNoteText
		)
	}

	fun onServeLevel() {
		_uiStateFlow.value = SurveyUiState.ServeLevel(
			progressPercent = 41,
			repository.questionsTitlesList[4],
			repository.sideNotesList[4].sideNoteTitle,
			repository.sideNotesList[4].sideNoteText
		)
	}

	fun onNetGameLevel() {
		_uiStateFlow.value = SurveyUiState.NetGameLevel(
			progressPercent = 50,
			repository.questionsTitlesList[5],
			repository.sideNotesList[5].sideNoteTitle,
			repository.sideNotesList[5].sideNoteText
		)
	}

	fun onGameSpeedLevel() {
		_uiStateFlow.value = SurveyUiState.GameSpeedLevel(
			progressPercent = 63,
			repository.questionsTitlesList[6],
			repository.sideNotesList[6].sideNoteTitle,
			repository.sideNotesList[6].sideNoteText
		)
	}

	fun onTournamentParticipation() {
		_uiStateFlow.value = SurveyUiState.TournamentParticipation(
			progressPercent = 78,
			repository.questionsTitlesList[7],
			repository.sideNotesList[7].sideNoteTitle,
			repository.sideNotesList[7].sideNoteText
		)
	}

	fun onTournamentTopPlaces() {
		_uiStateFlow.value = SurveyUiState.TournamentTopPlaces(
			progressPercent = 99,
			repository.questionsTitlesList[8],
			repository.sideNotesList[8].sideNoteTitle,
			repository.sideNotesList[8].sideNoteText
		)
	}


}