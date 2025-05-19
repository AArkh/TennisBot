package tennis.bot.mobile.profile.editgamedata

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tennis.bot.mobile.profile.account.AccountPageViewModel
import tennis.bot.mobile.profile.account.UserProfileAndEnumsRepository
import javax.inject.Inject

@HiltViewModel
class EditGameDataViewModel @Inject constructor(
	private val repository: UserProfileAndEnumsRepository
): ViewModel(){

	private val _uiStateFlow = MutableStateFlow(
		EditGameDataUiState(
			emptyList()
		)
	)
	val uiStateFlow = _uiStateFlow.asStateFlow().onStart { onStartup() }

	private fun onStartup() {
		viewModelScope.launch {
			withContext(Dispatchers.IO) {
				val profileData = repository.getProfile()
				val defaultGameData = repository.defaultGameData
				val isRightHandInt = if (profileData.isRightHand == true) 1 else if (profileData.isRightHand == false) 0 else null
				val isOneBackhandInt = if (profileData.isOneBackhand == true) 1 else if (profileData.isOneBackhand == false) 0 else null
				val decodedIds = repository.getEnumsById(
					listOf(
						Pair(AccountPageViewModel.GAME_STYLE_TITLE, profileData.gameStyle),
						Pair(AccountPageViewModel.IS_RIGHTHAND_TITLE, isRightHandInt),
						Pair(AccountPageViewModel.IS_ONE_BACKHAND_TITLE, isOneBackhandInt),
						Pair(AccountPageViewModel.SURFACE_TITLE, profileData.surface),
						Pair(AccountPageViewModel.SHOES_TITLE, profileData.shoes),
						Pair(AccountPageViewModel.RACQUET_TITLE, profileData.racquet),
						Pair(AccountPageViewModel.RACQUET_STRINGS_TITLE, profileData.racquetStrings)
					)
				)

				val modifiedGameData = defaultGameData.mapIndexed { index, gameDataItem ->
					gameDataItem.copy(resultOption = decodedIds.getOrElse(index) { gameDataItem.resultOption })
				}

				_uiStateFlow.value = EditGameDataUiState(gameDataCategoriesList = modifiedGameData)
			}
		}
	}

	fun onValueChanged(gameDataType: String, value: Int) {
		viewModelScope.launch(Dispatchers.IO) {
			repository.updateGameDataValue(gameDataType, value)
			onStartup()
		}
	}
}