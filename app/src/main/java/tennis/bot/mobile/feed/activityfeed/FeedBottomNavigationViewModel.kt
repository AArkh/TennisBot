package tennis.bot.mobile.feed.activityfeed

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import androidx.annotation.WorkerThread
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
import tennis.bot.mobile.profile.account.UserProfileAndEnumsRepository
import javax.inject.Inject

@HiltViewModel
class FeedBottomNavigationViewModel @Inject constructor(
	private val userProfileRepository: UserProfileAndEnumsRepository,
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

	private fun onFetchingProfilePicture(){
		viewModelScope.launch(Dispatchers.IO) {
			_uiStateFlow.value = _uiStateFlow.value.copy(playerPicture = userProfileRepository.getProfile().photo)
		}
	}

	fun onFetchingActivities() {
		viewModelScope.launch (Dispatchers.IO) {
			val activityPosts = repository.getActivities()
			convertPostsToItems(activityPosts)
		}
	}

	private fun convertPostsToItems(list: List<PostData>?) {
		if (list == null) return

		viewModelScope.launch(Dispatchers.IO) {
			val listOfItems = mutableListOf<CoreUtilsItem>()

			for(postData in list) {
				when(postData.postType) {
					FeedAdapter.NEW_PLAYER -> { listOfItems.add(feedPostsMapper.convertToNewPlayerPostItem(postData)) }
					FeedAdapter.MATCH_REQUEST -> {  listOfItems.add(feedPostsMapper.convertToMatchRequestPostItem(postData)) }
					FeedAdapter.SCORE -> {  listOfItems.add(feedPostsMapper.convertToScorePostItem(postData)) }
				}
			}

			_uiStateFlow.value = _uiStateFlow.value.copy(postItems = listOfItems)
			loadMediaContent()
		}
	}

	@WorkerThread
	private fun loadMediaContent() {
		viewModelScope.launch(Dispatchers.IO) {
			val newList = _uiStateFlow.value.postItems.map { scorePostItem ->
				when (scorePostItem) {
					is ScorePostItem -> {
						scorePostItem.copy(feedMediaItemsList = createListOfMedia(scorePostItem))
					}
					else -> scorePostItem
				}
			}
			_uiStateFlow.value = _uiStateFlow.value.copy(postItems = newList)
		}
	}

	private fun createListOfMedia(item: ScorePostItem): List<FeedMediaItem> {
		val theList = mutableListOf<FeedMediaItem>()
		if (item.video != null && item.photo != null) {
//			val (thumbnail, duration) = getVideoThumbnailAndDuration(item.video)
			theList.add(
				FeedMediaItem(
					mediaUrl = item.video,
//					videoThumbnail = thumbnail,
//					duration = duration,
					isVideo = true)
			)
			theList.add(FeedMediaItem(item.photo))
		} else if (item.video != null) {
//			val (thumbnail, duration) = getVideoThumbnailAndDuration(item.video)
			theList.add(
				FeedMediaItem(
					mediaUrl = item.video,
//					videoThumbnail = thumbnail,
//					duration = duration,
					isVideo = true)
			)
		} else if (item.photo != null) {
			theList.add(FeedMediaItem(item.photo))
		}
		theList.add(FeedMediaItem(item.player1?.photo))
		theList.add(FeedMediaItem(item.player2?.photo))
		if (item.player3 != null) {
			theList.add(FeedMediaItem(item.player3.photo))
			theList.add(FeedMediaItem(item.player4?.photo))
		}

		return theList.toList()
	}

	private fun getVideoThumbnailAndDuration(videoUrl: String?): Pair<Bitmap?, String> {
		val retriever = MediaMetadataRetriever()
		retriever.setDataSource(videoUrl)
		val bitmap: Bitmap? = retriever.frameAtTime

		try {
			val durationString = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
			val durationInMillis = durationString?.toLongOrNull() ?: 0

			val minutes = (durationInMillis / 1000 / 60).toInt()
			val seconds = (durationInMillis / 1000 % 60).toInt()

			retriever.release()

			return Pair(bitmap, String.format("%02d:%02d", minutes, seconds))
		} catch (e: Exception) {
			e.printStackTrace()
		} finally {
			retriever.release()
		}
		return Pair(bitmap, "00:00")
	}
}