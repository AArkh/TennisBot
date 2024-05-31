package tennis.bot.mobile.feed.activityfeed

import tennis.bot.mobile.core.CoreUtilsItem

data class FeedUiState(
	val postItems: List<CoreUtilsItem> = emptyList(),
	val scorePostMediaList: List<FeedMediaItem> = emptyList()
)
