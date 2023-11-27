package tennis.bot.mobile.onboarding.password

import androidx.annotation.DrawableRes

data class PasswordUiState(
	val userInput: CharSequence,
	val errorMessage: String?,
	val confidentialityText: String,
	val nextButtonEnabled: Boolean,
	val clearButtonVisible: Boolean,
)