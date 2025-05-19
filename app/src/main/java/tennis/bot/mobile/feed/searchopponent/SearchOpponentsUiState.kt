package tennis.bot.mobile.feed.searchopponent

import tennis.bot.mobile.utils.view.AvatarImage

data class SearchOpponentsUiState(
	val scoreType: Int? = null,
	val title: String? = null,
	val hintTitle: String?,
	val photosList: List<AvatarImage>? = emptyList(),
	val opponentsList: Array<OpponentItem?>?, // don't use generated code - it breaks uiStateFlow updates
	val numberOfOpponents: Int = 1,
	val isNextButtonEnabled: Boolean

)