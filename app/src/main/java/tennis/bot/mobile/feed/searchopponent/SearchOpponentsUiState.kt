package tennis.bot.mobile.feed.searchopponent

data class SearchOpponentsUiState(
	val scoreType: Int? = null,
	val hintTitle: String?,
	val opponentsList: Array<OpponentItem?>?, // пошла нахуй! - эта хуйня сгенерированная сломала мне uiStateFlow, я ее рот ебал
	val numberOfOpponents: Int = 1,
	val isNextButtonEnabled: Boolean

)