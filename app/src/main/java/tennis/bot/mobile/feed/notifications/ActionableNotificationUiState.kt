package tennis.bot.mobile.feed.notifications

import tennis.bot.mobile.feed.activityfeed.MatchRequestPostItem

data class ActionableNotificationUiState (
	val title: String,
	val id: Int,
	val itemToDisplay: MatchRequestPostItem?
)