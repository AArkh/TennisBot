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

	fun onFetchingAllRequests() {
		viewModelScope.launch(Dispatchers.IO) {
			kotlin.runCatching {
				val activityPosts = repository.getAllRequests()
				_uiStateFlow.value = _uiStateFlow.value.copy(itemsList = repository.mapGameToMatchRequestPostItem(activityPosts))
			}.onFailure {
				context.showToast(context.getString(R.string.error_no_network_message))
			}
		}
	}

	fun onFetchingIncomingRequests() {
		viewModelScope.launch(Dispatchers.IO) {
			kotlin.runCatching {
				val activityPosts = repository.getIncomingRequests()
				_uiStateFlow.value = _uiStateFlow.value.copy(itemsList = repository.mapGameToMatchRequestPostItem(activityPosts))
			}.onFailure {
				context.showToast(context.getString(R.string.error_no_network_message))
			}
		}
	}

	fun onFetchingOutcomingRequests() {
		viewModelScope.launch(Dispatchers.IO) {
			kotlin.runCatching {
				val activityPosts = repository.getOutcomingRequests()
				_uiStateFlow.value = _uiStateFlow.value.copy(itemsList = repository.mapGameToMatchRequestPostItem(activityPosts))
			}.onFailure {
				context.showToast(context.getString(R.string.error_no_network_message))
			}
		}
	}

	fun onFetchingAcceptedRequests() {
		viewModelScope.launch(Dispatchers.IO) {
			kotlin.runCatching {
				val activityPosts = repository.getAcceptedRequests()
				_uiStateFlow.value = _uiStateFlow.value.copy(itemsList = repository.mapGameToMatchRequestPostItem(activityPosts))
			}.onFailure {
				context.showToast(context.getString(R.string.error_no_network_message))
			}
		}
	}

	fun onSendingRequestResponse(id: Long, comment: String?) {
		viewModelScope.launch(Dispatchers.IO) {
			kotlin.runCatching {
				repository.postRequestResponse(id, comment)
			}.onFailure {
				context.showToast("Не удалось отправить ответ на заявку")
			}.onSuccess {
				context.showToast("Отклик на заявку успешно отправлен")
			}
		}
	}

	fun onDeletingGameRequest(id: Long) {
		viewModelScope.launch(Dispatchers.IO) {
			kotlin.runCatching {
				repository.deleteGameRequest(id)
			}.onFailure {
				context.showToast("Не удалось удалить заявку")
			}.onSuccess {
				context.showToast("Заявка успешно удалена")
				onFetchingOutcomingRequests()
			}
		}
	}

}

