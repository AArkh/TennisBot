package tennis.bot.mobile.onboarding.login

import android.content.Context
import android.text.InputFilter
import android.text.Spanned
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
class LoginViewModel @Inject constructor(
	@ApplicationContext private val context: Context,
	private val accountInfo: OnboardingRepository
) : ViewModel() {

	private val _uiStateFlow = MutableStateFlow(
		LoginUiState(
			countryIconRes = R.drawable.russia,
			phonePrefix = "+7",
			userPhoneInput = "",
			userPasswordInput = "",
			phoneErrorMessage = null,
			passwordErrorMessage = null,
			loginButtonEnabled = false,
			isLoading = false,
			clearPhoneButtonVisible = false,
			clearPasswordButtonVisible = false,
		)
	)
	val uiStateFlow = _uiStateFlow.asStateFlow()

	private val phoneErrorText = context.getString(R.string.onboarding_text_incorrect_phone_number)
	private val passwordErrorText = context.getString(R.string.password_hint)
	private val loginAndPasswordError = context.getString(R.string.wrong_pass_or_login)
	private val passwordConditionsRegex = Regex("^(?=.*[A-Za-z])(?=.*\\d).+\$")

	companion object {
		const val PHONE_NUMBER_MAX_LENGTH = 14
		const val PASSWORD_MIN_LENGTH = 8
	}

	fun onPhoneInput(phoneNumber: CharSequence) {
		val prevState: LoginUiState = _uiStateFlow.value
		val isClearPhoneButtonVisible = phoneNumber.isNotEmpty()
		val phoneErrorMessage = if (phoneNumber.isNotEmpty() && phoneNumber.length < 14) {
			phoneErrorText
		} else {
			null
		}
		_uiStateFlow.value = prevState.copy(
			userPhoneInput = phoneNumber.toString(),
			phoneErrorMessage = phoneErrorMessage,
			clearPhoneButtonVisible = isClearPhoneButtonVisible,
		)
	}

	fun onCountryPicked(countryCode: String, countryIcon: Int) {
		val prevState: LoginUiState = _uiStateFlow.value
		_uiStateFlow.value = prevState.copy(
			countryIconRes = countryIcon,
			phonePrefix = countryCode,
		)
	}

	fun onPasswordInput(password: CharSequence) {
		val prevState: LoginUiState = _uiStateFlow.value
		val isClearPasswordButtonVisible = password.isNotEmpty()
		val passwordConditions: Boolean = passwordConditionsRegex.matches(password) && password.length >= PASSWORD_MIN_LENGTH
		val passwordErrorMessage = if (!passwordConditions && password.isNotEmpty()) {
			passwordErrorText
		} else {
			null
		}
		_uiStateFlow.value = prevState.copy(
			userPasswordInput = password.toString(),
			passwordErrorMessage = passwordErrorMessage,
			clearPasswordButtonVisible = isClearPasswordButtonVisible,
		)
	}

	fun isLoginButtonEnabled(){
		val currentState = _uiStateFlow.value
		val isPhoneNumberOk = currentState.userPhoneInput.length == PHONE_NUMBER_MAX_LENGTH
		val isPasswordOk = passwordConditionsRegex.matches(currentState.userPasswordInput) && currentState.userPasswordInput.length >= PASSWORD_MIN_LENGTH

		_uiStateFlow.value = currentState.copy(
			loginButtonEnabled = isPhoneNumberOk && isPasswordOk
		)
	}

	private fun showLoading() {
		val currentState = _uiStateFlow.value
		_uiStateFlow.value = currentState.copy(
			isLoading = true
		)
	}

	private fun onStopLoading() {
		val currentState = _uiStateFlow.value
		_uiStateFlow.value = currentState.copy(
			isLoading = false
		)
	}

	private fun onError(error: String) {
		val currentState = _uiStateFlow.value
		_uiStateFlow.value = currentState.copy(
			passwordErrorMessage = error,
		)
	}

	fun onLoginPressed(username: CharSequence, password: CharSequence, navigationCallback: () -> Unit) {
		showLoading()

		viewModelScope.launch(Dispatchers.IO) {
			when (accountInfo.postLogin(uiStateFlow.value.phonePrefix + username.toString(), password.toString())) {
				200 -> {
					navigationCallback.invoke()
				}

				400 -> onError(loginAndPasswordError)
				else -> {
					context.showToast(context.getString(R.string.error_text))
				}
			}
			onStopLoading()
		}
	}

	class NoSpaceInputFilter : InputFilter {
		override fun filter(
			source: CharSequence?,
			start: Int,
			end: Int,
			dest: Spanned?,
			dstart: Int,
			dend: Int
		): CharSequence? {
			if (source?.contains(" ") == true) {
				return ""
			}
			return null
		}
	}
}