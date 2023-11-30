package tennis.bot.mobile.onboarding.survey

sealed class SurveyUiState(
	open val progressPercent: Int,
	open val questionTitle: String,
) {
	data class Loading(
		override val progressPercent: Int,
		override val questionTitle: String,
	) : SurveyUiState(progressPercent, questionTitle)

	data class OverallGameSkill(
		override val progressPercent: Int,
		override val questionTitle: String,
		val options: List<SurveyItem>,
	) : SurveyUiState(progressPercent, questionTitle)

	data class ForehandLevel(
		override val progressPercent: Int,
		override val questionTitle: String,
	) : SurveyUiState(progressPercent, questionTitle)

	data class BackhandLevel(
		override val progressPercent: Int,
		override val questionTitle: String,
	) : SurveyUiState(progressPercent, questionTitle)

	data class SliceShotLevel(
		override val progressPercent: Int,
		override val questionTitle: String,
	) : SurveyUiState(progressPercent, questionTitle)

	data class ServeLevel(
		override val progressPercent: Int,
		override val questionTitle: String,
	) : SurveyUiState(progressPercent, questionTitle)

	data class NetGameLevel(
		override val progressPercent: Int,
		override val questionTitle: String,
	) : SurveyUiState(progressPercent, questionTitle)

	data class GameSpeedLevel(
		override val progressPercent: Int,
		override val questionTitle: String,
	) : SurveyUiState(progressPercent, questionTitle)

	data class TournamentParticipation(
		override val progressPercent: Int,
		override val questionTitle: String,
	) : SurveyUiState(progressPercent, questionTitle)

	data class TournamentTopPlaces(
		override val progressPercent: Int,
		override val questionTitle: String,
	) : SurveyUiState(progressPercent, questionTitle)

	data class Error(
		override val progressPercent: Int,
		override val questionTitle: String,
	) : SurveyUiState(progressPercent, questionTitle)
}