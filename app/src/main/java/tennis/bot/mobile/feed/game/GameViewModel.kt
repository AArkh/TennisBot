package tennis.bot.mobile.feed.game

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import tennis.bot.mobile.R
import tennis.bot.mobile.utils.showToast
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(
	private val repository: GameRepository,
	@ApplicationContext private val context: Context
): ViewModel() {

	private val _uiStateFlow = MutableStateFlow(
		GameUiState(emptyList())
	)
	val uiStateFlow = _uiStateFlow.asStateFlow().onStart { onFetchingAllRequests() }

	private fun onFetchingAllRequests() {
		viewModelScope.launch (Dispatchers.IO) {
				val activityPosts = repository.getAllRequests()
				_uiStateFlow.value = _uiStateFlow.value.copy(itemsList = repository.mapGameToMatchRequestPostItem(activityPosts))
//			}.onFailure {
//				context.showToast(context.getString(R.string.error_no_network_message))
//			}
		}
	}
}