package tennis.bot.mobile.feed.activityfeed

import android.content.Context
import android.util.Log
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
import tennis.bot.mobile.core.authentication.AuthTokenRepository
import tennis.bot.mobile.profile.account.UserProfileAndEnumsRepository
import javax.inject.Inject

@HiltViewModel
class FeedBottomNavigationViewModel @Inject constructor(
	private val userProfileRepository: UserProfileAndEnumsRepository,
	private val tokenRepository: AuthTokenRepository,
	private val repository: FeedBottomNavigationRepository,
	private val feedPostsMapper: FeedPostsMapper,
	@ApplicationContext private val context: Context
): ViewModel()  {

	private val _uiStateFlow = MutableStateFlow(
		FeedBottomNavigationUiState(
			null
		)
	)
	val uiStateFlow = _uiStateFlow.asStateFlow().onStart {
		onFetchingProfilePicture()
		onFetchingActivities()
	}
	val addScoreOptions = listOf(context.getString(R.string.add_score_title), context.getString(R.string.create_game_title))

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

	private fun onFetchingProfilePicture(){
		viewModelScope.launch (Dispatchers.IO) {
			kotlin.runCatching {
				_uiStateFlow.value = _uiStateFlow.value.copy(playerPicture = userProfileRepository.getProfile().photo)
			}.onFailure {
				Log.d("123456", "we have a problem")
				tokenRepository.triggerUnAuthFlow(true)
			}
		}
	}

	private fun onFetchingActivities() {
		viewModelScope.launch (Dispatchers.IO) {
			val activityPosts = repository.getActivities()
			convertPostsToItems(activityPosts)
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