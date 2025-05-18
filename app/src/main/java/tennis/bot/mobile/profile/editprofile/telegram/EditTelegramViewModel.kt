package tennis.bot.mobile.profile.editprofile.telegram

import android.text.InputFilter
import android.text.Spanned
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import tennis.bot.mobile.profile.editprofile.EditProfileViewModel
import javax.inject.Inject

class EditTelegramViewModel @Inject constructor(): ViewModel() {

	private val _uiStateFlow = MutableStateFlow(
		EditTelegramUiState(
			telegramInput = "",
			clearTelegramButtonVisible = false,
			changeButtonEnabled = false
		)
	)

	val uiStateFlow = _uiStateFlow.asStateFlow()

	fun onTelegramInput(telegram: CharSequence) {
		val prevState: EditTelegramUiState = _uiStateFlow.value
		val isClearNameButtonVisible = telegram.isNotEmpty()

		_uiStateFlow.value = prevState.copy(
			telegramInput = telegram.toString(),
			clearTelegramButtonVisible = isClearNameButtonVisible,
		)
	}

	fun isChangeButtonEnabled() {
		val prevState: EditTelegramUiState = _uiStateFlow.value
		val input = _uiStateFlow.value.telegramInput
		val isNameSurnameEntered = input.isNotEmpty() && input.length >= 5 && input.length <= 32

		_uiStateFlow.value = prevState.copy(
			changeButtonEnabled = isNameSurnameEntered
		)
	}

	fun onUpdateNameSurname(activity: FragmentActivity, navigationCallback: () -> Unit) {
		activity.supportFragmentManager.setFragmentResult(
			EditProfileViewModel.TELEGRAM_REQUEST_KEY,
			bundleOf(EditProfileViewModel.SELECTED_TELEGRAM to uiStateFlow.value.telegramInput)
		)
		navigationCallback.invoke()
	}

	inner class LetterInputFilter : InputFilter {
		override fun filter(
			source: CharSequence?,
			start: Int,
			end: Int,
			dest: Spanned?,
			dstart: Int,
			dend: Int
		): CharSequence? {
			for (i in start until end) {
				val c = source!![i]

				if (!Character.isLetterOrDigit(c) && c != '_') {
					return ""
				}
			}
			return null
		}
	}
}
