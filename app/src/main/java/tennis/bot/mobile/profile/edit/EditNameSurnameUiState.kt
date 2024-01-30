package tennis.bot.mobile.profile.edit

data class EditNameSurnameUiState(
	val userNameInput: String,
	val userSurnameInput: String,
	val clearNameButtonVisible: Boolean,
	val clearSurnameButtonVisible: Boolean,
	val changeButtonEnabled: Boolean
)