package tennis.bot.mobile.feed.game

import android.content.Context
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import tennis.bot.mobile.R
import tennis.bot.mobile.feed.activityfeed.FeedSealedClass
import tennis.bot.mobile.feed.searchopponent.OpponentItem
import tennis.bot.mobile.feed.searchopponent.SearchOpponentsViewModel
import tennis.bot.mobile.utils.showToast
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(
	private val repository: GameRepository,
	@ApplicationContext private val context: Context
): ViewModel() {

	private var gamesPager = Pager(
		config = PagingConfig(
			pageSize = SearchOpponentsViewModel.PAGE_SIZE,
			maxSize = SearchOpponentsViewModel.PAGE_SIZE + (SearchOpponentsViewModel.PAGE_SIZE * 2), // since paging library stores all received pages using memory so we add this thing, to prevent in to store too much. the value is one recommended in docs
			enablePlaceholders = true
		),
		pagingSourceFactory = { GameDataSource(filter = ALL_REQUESTS) }
	).flow

	fun filterPlayerOpponent(arrayOfOpponents: Array<OpponentItem>): Array<OpponentItem> {
		val id = repository.getPlayerId()
		val newArray = arrayOfOpponents.filter {
			it.id != id
		}.toTypedArray()
		return newArray
	}

	fun onSendingRequestResponse(id: Long, comment: String?) {
		viewModelScope.launch(Dispatchers.IO) {
			kotlin.runCatching {
				if (!repository.postRequestResponse(id, comment)) {
					throw IllegalArgumentException("Failed to postRequestResponse")
				}
			}.onFailure {
				FirebaseCrashlytics.getInstance().recordException(it)
				context.showToast(context.getString(R.string.request_response_failure))
			}.onSuccess {
				context.showToast(context.getString(R.string.request_response_success))
			}
		}
	}

	fun onAcceptingInvite(id: Long, targetPlayerId: Long, successCallBack: () -> Unit) {
		viewModelScope.launch(Dispatchers.IO) {
			kotlin.runCatching {
				if (!repository.postInviteAccept(id, targetPlayerId)) {
					throw IllegalArgumentException("Failed to onAcceptingInvite")
				}
			}.onFailure {
				FirebaseCrashlytics.getInstance().recordException(it)
				context.showToast(context.getString(R.string.response_invite_failure))
			}.onSuccess {
				context.showToast(context.getString(R.string.response_invite_accept))
				successCallBack.invoke()
			}
		}
	}

	fun onDecliningInvite(id: Long, targetPlayerId: Long, successCallBack: () -> Unit) {
		viewModelScope.launch(Dispatchers.IO) {
			kotlin.runCatching {
				if (!repository.postInviteDecline(id, targetPlayerId)) {
					throw IllegalArgumentException("Failed to onDecliningInvite")
				}
			}.onFailure {
				FirebaseCrashlytics.getInstance().recordException(it)
				context.showToast(context.getString(R.string.response_invite_failure))
			}.onSuccess {
				context.showToast(context.getString(R.string.response_invite_decline))
				successCallBack.invoke()
			}
		}
	}

	fun onDeletingGameRequest(adapter: GameAdapter, id: Long) {
		viewModelScope.launch(Dispatchers.IO) {
			kotlin.runCatching {
				if (!repository.deleteGameRequest(id)) {
					throw IllegalArgumentException("Failed to deleteGameRequest")
				}
			}.onFailure {
				FirebaseCrashlytics.getInstance().recordException(it)
				context.showToast(context.getString(R.string.request_delete_failure))
			}.onSuccess {
				context.showToast(context.getString(R.string.request_delete_success))
				adapter.refresh()
			}
		}
	}

	fun onDeletingMyGameResponse(adapter: GameAdapter, id: Long) {
		viewModelScope.launch(Dispatchers.IO) {
			kotlin.runCatching {
				if (!repository.deleteMyGameResponse(id)) {
					throw IllegalArgumentException("Failed to deleteMyGameResponse")
				}
			}.onFailure {
				FirebaseCrashlytics.getInstance().recordException(it)
				context.showToast(context.getString(R.string.response_delete_failure))
			}.onSuccess {
				context.showToast(context.getString(R.string.response_delete_success))
				adapter.refresh()
			}
		}
	}

	fun onFetchingAllRequests(): Flow<PagingData<FeedSealedClass>> {
		getGamesPaginationFlow(ALL_REQUESTS)
		return gamesPager
	}

	fun onFetchingIncomingRequests(): Flow<PagingData<FeedSealedClass>> {
		getGamesPaginationFlow(INCOMING_REQUESTS)
		return gamesPager
	}

	fun onFetchingOutcomingRequests(): Flow<PagingData<FeedSealedClass>> {
		getGamesPaginationFlow(OUTCOMING_REQUESTS)
		return gamesPager
	}

	fun onFetchingAcceptedRequests(): Flow<PagingData<FeedSealedClass>> {
		getGamesPaginationFlow(ACCEPTED_REQUESTS)
		return gamesPager
	}

	private fun getGamesPaginationFlow(filter: String?) {
		if (filter != ACCEPTED_REQUESTS) {
			gamesPager = Pager(
				config = PagingConfig(
					pageSize = SearchOpponentsViewModel.PAGE_SIZE,
					maxSize = SearchOpponentsViewModel.PAGE_SIZE + (SearchOpponentsViewModel.PAGE_SIZE * 2),
					enablePlaceholders = true
				),
				pagingSourceFactory = { GameDataSource(filter) }
			).flow
		} else {
			gamesPager = Pager(
				config = PagingConfig(
					pageSize = SearchOpponentsViewModel.PAGE_SIZE,
					maxSize = SearchOpponentsViewModel.PAGE_SIZE + (SearchOpponentsViewModel.PAGE_SIZE * 2),
					enablePlaceholders = true
				),
				pagingSourceFactory = { AcceptedGamesDataSource() }
			).flow
		}
	}

	fun onInsertScoreButtonClicked(activity: FragmentActivity, opponentsList: Array<OpponentItem>, navigationCallback: () -> Unit) {
		activity.supportFragmentManager.setFragmentResult(
			SearchOpponentsViewModel.OPPONENT_PICKED_REQUEST_KEY,
			bundleOf(
				SearchOpponentsViewModel.SELECTED_OPPONENT_KEY to opponentsList
			)
		)
		navigationCallback.invoke()
	}

	inner class GameDataSource(private val filter: String?) : PagingSource<Int, FeedSealedClass>() {
		override fun getRefreshKey(state: PagingState<Int, FeedSealedClass>): Int { return 0 }

		override suspend fun load(params: LoadParams<Int>): LoadResult<Int, FeedSealedClass> {
			val position = params.key ?: 0

			return try {
				val response: GameBasicResponse? = when (filter) {
					ALL_REQUESTS -> { repository.getAllRequests(position) }
					INCOMING_REQUESTS -> { repository.getIncomingRequests(position) }
					OUTCOMING_REQUESTS -> { repository.getOutcomingRequests(position) }
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
				FirebaseCrashlytics.getInstance().recordException(exception)
				return LoadResult.Error(exception)
			} catch (exception: HttpException) {
				FirebaseCrashlytics.getInstance().recordException(exception)
				return LoadResult.Error(exception)
			} catch (exception: NullPointerException) {
				FirebaseCrashlytics.getInstance().recordException(exception)
				return LoadResult.Error(exception)
			}
		}
	}

	inner class AcceptedGamesDataSource : PagingSource<Int, FeedSealedClass>() {
		override fun getRefreshKey(state: PagingState<Int, FeedSealedClass>): Int { return 0 }

		override suspend fun load(params: LoadParams<Int>): LoadResult<Int, FeedSealedClass> {
			val position = params.key ?: 0

			return try {
				val response: GameAcceptedResponse? = repository.getAcceptedRequests(position)
				val itemsList = response?.items?.let { repository.mapAcceptedGameToPostItem(it) }
				val nextPosition = position + 20

				LoadResult.Page(
					data = itemsList ?: emptyList(),
					prevKey = if (position == 0) null else position - params.loadSize,
					nextKey = if (nextPosition >= (response?.totalCount ?: 0)) null else nextPosition
				)

			}  catch (exception: IOException) {
				FirebaseCrashlytics.getInstance().recordException(exception)
				return LoadResult.Error(exception)
			} catch (exception: HttpException) {
				FirebaseCrashlytics.getInstance().recordException(exception)
				return LoadResult.Error(exception)
			} catch (exception: NullPointerException) {
				FirebaseCrashlytics.getInstance().recordException(exception)
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

