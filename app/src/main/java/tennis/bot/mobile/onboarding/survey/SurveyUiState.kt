package tennis.bot.mobile.onboarding.survey

sealed class SurveyUiState(
	open val progressPercent: Int,
	open val questionTitle: String,
	open val sideNoteTitle: String,
	open val sideNoteText: String // research something to store an answer
) {
	data class Loading(
		override val progressPercent: Int,
		override val questionTitle: String,
		override val sideNoteTitle: String,
		override val sideNoteText: String
	) : SurveyUiState(progressPercent, questionTitle, sideNoteTitle, sideNoteText)

	data class OverallGameSkill(
		override val progressPercent: Int,
		override val questionTitle: String,
		val options: List<SurveyOptionsItem>,
		override val sideNoteTitle: String,
		override val sideNoteText: String
	) : SurveyUiState(progressPercent, questionTitle, sideNoteTitle, sideNoteText)

	data class ForehandLevel(
		override val progressPercent: Int,
		override val questionTitle: String,
		override val sideNoteTitle: String,
		override val sideNoteText: String
	) : SurveyUiState(progressPercent, questionTitle, sideNoteTitle, sideNoteText)

	data class BackhandLevel(
		override val progressPercent: Int,
		override val questionTitle: String,
		override val sideNoteTitle: String,
		override val sideNoteText: String
	) : SurveyUiState(progressPercent, questionTitle, sideNoteTitle, sideNoteText)

	data class SliceShotLevel(
		override val progressPercent: Int,
		override val questionTitle: String,
		override val sideNoteTitle: String,
		override val sideNoteText: String
	) : SurveyUiState(progressPercent, questionTitle, sideNoteTitle, sideNoteText)

	data class ServeLevel(
		override val progressPercent: Int,
		override val questionTitle: String,
		override val sideNoteTitle: String,
		override val sideNoteText: String
	) : SurveyUiState(progressPercent, questionTitle, sideNoteTitle, sideNoteText)

	data class NetGameLevel(
		override val progressPercent: Int,
		override val questionTitle: String,
		override val sideNoteTitle: String,
		override val sideNoteText: String
	) : SurveyUiState(progressPercent, questionTitle, sideNoteTitle, sideNoteText)

	data class GameSpeedLevel(
		override val progressPercent: Int,
		override val questionTitle: String,
		override val sideNoteTitle: String,
		override val sideNoteText: String
	) : SurveyUiState(progressPercent, questionTitle, sideNoteTitle, sideNoteText)

	data class TournamentParticipation(
		override val progressPercent: Int,
		override val questionTitle: String,
		override val sideNoteTitle: String,
		override val sideNoteText: String
	) : SurveyUiState(progressPercent, questionTitle, sideNoteTitle, sideNoteText)

	data class TournamentTopPlaces(
		override val progressPercent: Int,
		override val questionTitle: String,
		override val sideNoteTitle: String,
		override val sideNoteText: String
	) : SurveyUiState(progressPercent, questionTitle, sideNoteTitle, sideNoteText)

	data class Error(
		override val progressPercent: Int,
		override val questionTitle: String,
		override val sideNoteTitle: String,
		override val sideNoteText: String
	) : SurveyUiState(progressPercent, questionTitle, sideNoteTitle, sideNoteText)
}