package tennis.bot.mobile.feed.searchopponent

sealed class SearchOpponentsUiState() {
	object Initial : SearchOpponentsUiState()
	object Loading : SearchOpponentsUiState()
	data class OpponentDataReceived(
		val opponentsList: List<OpponentItem>
	) : SearchOpponentsUiState()

	object Error : SearchOpponentsUiState()
}