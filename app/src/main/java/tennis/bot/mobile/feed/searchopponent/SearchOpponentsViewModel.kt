package tennis.bot.mobile.feed.searchopponent

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import tennis.bot.mobile.profile.matches.MatchItem
import tennis.bot.mobile.profile.matches.MatchesUiState
import javax.inject.Inject

@HiltViewModel
class SearchOpponentsViewModel @Inject constructor(
	private val repository: SearchOpponentsRepository
): ViewModel() {

	private val _uiStateFlow = MutableStateFlow<SearchOpponentsUiState>(
		SearchOpponentsUiState.Initial
	)
	val uiStateFlow = _uiStateFlow.asStateFlow()

	fun onSearchOpponentsInput(text: CharSequence) {
		viewModelScope.launch {
			delay(500)
			repository.recordUserInput(text)
			onFetchingOpponents()
		}

	}

	private fun onFetchingOpponents(){
		viewModelScope.launch(Dispatchers.IO) {
			val opponents = repository.getOpponentItems()
			_uiStateFlow.value = SearchOpponentsUiState.OpponentDataReceived(opponents)
		}
	}

	fun getOpponentsNew(): Flow<PagingData<OpponentItem>> {
		Log.d("1234567", "New page")
		return Pager(
			config = PagingConfig(
				pageSize = 20,
				enablePlaceholders = true
			),
			pagingSourceFactory = { repository.OpponentsDataSource() }
		).flow
	}


}