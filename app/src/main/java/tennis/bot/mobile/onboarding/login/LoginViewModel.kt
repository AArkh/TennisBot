package tennis.bot.mobile.onboarding.login

import android.content.Context
import android.text.InputFilter
import android.text.Spanned
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import tennis.bot.mobile.R
import tennis.bot.mobile.onboarding.survey.AccountInfoRepository
import tennis.bot.mobile.utils.hideKeyboard
import tennis.bot.mobile.utils.showToast
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
	@ApplicationContext private val context: Context,
	private val accountInfo: AccountInfoRepository
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
	private val loginAndPasswordError = context.getString(R.string.wrong_pass_or_login)

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
		// todo regex в поле класса
		val passwordConditions: Boolean = Regex("^(?=.*[A-Za-z])(?=.*\\d).+\$").matches(password) && password.length >= 8 // todo волшебные символы в константы
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
		val isPhoneNumberOk = currentState.userPhoneInput.length == 14 // todo волшебные символы в константы
		val isPasswordOk = Regex("^(?=.*[A-Za-z])(?=.*\\d).+\$").matches(currentState.userPasswordInput) && currentState.userPasswordInput.length >= 8

		return isPhoneNumberOk && isPasswordOk
	}

	private fun showInitial() {
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

	private fun showLoading() {
		val currentState = _uiStateFlow.value
		_uiStateFlow.value = LoginUiState.Loading(
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

	private fun onError(error: String) {
		val currentState = _uiStateFlow.value
		_uiStateFlow.value = LoginUiState.Error(
			countryIconRes = currentState.countryIconRes,
			phonePrefix = currentState.phonePrefix,
			userPhoneInput = currentState.userPhoneInput,
			userPasswordInput = currentState.userPasswordInput,
			phoneErrorMessage = currentState.phoneErrorMessage,
			passwordErrorMessage = error,
			loginButtonEnabled = currentState.loginButtonEnabled,
			clearPhoneButtonVisible = currentState.clearPhoneButtonVisible,
			clearPasswordButtonVisible = currentState.clearPasswordButtonVisible
		)
	}

	fun onLoginPressed(username: CharSequence, password: CharSequence, navigationCallback: () -> Unit) {
		showLoading()

		viewModelScope.launch(Dispatchers.IO) {
			when (accountInfo.postLogin(uiStateFlow.value.phonePrefix + username.toString(), password.toString())) {
				 200 -> {
					 context.showToast("This would be a dialog cue")
					 navigationCallback.invoke()
					 showInitial()
				 }
				 400 -> onError(loginAndPasswordError)
				 else -> { context.showToast(context.getString(R.string.error_text)) } // todo у юзера будет бесконечный лоадинг
			 }
		}
	}
}