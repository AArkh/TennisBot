package tennis.bot.mobile.onboarding.phone

/**
 * Usually this stuff is sealed class, but we do not have a lotta states here
 */
data class PhoneInputUiState(
    val countryCode: String,
    val userInput: CharSequence,
    val errorMessage: String?,
    val proceedButtonEnabled: Boolean,
    val clearButtonVisible: Boolean,
)