package tennis.bot.mobile.onboarding.forgotpassword

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
import tennis.bot.mobile.R
import tennis.bot.mobile.onboarding.survey.OnboardingRepository
import tennis.bot.mobile.utils.showToast
import javax.inject.Inject

@HiltViewModel
class UpdatePasswordViewModel @Inject constructor(
	@ApplicationContext private val context: Context,
	private val repository: OnboardingRepository
): ViewModel() {

	private val _uiStateFlow = MutableStateFlow(
		UpdatePasswordUiState(
			passwordInput = "",
			confirmPasswordInput = "",
			passwordErrorMessage = null,
			confirmPasswordErrorMessage = null,
			passwordClearButtonVisible = false,
			confirmPasswordClearButtonVisible = false,
			isLoading = false,
			nextButtonEnabled = false,
		)
	)
	val uiStateFlow = _uiStateFlow.asStateFlow()

	fun onPasswordInput(password: CharSequence, isConfirmPassword: Boolean) {
		val isClearButtonVisible = password.isNotEmpty()
		val passwordConditions: Boolean = Regex("^(?=.*[A-Za-z])(?=.*\\d).+\$").matches(password) && password.length >= 8
		val errorMessage = if (!passwordConditions && password.isNotEmpty()) context.getString(R.string.password_hint) else null

		if (!isConfirmPassword)
			_uiStateFlow.value = uiStateFlow.value
				.copy(passwordInput = password, passwordClearButtonVisible = isClearButtonVisible, passwordErrorMessage = errorMessage)
		else
			_uiStateFlow.value = uiStateFlow.value
				.copy(confirmPasswordInput = password, confirmPasswordClearButtonVisible = isClearButtonVisible, confirmPasswordErrorMessage = errorMessage)
	}

	private fun checkPasswordsEquality(): Boolean {
		val password = uiStateFlow.value.passwordInput.toString()
		val confirmPassword = uiStateFlow.value.confirmPasswordInput.toString()

		if (confirmPassword.isNotEmpty() && password.isNotEmpty() && password != confirmPassword) {
			_uiStateFlow.value = uiStateFlow.value
				.copy(passwordErrorMessage = context.getString(R.string.password_match_hint), confirmPasswordErrorMessage = context.getString(R.string.password_match_hint))
			return false
		} else if (confirmPassword.isNotEmpty() && password.isNotEmpty() && password == confirmPassword) {
			_uiStateFlow.value = uiStateFlow.value
				.copy(passwordErrorMessage = null, confirmPasswordErrorMessage = null)
			return true
		}
		return false
	}

	fun isNextButtonEnabled(): Boolean {
		return uiStateFlow.value.passwordInput.isNotEmpty() && uiStateFlow.value.confirmPasswordInput.isNotEmpty()
				&& uiStateFlow.value.passwordErrorMessage == null && uiStateFlow.value.confirmPasswordErrorMessage == null
	}

	fun onNextButtonClicked(navigationCallback: () -> Unit) {
		viewModelScope.launch(Dispatchers.IO) {
			if (checkPasswordsEquality()) {
				kotlin.runCatching {
					showLoading()
					if (!repository.putUpdatePassword(uiStateFlow.value.passwordInput.toString()))
						throw IllegalArgumentException("Failed to post UpdatePassword")

				}.onFailure {
					onStopLoading()
					context.showToast(context.getString(R.string.error_no_network_message))
				}.onSuccess {
					onStopLoading()
					navigationCallback.invoke()
				}
			}
		}
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


}