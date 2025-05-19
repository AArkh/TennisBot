package tennis.bot.mobile.onboarding.survey

sealed class SurveyResultsUiState{
	data class InitialWithAnswers(
		val answers: List<SurveyResultItem>,
		val buttonContinueText: String
	) : SurveyResultsUiState()

	object Loading: SurveyResultsUiState()

	object Error: SurveyResultsUiState()
}