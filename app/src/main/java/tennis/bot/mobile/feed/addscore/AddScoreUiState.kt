package tennis.bot.mobile.feed.addscore

data class AddScoreUiState(
	val pickedOption: Int? = null,
	val sideNoteTitle: String,
	val sideNoteText: String,
	val sideNoteContainer: Boolean = true,
	val nextButtonEnabled: Boolean
)
