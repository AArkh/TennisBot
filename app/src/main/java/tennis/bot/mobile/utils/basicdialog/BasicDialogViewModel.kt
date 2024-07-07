package tennis.bot.mobile.utils.basicdialog

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class BasicDialogViewModel@Inject constructor(
	savedStateHandle: SavedStateHandle
): ViewModel() {

	private val _uiStateFlow = MutableStateFlow(
		BasicDialogUiState(
			animation = savedStateHandle.get<Int>(SELECT_DIALOG_ANIMATION),
			title = savedStateHandle.get<String>(SELECT_DIALOG_TITLE),
			text = savedStateHandle.get<String>(SELECT_DIALOG_TEXT),
			topButtonText = savedStateHandle.get<String>(SELECT_DIALOG_TOP_BUTTON_TEXT),
			topButtonColor = savedStateHandle.get<Int>(SELECT_DIALOG_TOP_BUTTON_COLOR),
			bottomButtonText = savedStateHandle.get<String>(SELECT_DIALOG_BOTTOM_BUTTON_TEXT),
			bottomButtonColor = savedStateHandle.get<Int>(SELECT_DIALOG_BOTTOM_BUTTON_COLOR),
			isOneButton = savedStateHandle.get<Boolean>(SELECT_DIALOG_IS_ONE_BUTTON) ?: false,
			isCancelable = savedStateHandle.get<Boolean>(SELECT_DIALOG_IS_CANCELABLE) ?: true,
			isCanceledOnTouchOutside = savedStateHandle.get<Boolean>(SELECT_DIALOG_IS_CANCELABLE_ON_TOUCH_OUTSIDE) ?: true
		)
	)
	val uiStateFlow = _uiStateFlow.asStateFlow()

	companion object {
		const val SELECT_DIALOG_ANIMATION: String = "SELECT_DIALOG_ANIMATION"
		const val SELECT_DIALOG_TITLE: String = "SELECT_DIALOG_TITLE"
		const val SELECT_DIALOG_TEXT: String = "SELECT_DIALOG_TEXT"
		const val SELECT_DIALOG_TOP_BUTTON_TEXT: String = "SELECT_DIALOG_TOP_BUTTON_TEXT"
		const val SELECT_DIALOG_TOP_BUTTON_COLOR: String = "SELECT_DIALOG_TOP_BUTTON_COLOR"
		const val SELECT_DIALOG_BOTTOM_BUTTON_TEXT: String = "SELECT_DIALOG_BOTTOM_BUTTON_TEXT"
		const val SELECT_DIALOG_BOTTOM_BUTTON_COLOR: String = "SELECT_DIALOG_BOTTOM_BUTTON_COLOR"
		const val SELECT_DIALOG_IS_ONE_BUTTON: String = "SELECT_DIALOG_IS_ONE_BUTTON"
		const val SELECT_DIALOG_IS_CANCELABLE: String = "SELECT_DIALOG_IS_CANCELABLE"
		const val SELECT_DIALOG_IS_CANCELABLE_ON_TOUCH_OUTSIDE: String = "SELECT_DIALOG_IS_CANCELABLE_ON_TOUCH_OUTSIDE"
	}
}