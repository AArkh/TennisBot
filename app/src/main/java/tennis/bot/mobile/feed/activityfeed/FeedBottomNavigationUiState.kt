package tennis.bot.mobile.feed.activityfeed

import tennis.bot.mobile.core.CoreUtilsItem

data class FeedBottomNavigationUiState(
	val playerPicture: String?,
	val postItems: List<CoreUtilsItem> = emptyList(),
	val scorePostMediaList: List<FeedMediaItem> = emptyList()
)
