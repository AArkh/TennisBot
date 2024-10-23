package tennis.bot.mobile.feed.notifications

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import tennis.bot.mobile.R
import tennis.bot.mobile.feed.game.GameRepository
import tennis.bot.mobile.feed.game.GameViewModel
import javax.inject.Inject

@HiltViewModel
class ActionableNotificationViewModel @Inject constructor(
	private val gameRepository: GameRepository,
	notificationsRepository: NotificationsRepository,
	@ApplicationContext private val context: Context,
	savedStateHandle: SavedStateHandle
): GameViewModel(gameRepository, notificationsRepository, context) {

	private val isIncoming = savedStateHandle.get<Boolean>(ActionableNotificationFragment.IS_INCOMING_ARGUMENT) ?: false
	private val _uiStateFlow = MutableStateFlow(
		ActionableNotificationUiState(
			title = if (isIncoming) context.getString(R.string.incoming) else context.getString(R.string.outcoming_full),
			id = savedStateHandle.get<Int>(ActionableNotificationFragment.ID_ARGUMENT) ?: 0,
			itemToDisplay = null
		)
	)
	val immutableUiStateFlow: Flow<ActionableNotificationUiState> = _uiStateFlow.asStateFlow() // had to use a custom name to not upset the parent class

	fun getAppropriateItem(emptyListCallback: () -> Unit) {
		viewModelScope.launch(Dispatchers.IO) {
			val result = if(isIncoming) {
				gameRepository.getIncomingRequests(0)
			} else {
				gameRepository.getOutcomingRequests(0)
			}

			val items = result?.items?.let { gameRepository.mapGameToMatchRequestPostItem(it) }
			if (!items.isNullOrEmpty()) {
				_uiStateFlow.value = _uiStateFlow.value.copy(itemToDisplay = items.find { it.id == _uiStateFlow.value.id })
			} else {
				emptyListCallback.invoke()
			}
		}
	}
}