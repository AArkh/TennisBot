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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
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
	}

	private val _uiStateFlow = MutableStateFlow<SearchOpponentsUiState>(SearchOpponentsUiState.Initial)
	val uiStateFlow = _uiStateFlow.asStateFlow()

	private val _userInput = MutableStateFlow<String?>(null)
	val userInput: Flow<String?> = _userInput.debounce(250L)

	val opponentsPager = Pager(
			config = PagingConfig(
				pageSize = 20,
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
				val response = _userInput.value?.let { repository.getOpponents(it, position) }
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
				Log.e("1234567", "load: io", exception)
				return LoadResult.Error(exception)
			} catch (exception: HttpException) {
				Log.e("1234567", "load: gttp", exception)
				return LoadResult.Error(exception)
			} catch (e: Exception) {
				Log.e("1234567", "load: general", e)
				return LoadResult.Error(e)
			}
		}
	}
}