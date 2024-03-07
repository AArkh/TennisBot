package tennis.bot.mobile.feed.searchopponent

data class SearchOpponentsUiState(
	val scoreType: Int? = null,
	val title: String? = null,
	val hintTitle: String?,
	val opponentsList: Array<OpponentItem?>?, // don't use generated code - it breaks uiStateFlow updates
	val numberOfOpponents: Int = 1,
	val isNextButtonEnabled: Boolean

)