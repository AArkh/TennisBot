package tennis.bot.mobile.onboarding.namegender

import android.content.Context
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import tennis.bot.mobile.R
import javax.inject.Inject

@HiltViewModel
class NameGenderViewModel @Inject constructor(@ApplicationContext private val context: Context): ViewModel(
) {

	private val errorText = context.getString(R.string.namesurname_error_text)

	private val _uiStateFlow = MutableStateFlow(
		NameGenderUiState(
			userNameInput = "",
			userSurnameInput = "",
			gender = 0,
			errorMessage = errorText,
			clearNameButtonVisible = false,
			clearSurnameButtonVisible = false,
			nextButtonEnabled = false
		)
	)

	val uiStateFlow = _uiStateFlow.asStateFlow()

	fun onNameInput(name: CharSequence) {
		val prevState: NameGenderUiState = _uiStateFlow.value
		val isClearNameButtonVisible = name.isNotEmpty()
		val errorMessage = if (name.length < 3 && name.isNotEmpty()) {
			errorText
		} else {
			null
		}
		_uiStateFlow.value = prevState.copy(
			userNameInput = name.toString(),
			errorMessage = errorMessage,
			clearNameButtonVisible = isClearNameButtonVisible,
		)
	}

	fun onSurnameInput(surname: CharSequence) {
		val prevState: NameGenderUiState = _uiStateFlow.value
		val isClearSurnameButtonVisible = surname.isNotEmpty()
		val errorMessage = if (surname.length < 3 && surname.isNotEmpty()) {
			errorText
		} else {
			null
		}

		_uiStateFlow.value = prevState.copy(
			userSurnameInput = surname.toString(),
			errorMessage = errorMessage,
			clearSurnameButtonVisible = isClearSurnameButtonVisible,
		)
	}

	fun onGenderChosen(gender: Int) {
		val prevState = _uiStateFlow.value
		_uiStateFlow.value = prevState.copy(
			gender = gender
		)
	}

	fun isNextButtonEnabled() { // gender type?
		val prevState: NameGenderUiState = _uiStateFlow.value
		val isNameOk = prevState.userNameInput.length > 3
		val isSurnameOk = prevState.userSurnameInput.length > 3
		val isGenderPicked = prevState.gender == 1 || prevState.gender == 2

		_uiStateFlow.value = prevState.copy(
			nextButtonEnabled = isNameOk && isSurnameOk && isGenderPicked
		)
	}
}