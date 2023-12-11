package tennis.bot.mobile.onboarding.namegender

import android.content.Context
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import tennis.bot.mobile.onboarding.survey.AccountInfoRepository
import tennis.bot.mobile.onboarding.survey.AccountInfoRepository.Companion.IS_MALE_HEADER
import tennis.bot.mobile.onboarding.survey.AccountInfoRepository.Companion.NAME_HEADER
import tennis.bot.mobile.onboarding.survey.AccountInfoRepository.Companion.SURNAME_HEADER
import javax.inject.Inject

@HiltViewModel
class NameGenderViewModel @Inject constructor(
	@ApplicationContext private val context: Context,
	private val accountInfo: AccountInfoRepository): ViewModel(
) {

	private val _uiStateFlow = MutableStateFlow(
		NameGenderUiState(
			userNameInput = "",
			userSurnameInput = "",
			gender = 0,
			clearNameButtonVisible = false,
			clearSurnameButtonVisible = false,
			nextButtonEnabled = false
		)
	)

	val uiStateFlow = _uiStateFlow.asStateFlow()

	fun onNameInput(name: CharSequence) {
		val prevState: NameGenderUiState = _uiStateFlow.value
		val isClearNameButtonVisible = name.isNotEmpty()

		_uiStateFlow.value = prevState.copy(
			userNameInput = name.toString(),
			clearNameButtonVisible = isClearNameButtonVisible,
		)
	}

	fun onSurnameInput(surname: CharSequence) {
		val prevState: NameGenderUiState = _uiStateFlow.value
		val isClearSurnameButtonVisible = surname.isNotEmpty()

		_uiStateFlow.value = prevState.copy(
			userSurnameInput = surname.toString(),
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
		val isNameOk = prevState.userNameInput.length >= 3
		val isSurnameOk = prevState.userSurnameInput.length >= 3
		val isGenderPicked = prevState.gender == 1 || prevState.gender == 2

		_uiStateFlow.value = prevState.copy(
			nextButtonEnabled = isNameOk && isSurnameOk && isGenderPicked
		)
	}

	fun recordAccountValues() {
		repo.saveGender(true)
		repo.saveUserName(_uiStateFlow.value.userNameInput.toString())

		// в месте запроса
		repo.getUserGender() // true

		accountInfo.putStringInSharedPref(NAME_HEADER, _uiStateFlow.value.userNameInput.toString())
		accountInfo.putStringInSharedPref(SURNAME_HEADER, _uiStateFlow.value.userNameInput.toString())
		accountInfo.putGenderInSharedPref(IS_MALE_HEADER, _uiStateFlow.value.gender)
	}
}