package tennis.bot.mobile.feed.insertscore

import android.content.Context
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import tennis.bot.mobile.profile.account.UserProfileAndEnumsRepository
import tennis.bot.mobile.utils.showToast
import javax.inject.Inject

@HiltViewModel
class InsertScoreViewModel @Inject constructor(
	private val userProfileRepository: UserProfileAndEnumsRepository,
): ViewModel() {

	companion object {
		const val DEFAULT_SCORE = "0 - 0"
	}

	private val _uiStateFlow = MutableStateFlow(
		InsertScoreUiState(
			player1Image = null,
			player1Name = "",
			player2Id = 0,
			player2Image = null,
			player2Name = "",
			isSendButtonActive = false
		)
	)
	val uiStateFlow = _uiStateFlow.asStateFlow()

	fun onInitial(opponentId: Long, opponentPhoto: String?, opponentName: String) {
		val player1 = userProfileRepository.getProfile()
		_uiStateFlow.value = _uiStateFlow.value.copy(
			player1Image = player1.photo,
			player1Name = player1.name.substringBefore(" "),
			player2Id = opponentId,
			player2Image = opponentPhoto,
			player2Name = opponentName.substringBefore(" "),
		)
	}

	fun onAddingSetItem(context: Context) {
		val currentSets = uiStateFlow.value.setsList
		if(currentSets.size < 5) {
			val newSets = currentSets + TennisSetItem((currentSets.size + 1),  DEFAULT_SCORE)
			_uiStateFlow.value = uiStateFlow.value.copy(setsList = newSets)
		} else {
			context.showToast("You can't add more sets")
		}
	}

	fun onDeletingSetItem(position: Int) {
		val currentSets = uiStateFlow.value.setsList

		val newSets = currentSets - currentSets[position]
		_uiStateFlow.value = uiStateFlow.value.copy(setsList = newSets)
	}

	fun onScoreReceived(setNumber: Int,score: String) {
		val newSetList = uiStateFlow.value.setsList.map { setItem ->
			if (setItem.setNumber == setNumber) {
				score.let { setItem.copy(score = it) }
			} else {
				setItem
			}
		}
		_uiStateFlow.value = uiStateFlow.value.copy(setsList = newSetList)
	}
}