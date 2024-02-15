package tennis.bot.mobile.feed.searchopponent

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class SearchOpponentsViewModel @Inject constructor(
	private val repository: SearchOpponentsRepository
): ViewModel() {

	private val _uiStateFlow = MutableStateFlow<SearchOpponentsUiState>(SearchOpponentsUiState.Initial)
	val uiStateFlow = _uiStateFlow.asStateFlow()

	private val _userInput = MutableStateFlow<String?>(null)
	val userInput: StateFlow<String?> = _userInput

	fun onSearchOpponentsInput(text: CharSequence) {
		viewModelScope.launch {
			_userInput.emit(text.toString())
		}
	}

	val opponentsPager = Pager(
			config = PagingConfig(
				pageSize = 20,
				enablePlaceholders = true
			),
			pagingSourceFactory = { OpponentsDataSource() }
		).flow

	inner class OpponentsDataSource : PagingSource<Int, OpponentItem>() {
		override fun getRefreshKey(state: PagingState<Int, OpponentItem>): Int { return 0 }

		override suspend fun load(params: LoadParams<Int>): LoadResult<Int, OpponentItem> {

			val position = params.key ?: 0
			return try {
				val response = userInput.value?.let { repository.getOpponents(it, position) }
				val opponentItemsList = response?.items?.let { repository.convertToOpponentItemList(it) }
				val nextPosition = position + 20
				if (position >= (response?.totalCount ?: 0)) {
					Log.d("OpponentsDataSource", "Reached the end of data")
					return LoadResult.Page(emptyList(), prevKey = null, nextKey = null)
				} else {
					Log.d("OpponentsDataSource", "Loading page starting from position: $nextPosition")
					LoadResult.Page(
						data = opponentItemsList!!,
						prevKey = if (position == 0) null else position - params.loadSize,
						nextKey = if (nextPosition >= (response.totalCount)) null else nextPosition
					)
				}
			} catch (exception: IOException) {
				return LoadResult.Error(exception)
			} catch (exception: HttpException) {
				return LoadResult.Error(exception)
			}
		}
	}


}