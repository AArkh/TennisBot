package tennis.bot.mobile.feed

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
import tennis.bot.mobile.onboarding.location.LocationDataMapper
import tennis.bot.mobile.onboarding.location.LocationRepository
import tennis.bot.mobile.profile.account.AccountPageViewModel.Companion.EXPERIENCE_TITLE
import tennis.bot.mobile.profile.account.UserProfileAndEnumsRepository
import javax.inject.Inject

@HiltViewModel
class FeedBottomNavigationViewModel @Inject constructor(
	private val userProfileRepository: UserProfileAndEnumsRepository,
	private val locationRepository: LocationRepository,
	private val locationDataMapper: LocationDataMapper,
	private val repository: FeedBottomNavigationRepository,
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
					FeedAdapter.NEW_PLAYER -> { convertNewPlayerToItem(postData) }
					FeedAdapter.MATCH_REQUEST -> { convertMatchScoreToItem(postData) }
					FeedAdapter.SCORE -> { ScorePostItem(postData, postData.post as PostParent.ScorePost) }
				}
			}

			_uiStateFlow.value = _uiStateFlow.value.copy(postItems = listOfItems)
		}
	}

	suspend fun formatLocationDataForPost(cityId: Int, districtId: Int?): String? {
		val locations = locationRepository.getLocations()
		val city = locationDataMapper.findCityString(locations, cityId)
		val district = if (districtId != null) {
			locationDataMapper.findDistrictFromCity(locations, cityId, districtId)
		} else {
			null
		}
		val location = if (districtId == null) {
			city
		} else {
			"$city(${district})"
		}

		return location
	}

	private suspend fun convertNewPlayerToItem(postData: PostData): NewPlayerPostItem {
		val newPlayerPost = postData.post as PostParent.NewPlayerPost

		return NewPlayerPostItem(
			postData = postData,
			newPlayerPost = newPlayerPost,
			location = formatLocationDataForPost(newPlayerPost.cityId, null) ?: "",
			experience = userProfileRepository.getEnumById(Pair(EXPERIENCE_TITLE, newPlayerPost.experience))
		)
	}

	private suspend fun convertMatchScoreToItem(postData: PostData): MatchRequestPostItem {
		val matchRequestPost = postData.post as PostParent.MatchRequestPost

		return MatchRequestPostItem(
			postData = postData,
			matchRequestPost = matchRequestPost,
			locationSubTitle = formatLocationDataForPost(matchRequestPost.cityId, matchRequestPost.districtId) ?: "",
			experience = userProfileRepository.getEnumById(Pair(EXPERIENCE_TITLE, matchRequestPost.playerExperience))
		)
	}
}