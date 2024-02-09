package tennis.bot.mobile.profile.editprofile.namesurname

import android.text.InputFilter
import android.text.Spanned
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import tennis.bot.mobile.profile.editprofile.EditProfileViewModel.Companion.NAME_SURNAME_REQUEST_KEY
import tennis.bot.mobile.profile.editprofile.EditProfileViewModel.Companion.SELECTED_NAME_SURNAME
import javax.inject.Inject


class EditNameSurnameViewModel @Inject constructor(): ViewModel() {

	private val _uiStateFlow = MutableStateFlow(
		EditNameSurnameUiState(
			userNameInput = "",
			userSurnameInput = "",
			clearNameButtonVisible = false,
			clearSurnameButtonVisible = false,
			changeButtonEnabled = false
		)
	)

	val uiStateFlow = _uiStateFlow.asStateFlow()

	fun onNameInput(name: CharSequence) {
		val prevState: EditNameSurnameUiState = _uiStateFlow.value
		val isClearNameButtonVisible = name.isNotEmpty()

		_uiStateFlow.value = prevState.copy(
			userNameInput = name.toString(),
			clearNameButtonVisible = isClearNameButtonVisible,
		)
	}

	fun onSurnameInput(surname: CharSequence) {
		val prevState: EditNameSurnameUiState = _uiStateFlow.value
		val isClearSurnameButtonVisible = surname.isNotEmpty()

		_uiStateFlow.value = prevState.copy(
			userSurnameInput = surname.toString(),
			clearSurnameButtonVisible = isClearSurnameButtonVisible,
		)
	}

	fun isChangeButtonEnabled() {
		val prevState: EditNameSurnameUiState = _uiStateFlow.value
		val isNameSurnameEntered = _uiStateFlow.value.userNameInput.isNotEmpty() && _uiStateFlow.value.userSurnameInput.isNotEmpty()

		_uiStateFlow.value = prevState.copy(
			changeButtonEnabled = isNameSurnameEntered
		)
	}

	fun onUpdateNameSurname(activity: FragmentActivity, navigationCallback: () -> Unit) {
		activity.supportFragmentManager.setFragmentResult(
			NAME_SURNAME_REQUEST_KEY,
			bundleOf(SELECTED_NAME_SURNAME to "${uiStateFlow.value.userNameInput} ${uiStateFlow.value.userSurnameInput}")
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
				if (!Character.isLetter(source!![i])) {
					return ""
				}
			}
			return null
		}
	}
}
