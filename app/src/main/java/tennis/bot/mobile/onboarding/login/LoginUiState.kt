package tennis.bot.mobile.onboarding.login

sealed class LoginUiState(
	open val countryIconRes: Int,
	open val phonePrefix: String,
	open val userPhoneInput: String,
	open val userPasswordInput: String,
	open val phoneErrorMessage: String?,
	open val passwordErrorMessage: String?,
	open val loginButtonEnabled: Boolean,
	open val clearPhoneButtonVisible: Boolean,
	open val clearPasswordButtonVisible: Boolean
) {

	data class Initial(
		override val countryIconRes: Int,
		override val phonePrefix: String,
		override val userPhoneInput: String,
		override val userPasswordInput: String,
		override val phoneErrorMessage: String?,
		override val passwordErrorMessage: String?,
		override val loginButtonEnabled: Boolean,
		override val clearPhoneButtonVisible: Boolean,
		override val clearPasswordButtonVisible: Boolean
	): LoginUiState(countryIconRes, phonePrefix, userPhoneInput, userPasswordInput, phoneErrorMessage, passwordErrorMessage, loginButtonEnabled,
		clearPhoneButtonVisible, clearPasswordButtonVisible)

	data class Loading(
		override val countryIconRes: Int,
		override val phonePrefix: String,
		override val userPhoneInput: String,
		override val userPasswordInput: String,
		override val phoneErrorMessage: String?,
		override val passwordErrorMessage: String?,
		override val loginButtonEnabled: Boolean,
		override val clearPhoneButtonVisible: Boolean,
		override val clearPasswordButtonVisible: Boolean
	): LoginUiState(countryIconRes, phonePrefix, userPhoneInput, userPasswordInput, phoneErrorMessage, passwordErrorMessage, loginButtonEnabled,
		clearPhoneButtonVisible, clearPasswordButtonVisible)

	data class Error(
		override val countryIconRes: Int,
		override val phonePrefix: String,
		override val userPhoneInput: String,
		override val userPasswordInput: String,
		override val phoneErrorMessage: String?,
		override val passwordErrorMessage: String?,
		override val loginButtonEnabled: Boolean,
		override val clearPhoneButtonVisible: Boolean,
		override val clearPasswordButtonVisible: Boolean
	): LoginUiState(countryIconRes, phonePrefix, userPhoneInput, userPasswordInput, phoneErrorMessage, passwordErrorMessage, loginButtonEnabled,
		clearPhoneButtonVisible, clearPasswordButtonVisible)
}