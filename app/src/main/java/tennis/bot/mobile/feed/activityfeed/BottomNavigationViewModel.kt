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
import tennis.bot.mobile.core.authentication.AuthTokenRepository
import tennis.bot.mobile.profile.account.UserProfileAndEnumsRepository
import javax.inject.Inject

@HiltViewModel
class BottomNavigationViewModel @Inject constructor(
	private val userProfileRepository: UserProfileAndEnumsRepository,
	private val tokenRepository: AuthTokenRepository,
	@ApplicationContext private val context: Context
): ViewModel()  {

	private val _uiStateFlow = MutableStateFlow(
		BottomNavigationUiState(null)	)
	val uiStateFlow = _uiStateFlow.asStateFlow().onStart {
		onFetchingProfilePicture()
	}
	val addScoreOptions = listOf(context.getString(R.string.add_score_title), context.getString(R.string.create_game_title))

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
}