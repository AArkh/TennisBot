package tennis.bot.mobile.feed.requestcreation

import tennis.bot.mobile.core.CoreUtilsItem

data class RequestCreationUiState(
	val layoutItemsList: List<CoreUtilsItem>,
	val isLoading: Boolean,
	val isCreateButtonLoading: Boolean,
	val isCreateButtonActive: Boolean
)