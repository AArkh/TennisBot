package tennis.bot.mobile.onboarding.login

data class LoginUiState(
	val countryIconRes: Int,
	val phonePrefix: String,
	val userPhoneInput: String,
	val userPasswordInput: String,
	val phoneErrorMessage: String?,
	val passwordErrorMessage: String?,
	val loginButtonEnabled: Boolean,
	val isLoading: Boolean,
	val clearPhoneButtonVisible: Boolean,
	val clearPasswordButtonVisible: Boolean
)