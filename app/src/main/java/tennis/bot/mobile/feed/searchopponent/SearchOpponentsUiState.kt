package tennis.bot.mobile.feed.searchopponent

data class SearchOpponentsUiState(
	val hintTitle: String?,
	val opponentsList: List<OpponentItem>?,
	val numberOfOpponents: Int = 1,

)