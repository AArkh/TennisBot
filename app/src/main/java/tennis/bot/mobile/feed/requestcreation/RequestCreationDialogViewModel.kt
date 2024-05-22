package tennis.bot.mobile.feed.requestcreation

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tennis.bot.mobile.R
import tennis.bot.mobile.profile.account.UserProfileAndEnumsRepository
import tennis.bot.mobile.profile.editgamedata.EditGameDataDialogUiState
import tennis.bot.mobile.profile.editgamedata.TextOnlyItem
import javax.inject.Inject

@HiltViewModel
class RequestCreationDialogViewModel @Inject constructor(
	private val repository: UserProfileAndEnumsRepository,
	savedStateHandle: SavedStateHandle,
	@ApplicationContext private val context: Context
): ViewModel() {

	private val currentAction = savedStateHandle.get<Int>(RequestCreationFragment.REQUEST_DIALOG_SELECT_ACTION_KEY) ?: ""

	private val _uiStateFlow = MutableStateFlow(
		EditGameDataDialogUiState(
			"",
			emptyList()
		)
	)
	val uiStateFlow = _uiStateFlow.asStateFlow().onStart { onLoadingSelectedList() }

	private fun onLoadingSelectedList() {
		when (currentAction) {
			GAME_TYPE -> {
				loadOptionsListByTitle(GAME_TYPE_TITLE)
			}
			GAME_PAY -> {
				loadOptionsListByTitle(GAME_PAY_TITLE)
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
						title = when(type) {
							GAME_TYPE_TITLE -> { context.getString(R.string.gametype_title) }
							GAME_PAY_TITLE -> { context.getString(R.string.payment_title) }
							else -> ""
						},
						optionsList = enumOptionsList
					)
				}
			}
		}
	}

	fun onOptionPicked(activity: FragmentActivity, title: String, optionInt: Int) {
		viewModelScope.launch (Dispatchers.IO) {
			val option = repository.getEnumById(
				Pair(
					when (currentAction) {
						GAME_TYPE -> GAME_TYPE_TITLE
						GAME_PAY -> GAME_PAY_TITLE
						else -> ""
					}, optionInt))
			activity.supportFragmentManager.setFragmentResult(
				RequestCreationFragment.REQUEST_DIALOG_REQUEST_KEY,
				Bundle().apply {
					putString(RequestCreationFragment.REQUEST_DIALOG_TITLE, title)
					putInt(RequestCreationFragment.REQUEST_DIALOG_PICKED_OPTION_ID, optionInt)
					putString(RequestCreationFragment.REQUEST_DIALOG_PICKED_OPTION, option)
				}
			)
		}
	}

	companion object {
		private const val GAME_TYPE = 1
		private const val GAME_PAY = 2
		const val GAME_TYPE_TITLE = "gameType"
		const val GAME_PAY_TITLE = "gamePay"
	}
}