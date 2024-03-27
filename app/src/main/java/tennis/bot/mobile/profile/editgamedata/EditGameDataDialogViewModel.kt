package tennis.bot.mobile.profile.editgamedata

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.SavedStateHandle
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
import tennis.bot.mobile.profile.editgamedata.EditGameDataFragment.Companion.GAMEDATA_DIALOG_PICKED_POSITION
import tennis.bot.mobile.profile.editgamedata.EditGameDataFragment.Companion.GAMEDATA_DIALOG_TITLE
import javax.inject.Inject

@HiltViewModel
class EditGameDataDialogViewModel @Inject constructor(
	private val repository: UserProfileAndEnumsRepository,
	private val savedStateHandle: SavedStateHandle,
): ViewModel() {

	private val currentAction = savedStateHandle.get<Int>(EditGameDataFragment.GAMEDATA_DIALOG_SELECT_ACTION_KEY) ?: ""

	private val _uiStateFlow = MutableStateFlow(
		EditGameDataDialogUiState(
			"",
			emptyList()
		)
	)
	val uiStateFlow = _uiStateFlow.asStateFlow().onStart { onLoadingSelectedList() }

	private fun onLoadingSelectedList() {
		when (currentAction) {
			GAME_STYLE -> {
				loadOptionsListByTitle(AccountPageViewModel.GAME_STYLE_TITLE)
			}
			IS_RIGHT_HAND -> {
				loadOptionsListByTitle(AccountPageViewModel.IS_RIGHTHAND_TITLE)
			}
			IS_ONE_BACKHAND -> {
				loadOptionsListByTitle(AccountPageViewModel.IS_ONE_BACKHAND_TITLE)
			}
			SURFACE -> {
				loadOptionsListByTitle(AccountPageViewModel.SURFACE_TITLE)
			}
			SHOES -> {
				loadOptionsListByTitle(AccountPageViewModel.SHOES_TITLE)
			}
			RACQUET -> {
				loadOptionsListByTitle(AccountPageViewModel.RACQUET_TITLE)
			}
			RACQUET_STRINGS -> {
				loadOptionsListByTitle(AccountPageViewModel.RACQUET_STRINGS_TITLE)
			}
		}
	}

	private fun loadOptionsListByTitle(type: String) {
		viewModelScope.launch {
			withContext(Dispatchers.IO) {
				val enumList = repository.getEnumGroup(type)
				val enumOptionsList = enumList?.map { enumData ->
					TextOnlyItem(
						id = enumData.id,
						title = enumData.name
					)
				}
				if (enumOptionsList != null) {
					_uiStateFlow.value = EditGameDataDialogUiState(
						title = repository.getEnumTitle(type),
						optionsList = enumOptionsList
					)
				}
			}
		}
	}

	fun onOptionPicked(activity: FragmentActivity, title: String, position: Int) {
		activity.supportFragmentManager.setFragmentResult(
			EditGameDataFragment.GAMEDATA_DIALOG_REQUEST_KEY,
			Bundle().apply {
				putString(GAMEDATA_DIALOG_TITLE, title)
				putInt(GAMEDATA_DIALOG_PICKED_POSITION, position)
			}
		)
	}

	companion object {
		private const val GAME_STYLE = 0
		private const val IS_RIGHT_HAND = 1
		private const val IS_ONE_BACKHAND = 2
		private const val SURFACE = 3
		private const val SHOES = 4
		private const val RACQUET = 5
		private const val RACQUET_STRINGS = 6
	}
}

