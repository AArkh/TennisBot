package tennis.bot.mobile.feed.game
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import tennis.bot.mobile.feed.activityfeed.MatchRequestPostItem
import tennis.bot.mobile.feed.searchopponent.SearchOpponentsViewModel
import tennis.bot.mobile.utils.showToast
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(
	private val repository: GameRepository,
	@ApplicationContext private val context: Context
): ViewModel() {

	private val _uiStateFlow = MutableStateFlow(
		GameUiState(emptyList())
	)
	val uiStateFlow = _uiStateFlow.asStateFlow()

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

	fun onDeletingGameRequest(adapter: GameAdapter, id: Long) {
		viewModelScope.launch(Dispatchers.IO) {
			kotlin.runCatching {
				repository.deleteGameRequest(id)
			}.onFailure {
				context.showToast("Не удалось удалить заявку")
			}.onSuccess {
				context.showToast("Заявка успешно удалена")
				adapter.refresh()
			}
		}
	}

	fun onDeletingMyGameResponse(adapter: GameAdapter, id: Long) {
		viewModelScope.launch(Dispatchers.IO) {
			kotlin.runCatching {
				repository.deleteMyGameResponse(id)
			}.onFailure {
				context.showToast("Не удалось удалить отклик")
			}.onSuccess {
				context.showToast("Отклик успешно удален")
				adapter.refresh()
			}
		}
	}

	fun onFetchingAllRequests(): Flow<PagingData<MatchRequestPostItem>> {
		return getGamesPaginationFlow(ALL_REQUESTS)
	}

	fun onFetchingIncomingRequests(): Flow<PagingData<MatchRequestPostItem>> {
		return getGamesPaginationFlow(INCOMING_REQUESTS)
	}

	fun onFetchingOutcomingRequests(): Flow<PagingData<MatchRequestPostItem>> {
		return getGamesPaginationFlow(OUTCOMING_REQUESTS)
	}

	fun onFetchingAcceptedRequests(): Flow<PagingData<MatchRequestPostItem>> {
		return getGamesPaginationFlow(ACCEPTED_REQUESTS)
	}

	private fun getGamesPaginationFlow(filter: String?): Flow<PagingData<MatchRequestPostItem>> {
		return Pager(
			config = PagingConfig(
				pageSize = SearchOpponentsViewModel.PAGE_SIZE,
				maxSize = SearchOpponentsViewModel.PAGE_SIZE + (SearchOpponentsViewModel.PAGE_SIZE * 2),
				enablePlaceholders = true
			),
			pagingSourceFactory = { GameDataSource(filter) }
		).flow
	}

	inner class GameDataSource(private val filter: String?) : PagingSource<Int, MatchRequestPostItem>() {
		override fun getRefreshKey(state: PagingState<Int, MatchRequestPostItem>): Int { return 0 }

		override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MatchRequestPostItem> {
			val position = params.key ?: 0

			return try {
				val response: GameBasicResponse? = when (filter) {
					ALL_REQUESTS -> { repository.getAllRequests(position) }
					INCOMING_REQUESTS -> { repository.getIncomingRequests(position) }
					OUTCOMING_REQUESTS -> { repository.getOutcomingRequests(position) }
					ACCEPTED_REQUESTS -> { repository.getAcceptedRequests(position) }
					else -> { null }

				}
				val itemsList = response?.items?.let { repository.mapGameToMatchRequestPostItem(it) }
				val nextPosition = position + 20

				LoadResult.Page(
					data = itemsList ?: emptyList(),
					prevKey = if (position == 0) null else position - params.loadSize,
					nextKey = if (nextPosition >= (response?.totalCount ?: 0)) null else nextPosition
				)

			} catch (exception: IOException) {
				return LoadResult.Error(exception)
			} catch (exception: HttpException) {
				return LoadResult.Error(exception)
			} catch (exception: NullPointerException) {
				return LoadResult.Error(exception)
			}
		}
	}

	companion object {
		private const val ALL_REQUESTS = "ALL_REQUESTS"
		private const val INCOMING_REQUESTS = "INCOMING_REQUESTS"
		private const val OUTCOMING_REQUESTS = "OUTCOMING_REQUESTS"
		private const val ACCEPTED_REQUESTS = "ACCEPTED_REQUESTS"
	}
}

