package tennis.bot.mobile.feed.notifications

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import retrofit2.HttpException
import tennis.bot.mobile.feed.activityfeed.FeedSealedClass
import tennis.bot.mobile.feed.searchopponent.SearchOpponentsViewModel
import tennis.bot.mobile.profile.matches.TennisSetNetwork
import tennis.bot.mobile.utils.formatDateForFeed
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor(
	private val repository: NotificationsRepository,
	@ApplicationContext private val context: Context
): ViewModel()  {

	fun getNotificationsPaginationFlow(): Flow<PagingData<NotificationData>> {
		return Pager(
			config = PagingConfig(
				pageSize = SearchOpponentsViewModel.PAGE_SIZE,
				maxSize = SearchOpponentsViewModel.PAGE_SIZE + (SearchOpponentsViewModel.PAGE_SIZE * 2),
				enablePlaceholders = true
			),
			pagingSourceFactory = { NotificationsDataSource() }
		).flow
	}


	inner class NotificationsDataSource : PagingSource<Int, NotificationData>() {
		override fun getRefreshKey(state: PagingState<Int, NotificationData>): Int { return 0 }

		override suspend fun load(params: LoadParams<Int>): LoadResult<Int, NotificationData> {
			val position = params.key ?: 0

			return try {
				val response = repository.getAllNotifications(position)
				val itemsList = response?.items?.map { it.copy(createdAt = formatDateForFeed(it.createdAt, context)) }
				val nextPosition = position + 20

				Log.d("NotificationsDataSource", "Loading page starting from position: $nextPosition")
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
}
