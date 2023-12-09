package tennis.bot.mobile.onboarding.survey

sealed class SurveyResultsUiState{
	data class InitialWithAnswers(
		val answers: List<SurveyResultItem>
	) : SurveyResultsUiState()

	object SendingPost: SurveyResultsUiState()

	object Error: SurveyResultsUiState()
}