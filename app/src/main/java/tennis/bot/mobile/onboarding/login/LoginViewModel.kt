package tennis.bot.mobile.onboarding.login

import android.content.Context
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import tennis.bot.mobile.R
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
	@ApplicationContext private val context: Context
): ViewModel() {

	private val _uiStateFlow = MutableStateFlow<LoginUiState>(
		LoginUiState.Initial(
			countryIconRes = R.drawable.russia,
			phonePrefix = "+7",
			userPhoneInput = "",
			userPasswordInput = "",
			phoneErrorMessage = null,
			passwordErrorMessage = null,
			loginButtonEnabled = false,
			clearPhoneButtonVisible = false,
			clearPasswordButtonVisible = false,
		)
	)
	val uiStateFlow = _uiStateFlow.asStateFlow()

	private val phoneErrorText = context.getString(R.string.onboarding_text_incorrect_phone_number)
	private val passwordErrorText = context.getString(R.string.password_hint)

	fun onPhoneInput(phoneNumber: CharSequence) {
		val prevState: LoginUiState = _uiStateFlow.value
		val isClearPhoneButtonVisible = phoneNumber.isNotEmpty()
		val phoneErrorMessage = if ( phoneNumber.isNotEmpty() && phoneNumber.length < 14) {
			phoneErrorText
		} else {
			null
		}
		_uiStateFlow.value = LoginUiState.Initial(
			countryIconRes = prevState.countryIconRes,
			phonePrefix = prevState.phonePrefix,
			userPhoneInput = phoneNumber.toString(),
			userPasswordInput = prevState.userPasswordInput,
			phoneErrorMessage = phoneErrorMessage,
			passwordErrorMessage = prevState.passwordErrorMessage,
			loginButtonEnabled = isLoginButtonEnabled(),
			clearPhoneButtonVisible = isClearPhoneButtonVisible,
			clearPasswordButtonVisible = prevState.clearPasswordButtonVisible,
		)
	}

	fun onCountryPicked(countryCode: String, countryIcon: Int) {
		val prevState: LoginUiState = _uiStateFlow.value
		_uiStateFlow.value = LoginUiState.Initial(
			countryIconRes = countryIcon,
			phonePrefix = countryCode,
			userPhoneInput = prevState.userPhoneInput,
			userPasswordInput = prevState.userPasswordInput,
			phoneErrorMessage = prevState.phoneErrorMessage,
			passwordErrorMessage = prevState.passwordErrorMessage,
			loginButtonEnabled = isLoginButtonEnabled(),
			clearPhoneButtonVisible = prevState.clearPhoneButtonVisible,
			clearPasswordButtonVisible = prevState.clearPasswordButtonVisible,
		)
	}

	fun onPasswordInput(password: CharSequence) {
		val prevState: LoginUiState = _uiStateFlow.value
		val isClearPasswordButtonVisible = password.isNotEmpty()
		val passwordConditions: Boolean = Regex("^(?=.*[A-Za-z])(?=.*\\d).+\$").matches(password) && password.length >= 8
		val passwordErrorMessage = if (!passwordConditions && password.isNotEmpty()) {
			passwordErrorText
		} else {
			null
		}
		_uiStateFlow.value = LoginUiState.Initial(
			countryIconRes = prevState.countryIconRes,
			phonePrefix = prevState.phonePrefix,
			userPhoneInput = prevState.userPhoneInput,
			userPasswordInput = password.toString(),
			phoneErrorMessage = prevState.phoneErrorMessage,
			passwordErrorMessage = passwordErrorMessage,
			loginButtonEnabled = isLoginButtonEnabled(),
			clearPhoneButtonVisible = prevState.clearPhoneButtonVisible,
			clearPasswordButtonVisible = isClearPasswordButtonVisible,
		)
	}

	private fun isLoginButtonEnabled(): Boolean {
		val currentState = _uiStateFlow.value
		val isPhoneNumberOk = currentState.userPhoneInput.length == 14 //todo переработать для разных стран
		val isPasswordOk = Regex("^(?=.*[A-Za-z])(?=.*\\d).+\$").matches(currentState.userPasswordInput) && currentState.userPasswordInput.length >= 8

		return isPhoneNumberOk && isPasswordOk
	}

	fun showLoading() {
		val currentState = _uiStateFlow.value
		_uiStateFlow.value = LoginUiState.Initial(
			countryIconRes = currentState.countryIconRes,
			phonePrefix = currentState.phonePrefix,
			userPhoneInput = currentState.userPhoneInput,
			userPasswordInput = currentState.userPasswordInput,
			phoneErrorMessage = currentState.phoneErrorMessage,
			passwordErrorMessage = currentState.passwordErrorMessage,
			loginButtonEnabled = currentState.loginButtonEnabled,
			clearPhoneButtonVisible = currentState.clearPhoneButtonVisible,
			clearPasswordButtonVisible = currentState.clearPasswordButtonVisible
		)
	}
}