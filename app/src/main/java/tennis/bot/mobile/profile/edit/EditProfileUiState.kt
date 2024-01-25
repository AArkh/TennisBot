package tennis.bot.mobile.profile.edit

data class EditProfileUiState(
	val profilePicture: String,
	val nameSurname: String,
	val dateOfBirthday: String,
	val location: String,
	val phoneNumber: String,
	val telegramId: String
)
