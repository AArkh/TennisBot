package tennis.bot.mobile.feed.searchopponent.doubles

import tennis.bot.mobile.feed.searchopponent.OpponentItem

data class SearchOpponentsDoubleUiState(
	val hintTitle: String?,
	val opponent: OpponentItem? = null
)
