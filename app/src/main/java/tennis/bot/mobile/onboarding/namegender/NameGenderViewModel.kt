package tennis.bot.mobile.onboarding.namegender

import android.content.Context
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import tennis.bot.mobile.onboarding.namegender.Const.FEMALE
import tennis.bot.mobile.onboarding.namegender.Const.MALE
import tennis.bot.mobile.onboarding.survey.OnboardingRepository
import javax.inject.Inject

@HiltViewModel
class NameGenderViewModel @Inject constructor(
	@ApplicationContext private val context: Context,
	private val accountInfo: OnboardingRepository): ViewModel(
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

	fun isNextButtonEnabled() {
		val prevState: NameGenderUiState = _uiStateFlow.value
		val isGenderPicked = prevState.gender == MALE || prevState.gender == FEMALE

		_uiStateFlow.value = prevState.copy(
			nextButtonEnabled = isGenderPicked
		)
	}

	fun onNextButtonClicked() {
		accountInfo.recordNameSurnameAndGender(
			name = _uiStateFlow.value.userNameInput.toString(),
			surname = _uiStateFlow.value.userNameInput.toString(),
			gender = _uiStateFlow.value.gender
		)
	}

}

object Const {
	const val MALE = 1
	const val FEMALE = 2
}
