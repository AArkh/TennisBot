package tennis.bot.mobile.feed.activityfeed

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import tennis.bot.mobile.feed.searchopponent.SearchOpponentsViewModel
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
	private val repository: FeedRepository,
	private val feedPostsMapper: FeedPostsMapper
): ViewModel()  {

	fun onLikeButtonPressed(isLike: Boolean, postId: Long) {
		viewModelScope.launch {
			kotlin.runCatching {
				if (isLike) {
					repository.postLike(postId)
				} else {
					repository.postUnlike(postId)
				}
			}
		}
	}

	private suspend fun convertPostsToItems(list: List<PostData>?): List<FeedSealedClass> {
		if (list == null) return emptyList()
		val listOfItems = mutableListOf<FeedSealedClass>()

		for (postData in list) {
			when (postData.postType) {
				FeedAdapter.NEW_PLAYER -> {
					listOfItems.add(feedPostsMapper.convertToNewPlayerPostItem(postData))
				}

				FeedAdapter.MATCH_REQUEST -> {
					listOfItems.add(feedPostsMapper.convertToMatchRequestPostItem(postData))
				}

				FeedAdapter.SCORE -> {
					listOfItems.add(feedPostsMapper.convertToScorePostItem(postData))
				}
			}
		}
		return listOfItems.toList()
	}

	fun getFeedPaginationFlow(): Flow<PagingData<FeedSealedClass>> {
		return Pager(
			config = PagingConfig(
				pageSize = SearchOpponentsViewModel.PAGE_SIZE,
				maxSize = SearchOpponentsViewModel.PAGE_SIZE + (SearchOpponentsViewModel.PAGE_SIZE * 2),
				enablePlaceholders = true
			),
			pagingSourceFactory = { FeedDataSource() }
		).flow
	}

	inner class FeedDataSource : PagingSource<Int, FeedSealedClass>() {
		override fun getRefreshKey(state: PagingState<Int, FeedSealedClass>): Int { return 0 }

		override suspend fun load(params: LoadParams<Int>): LoadResult<Int, FeedSealedClass> {
			val position = params.key ?: 0

			return try {
				val response = repository.getActivities(position)
				val itemsList = response?.items?.let { convertPostsToItems(it) }
				val nextPosition = position + 20

				Log.d("FeedDataSource", "Loading page starting from position: $nextPosition")
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

}