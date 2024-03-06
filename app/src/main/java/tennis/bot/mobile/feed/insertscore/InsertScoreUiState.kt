package tennis.bot.mobile.feed.insertscore

import tennis.bot.mobile.core.CoreUtilsItem
import tennis.bot.mobile.feed.searchopponent.OpponentItem


data class InsertScoreUiState(
	val player1Image: String?,
	val player1Name: String,
	val player2: OpponentItem?,
	val player3: OpponentItem? = null,
	val player4: OpponentItem? = null,
	val setsList: List<TennisSetItem> = mutableListOf(TennisSetItem(1, "0 : 0", isActive = true)),
	val mediaItemList: List<CoreUtilsItem> = listOf(MediaTitle, InsertScoreMediaItem(), SideNoteItem()),
	val isAddSetButtonActive: Boolean = false,
	val isAddSuperTieBreakActive: Boolean = false,
	val isSendButtonActive: Boolean = false,
	val isLoading: Boolean = false
)
