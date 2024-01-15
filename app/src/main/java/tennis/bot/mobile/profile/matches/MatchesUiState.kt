package tennis.bot.mobile.profile.matches



sealed class MatchesUiState {
	object Loading : MatchesUiState()
	data class MatchesDataReceived(
		val matchesList: List<MatchItem>
	) : MatchesUiState()

	object Error : MatchesUiState()
}
