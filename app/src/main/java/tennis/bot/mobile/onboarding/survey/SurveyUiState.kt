package tennis.bot.mobile.onboarding.survey

sealed class SurveyUiState(
	open val progressPercent: Int,
	open val questionTitle: String,
	open val prevState: SurveyUiState?,
) {

	data class OverallGameSkill(
		override val progressPercent: Int,
		override val questionTitle: String,
		override val prevState: SurveyUiState?,
		val options: List<SurveyItem>,
	) : SurveyUiState(progressPercent, questionTitle, prevState)

	data class ForehandLevel(
		override val progressPercent: Int,
		override val questionTitle: String,
		override val prevState: SurveyUiState
	) : SurveyUiState(progressPercent, questionTitle, prevState)

	data class BackhandLevel(
		override val progressPercent: Int,
		override val questionTitle: String,
		override val prevState: SurveyUiState
	) : SurveyUiState(progressPercent, questionTitle, prevState)

	data class SliceShotLevel(
		override val progressPercent: Int,
		override val questionTitle: String,
		override val prevState: SurveyUiState
	) : SurveyUiState(progressPercent, questionTitle, prevState)

	data class ServeLevel(
		override val progressPercent: Int,
		override val questionTitle: String,
		override val prevState: SurveyUiState
	) : SurveyUiState(progressPercent, questionTitle, prevState)

	data class NetGameLevel(
		override val progressPercent: Int,
		override val questionTitle: String,
		override val prevState: SurveyUiState
	) : SurveyUiState(progressPercent, questionTitle, prevState)

	data class GameSpeedLevel(
		override val progressPercent: Int,
		override val questionTitle: String,
		override val prevState: SurveyUiState
	) : SurveyUiState(progressPercent, questionTitle, prevState)

	data class TournamentParticipation(
		override val progressPercent: Int,
		override val questionTitle: String,
		override val prevState: SurveyUiState
	) : SurveyUiState(progressPercent, questionTitle, prevState)

	data class TournamentTopPlaces(
		override val progressPercent: Int,
		override val questionTitle: String,
		override val prevState: SurveyUiState
	) : SurveyUiState(progressPercent, questionTitle, prevState)
}