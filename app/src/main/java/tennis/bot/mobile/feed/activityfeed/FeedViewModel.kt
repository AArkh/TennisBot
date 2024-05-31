package tennis.bot.mobile.feed.activityfeed

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
import tennis.bot.mobile.core.CoreUtilsItem
import tennis.bot.mobile.utils.showToast
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
	private val repository: FeedRepository,
	private val feedPostsMapper: FeedPostsMapper,
	@ApplicationContext private val context: Context
): ViewModel()  {

	private val _uiStateFlow = MutableStateFlow(
		FeedUiState()
	)
	val uiStateFlow = _uiStateFlow.asStateFlow().onStart {
		onFetchingActivities()
	}

	fun onLikeButtonPressed(isLike: Boolean, postId: Long) {
		viewModelScope.launch {
			kotlin.runCatching {
				if (isLike) {
					repository.postLike(postId)
				} else {
					repository.postUnlike(postId)
				}
			}.onSuccess {
				onFetchingActivities()
			}
		}
	}

	private fun onFetchingActivities() {
		viewModelScope.launch (Dispatchers.IO) {
			kotlin.runCatching {
				val activityPosts = repository.getActivities()
				convertPostsToItems(activityPosts)
			}.onFailure {
				context.showToast(context.getString(R.string.error_no_network_message))
			}
		}
	}

	private suspend fun convertPostsToItems(list: List<PostData>?) {
		if (list == null) return
		val listOfItems = mutableListOf<CoreUtilsItem>()

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
		_uiStateFlow.value = _uiStateFlow.value.copy(postItems = listOfItems)
	}
}