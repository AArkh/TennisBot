package tennis.bot.mobile.onboarding.forgotpassword

data class UpdatePasswordUiState(
	val passwordInput: CharSequence,
	val confirmPasswordInput: CharSequence,
	val passwordErrorMessage: String?,
	val confirmPasswordErrorMessage: String?,
	val passwordClearButtonVisible: Boolean,
	val confirmPasswordClearButtonVisible: Boolean,
	val isLoading: Boolean,
	val nextButtonEnabled: Boolean,
)
