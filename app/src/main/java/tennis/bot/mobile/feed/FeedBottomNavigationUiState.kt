package tennis.bot.mobile.feed

import tennis.bot.mobile.core.CoreUtilsItem

data class FeedBottomNavigationUiState(
	val playerPicture: String?,
	val postItems: List<CoreUtilsItem>? = null
)
