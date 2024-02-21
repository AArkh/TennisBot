package tennis.bot.mobile.feed.insertscore

import android.net.Uri

data class InsertScoreUiState( // todo probably should store picture and video here as well
	val player1Image: String?,
	val player1Name: String,
	val player2Id: Long,
	val player2Image: String?,
	val player2Name: String,
	val setsList: List<TennisSetItem> = mutableListOf(TennisSetItem(1, "0 - 0")),
	val pickedPhoto: Uri? = null,
	val pickedVideo: Uri? = null,
	val isPhotoBackgroundActive: Boolean = false,
	val isAddSetButtonActive: Boolean = false,
	val isAddSuperTieBreakActive: Boolean = false,
	val isSendButtonActive: Boolean = false
)
