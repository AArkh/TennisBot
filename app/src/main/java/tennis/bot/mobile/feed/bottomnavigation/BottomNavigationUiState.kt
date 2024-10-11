package tennis.bot.mobile.feed.bottomnavigation

data class BottomNavigationUiState(
	val title: String,
	val currentItemId: Int,
	val allNotifications: Int,
	val feedNotifications: Int,
	val gameNotifications: Int,
	val playerPicture: String?
)
