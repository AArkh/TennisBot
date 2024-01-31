package tennis.bot.mobile.profile.edit.telegram

data class EditTelegramUiState(
	val telegramInput: String,
	val clearTelegramButtonVisible: Boolean,
	val changeButtonEnabled: Boolean
)
