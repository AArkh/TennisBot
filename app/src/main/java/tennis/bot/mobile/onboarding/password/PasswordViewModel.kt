package tennis.bot.mobile.onboarding.password

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tennis.bot.mobile.R
import tennis.bot.mobile.onboarding.survey.AccountInfoRepository
import tennis.bot.mobile.onboarding.survey.AccountInfoRepository.Companion.PASSWORD_HEADER
import javax.inject.Inject

@HiltViewModel
class PasswordViewModel @Inject constructor(
	@ApplicationContext private val context: Context,
	private val accountInfo: AccountInfoRepository
): ViewModel() {

	private val errorText = context.getString(R.string.password_hint)
	private val confidentialityText = context.getString(R.string.password_agree_with_confidentiality)

	private val _uiStateFlow = MutableStateFlow(
		PasswordUiState(
			userInput = "",
			errorMessage = null,
			confidentialityText = confidentialityText,
			nextButtonEnabled = false,
			clearButtonVisible = false
		)
	)

	val uiStateFlow = _uiStateFlow.asStateFlow()

	fun onPasswordInput(password: CharSequence) {
		val prevState: PasswordUiState = _uiStateFlow.value
		val isClearButtonVisible = password.isNotEmpty()
		val passwordConditions: Boolean = Regex("^(?=.*[A-Za-z])(?=.*\\d).+\$").matches(password) && password.length >= 8
		val errorMessage = if (!passwordConditions && password.isNotEmpty()) {
			errorText
		} else {
			null
		}
		_uiStateFlow.value = prevState.copy(
			userInput = password.toString(),
			clearButtonVisible = isClearButtonVisible,
			errorMessage = errorMessage,
			nextButtonEnabled = passwordConditions
		)
	}

	fun recordPassword() {
		accountInfo.putStringInSharedPref(PASSWORD_HEADER, _uiStateFlow.value.userInput.toString())
	}

	fun onPostRegister() {

		accountInfo.postRegister()
	}
}