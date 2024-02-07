package tennis.bot.mobile.profile.editprofile.telegram

data class EditTelegramUiState(
	val telegramInput: String,
	val clearTelegramButtonVisible: Boolean,
	val changeButtonEnabled: Boolean
)
