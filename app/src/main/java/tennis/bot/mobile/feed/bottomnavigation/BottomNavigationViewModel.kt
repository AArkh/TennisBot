package tennis.bot.mobile.feed.bottomnavigation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
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
import tennis.bot.mobile.utils.showToast
import javax.inject.Inject

@HiltViewModel
class BottomNavigationViewModel @Inject constructor(
	private val userProfileRepository: UserProfileAndEnumsRepository,
	private val tokenRepository: AuthTokenRepository,
	@ApplicationContext private val context: Context
): ViewModel()  {

	private val _uiStateFlow = MutableStateFlow(
		BottomNavigationUiState(
			title = context.getString(R.string.feed_toolbar_title),
			currentItemId = R.id.feed_item,
			playerPicture = null
		)
	)
	val uiStateFlow = _uiStateFlow.asStateFlow().onStart {
		onFetchingProfilePicture()
	}
	val addScoreOptions = listOf(context.getString(R.string.add_score_title), context.getString(R.string.create_game_title))

	private fun onFetchingProfilePicture(){
		viewModelScope.launch (Dispatchers.IO) {
			kotlin.runCatching {
				_uiStateFlow.value = _uiStateFlow.value.copy(playerPicture = userProfileRepository.getProfile().photoUrl)
			}.onFailure {
				if (it is UninitializedPropertyAccessException) { // cachedProfile can't load so the this exception is triggered
					tokenRepository.triggerUnAuthFlow(false)
					context.showToast(context.getString(R.string.insufficient_access_text))
				} else {
					tokenRepository.triggerUnAuthFlow(true)
				}
				FirebaseCrashlytics.getInstance().recordException(it)
			}
		}
	}

	fun onCheckingAppVersion(appVersionName: String, updateDialogCallback: (isBroken: Boolean) -> Unit) {
		viewModelScope.launch (Dispatchers.IO) {
			kotlin.runCatching { // removed onFailure because other components signal the same thing
				val response = userProfileRepository.getAppVersion(appVersionName)
				if (response?.updated == true) {
					response.broken.let { updateDialogCallback.invoke(it) }
				}
			}
		}
	}

	fun onItemChosen(fragmentTag: String) {
		when (fragmentTag) {
			FRAGMENT_FEED -> {
				_uiStateFlow.value = _uiStateFlow.value.copy(title = context.getString(R.string.feed_toolbar_title), currentItemId = R.id.feed_item)
			}
			FRAGMENT_GAME -> {
				_uiStateFlow.value = _uiStateFlow.value.copy(title = context.getString(R.string.game_toolbar_title), currentItemId = R.id.game_item)
			}
			else -> {}
		}
	}

	companion object {
		const val FRAGMENT_FEED = "FRAGMENT_FEED"
		const val FRAGMENT_GAME = "FRAGMENT_GAME"
		const val FRAGMENT_CHAT = "FRAGMENT_CHAT"
		const val FRAGMENT_TOURNAMENT = "FRAGMENT_TOURNAMENT"
		const val FRAGMENT_EMPTY = "FRAGMENT_EMPTY"
	}
}