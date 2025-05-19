package tennis.bot.mobile.onboarding.password

sealed class PasswordUiState(
	open val userInput: CharSequence,
	open val errorMessage: String?,
	open val confidentialityText: String,
	open val nextButtonEnabled: Boolean,
	open val clearButtonVisible: Boolean,
) {
	data class Initial(
		override val userInput: CharSequence,
		override val errorMessage: String?,
		override val confidentialityText: String,
		override val nextButtonEnabled: Boolean,
		val nextButtonText: String,
		override val clearButtonVisible: Boolean,
	) : PasswordUiState(userInput, errorMessage, confidentialityText, nextButtonEnabled, clearButtonVisible)

	data class Loading(
		override val userInput: CharSequence,
		override val errorMessage: String?,
		override val confidentialityText: String,
		override val nextButtonEnabled: Boolean,
		override val clearButtonVisible: Boolean,
	) : PasswordUiState(userInput, errorMessage, confidentialityText, nextButtonEnabled, clearButtonVisible)

	data class Error(
		override val userInput: CharSequence,
		override val errorMessage: String?,
		override val confidentialityText: String,
		override val nextButtonEnabled: Boolean,
		override val clearButtonVisible: Boolean,
	) : PasswordUiState(userInput, errorMessage, confidentialityText, nextButtonEnabled, clearButtonVisible)
}