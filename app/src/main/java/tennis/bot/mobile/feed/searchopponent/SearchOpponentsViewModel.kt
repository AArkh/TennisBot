package tennis.bot.mobile.feed.searchopponent

import android.util.Log
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
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

	companion object {
		const val OPPONENT_PICKED_REQUEST_KEY = "OPPONENT_PICKED_REQUEST_KEY"
		const val SELECTED_OPPONENT_NAME_KEY = "SELECTED_OPPONENT_NAME_KEY"
		const val SELECTED_OPPONENT_PHOTO_KEY = "SELECTED_OPPONENT_PHOTO_KEY"
		const val SELECTED_OPPONENT_ID_KEY = "SELECTED_OPPONENT_ID_KEY"
		const val PAGE_SIZE = 20
	}

	private val _uiStateFlow = MutableStateFlow<SearchOpponentsUiState>(SearchOpponentsUiState.Initial)
	val uiStateFlow = _uiStateFlow.asStateFlow()

	private val _userInput = MutableStateFlow<String?>(null)
	val userInput: StateFlow<String?> = _userInput

	val opponentsPager = Pager(
			config = PagingConfig(
				pageSize = PAGE_SIZE,
				maxSize = PAGE_SIZE + (PAGE_SIZE * 2), // since paging library stores all received pages using memory so we add this thing, to prevent in to store too much. the value is recommended in docs
				enablePlaceholders = true
			),
			pagingSourceFactory = { OpponentsDataSource() }
		).flow

	fun onSearchOpponentsInput(text: CharSequence) {
		viewModelScope.launch(Dispatchers.IO) {
			_userInput.emit(text.toString())
		}
	}

	fun onOpponentPicked(opponent: OpponentItem, activity: FragmentActivity) {
		activity.supportFragmentManager.setFragmentResult(
			OPPONENT_PICKED_REQUEST_KEY,
			bundleOf(
				SELECTED_OPPONENT_ID_KEY to opponent.id,
				SELECTED_OPPONENT_NAME_KEY to opponent.nameSurname,
				SELECTED_OPPONENT_PHOTO_KEY to opponent.profilePicture)
		)
	}

	inner class OpponentsDataSource : PagingSource<Int, OpponentItem>() {
		override fun getRefreshKey(state: PagingState<Int, OpponentItem>): Int { return 0 }

		override suspend fun load(params: LoadParams<Int>): LoadResult<Int, OpponentItem> {
			val position = params.key ?: 0

			return try {
				val response = userInput.value?.let { repository.getOpponents(it, position) }
				val opponentItemsList = response?.items?.let { repository.convertToOpponentItemList(it) }
				val nextPosition = position + 20

				Log.d("OpponentsDataSource", "Loading page starting from position: $nextPosition")
				LoadResult.Page(
					data = opponentItemsList ?: emptyList(),
					prevKey = if (position == 0) null else position - params.loadSize,
					nextKey = if (nextPosition >= (response?.totalCount ?: 0)) null else nextPosition
				)

			} catch (exception: IOException) {
				Log.e("SearchOpponent", "SearchOpponent = $exception")
				return LoadResult.Error(exception)
			} catch (exception: HttpException) {
				Log.e("SearchOpponent", "HttpException = $exception")
				return LoadResult.Error(exception)
			}
		}
	}


}