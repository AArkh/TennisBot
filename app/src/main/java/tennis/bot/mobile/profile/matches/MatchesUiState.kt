package tennis.bot.mobile.profile.matches



sealed class MatchesUiState {
	object Loading : MatchesUiState()
	object MatchesDataReceived: MatchesUiState()

	object Error : MatchesUiState()
}
