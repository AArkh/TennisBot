package tennis.bot.mobile.feed.game

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import retrofit2.HttpException
import tennis.bot.mobile.feed.searchopponent.OpponentItem
import tennis.bot.mobile.feed.searchopponent.SearchOpponentsRepository
import tennis.bot.mobile.feed.searchopponent.SearchOpponentsViewModel
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
open class PlayersViewModel @Inject constructor(
	private val repository: SearchOpponentsRepository
): ViewModel() {

	val playersPager = Pager(
		config = PagingConfig(
			pageSize = SearchOpponentsViewModel.PAGE_SIZE,
			maxSize = SearchOpponentsViewModel.PAGE_SIZE + (SearchOpponentsViewModel.PAGE_SIZE * 2), // since paging library stores all received pages using memory so we add this thing, to prevent in to store too much. the value is one recommended in docs
			enablePlaceholders = true
		),
		pagingSourceFactory = { OpponentsDataSource() }
	).flow

	inner class OpponentsDataSource : PagingSource<Int, OpponentItem>() {
		override fun getRefreshKey(state: PagingState<Int, OpponentItem>): Int {
			return 0
		}

		override suspend fun load(params: LoadParams<Int>): LoadResult<Int, OpponentItem> {
			val position = params.key ?: 0

			return try {
				val response = repository.getOpponents("", position)
				val opponentItemsList = response?.items?.let { repository.convertToOpponentItemList(it) }
				val nextPosition = position + 20

				Log.d("OpponentsDataSource", "Loading page starting from position: $nextPosition")
				LoadResult.Page(
					data = opponentItemsList ?: emptyList(),
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
}