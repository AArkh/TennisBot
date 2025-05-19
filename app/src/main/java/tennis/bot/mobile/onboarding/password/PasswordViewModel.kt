package tennis.bot.mobile.onboarding.password

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import tennis.bot.mobile.R
import tennis.bot.mobile.onboarding.survey.OnboardingRepository
import tennis.bot.mobile.utils.showToast
import javax.inject.Inject

@HiltViewModel
class PasswordViewModel @Inject constructor(
	@ApplicationContext private val context: Context,
	private val accountInfo: OnboardingRepository
) : ViewModel() {

	private val errorText = context.getString(R.string.password_hint)
	private val confidentialityText = context.getString(R.string.password_agree_with_confidentiality)
	private val nextButtonText = context.getString(R.string.password_create_account)

	private val _uiStateFlow = MutableStateFlow<PasswordUiState>(
		PasswordUiState.Initial(
			userInput = "",
			errorMessage = null,
			confidentialityText = confidentialityText,
			nextButtonEnabled = false,
			nextButtonText = nextButtonText,
			clearButtonVisible = false
		)
	)
	val uiStateFlow = _uiStateFlow.asStateFlow()

	fun onPasswordInput(password: CharSequence) {
		val isClearButtonVisible = password.isNotEmpty()
		val passwordConditions: Boolean = Regex("^(?=.*[A-Za-z])(?=.*\\d).+\$").matches(password) && password.length >= 8
		val errorMessage = if (!passwordConditions && password.isNotEmpty()) {
			errorText
		} else {
			null
		}
		_uiStateFlow.value = PasswordUiState.Initial(
			userInput = password.toString(),
			clearButtonVisible = isClearButtonVisible,
			errorMessage = errorMessage,
			confidentialityText = confidentialityText,
			nextButtonText = nextButtonText,
			nextButtonEnabled = passwordConditions
		)
	}

	fun showLoading() {
		val currentState = _uiStateFlow.value
		_uiStateFlow.value = PasswordUiState.Loading(
			userInput = currentState.userInput,
			clearButtonVisible = currentState.clearButtonVisible,
			errorMessage = currentState.errorMessage,
			confidentialityText = confidentialityText,
			nextButtonEnabled = currentState.nextButtonEnabled
		)
	}

	fun onError() {
		context.showToast("${context.getString(R.string.error_text)}. Попробуйте еще раз")
		val prevState =  _uiStateFlow.value
		_uiStateFlow.value = PasswordUiState.Initial(
			userInput = prevState.userInput,
			clearButtonVisible = prevState.clearButtonVisible,
			errorMessage = prevState.errorMessage,
			confidentialityText = confidentialityText,
			nextButtonEnabled = prevState.nextButtonEnabled,
			nextButtonText = nextButtonText
		)
	}
	fun onNextButtonClicked(navigationCallback: () -> Unit) {
		accountInfo.recordPassword(_uiStateFlow.value.userInput.toString())
		showLoading()
		viewModelScope.launch(Dispatchers.IO) {
			kotlin.runCatching {
				if (!accountInfo.postRegister())
					throw IllegalArgumentException("Failed to post Register")
			}.onFailure {
				onError()
			}.onSuccess {
				navigationCallback.invoke()
			}
		}
	}
}