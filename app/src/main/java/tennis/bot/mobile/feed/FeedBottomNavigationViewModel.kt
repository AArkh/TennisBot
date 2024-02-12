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
import tennis.bot.mobile.profile.account.UserProfileAndEnumsRepository
import javax.inject.Inject

@HiltViewModel
class FeedBottomNavigationViewModel @Inject constructor(
	private val repository: UserProfileAndEnumsRepository,
	@ApplicationContext private val context: Context
): ViewModel()  {

	private val _uiStateFlow = MutableStateFlow(
		FeedBottomNavigationUiState(
			null
		)
	)
	val uiStateFlow = _uiStateFlow.asStateFlow().onStart { onFetchingProfilePicture() }
	val addScoreOptions = listOf(context.getString(R.string.add_score_title), context.getString(R.string.create_game_title))

	private fun onFetchingProfilePicture(){
		viewModelScope.launch(Dispatchers.IO) {
			_uiStateFlow.value = _uiStateFlow.value.copy(playerPicture = repository.getProfile().photo)
		}
	}
}