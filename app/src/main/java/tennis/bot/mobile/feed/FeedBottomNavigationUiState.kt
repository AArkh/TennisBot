package tennis.bot.mobile.feed

import tennis.bot.mobile.core.CoreUtilsItem

data class FeedBottomNavigationUiState(
	val playerPicture: String?,
	val postItems: List<CoreUtilsItem> = emptyList(),
	val scorePostMediaList: List<MediaItem> = emptyList()
)
