package tennis.bot.mobile.onboarding.namegender

data class NameGenderUiState(
	val userNameInput: CharSequence,
	val userSurnameInput: CharSequence,
	val gender: Int,
	val clearNameButtonVisible: Boolean,
	val clearSurnameButtonVisible: Boolean,
	val nextButtonEnabled: Boolean,

	)