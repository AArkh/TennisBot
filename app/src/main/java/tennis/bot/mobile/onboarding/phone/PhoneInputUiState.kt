package tennis.bot.mobile.onboarding.phone

import androidx.annotation.DrawableRes

/**
 * Usually this stuff is sealed class, but we do not have a lotta states here
 */
data class PhoneInputUiState(
    @DrawableRes val iconRes: Int,
    val prefix: String,
    val userInput: CharSequence,
    val errorMessage: String?,
    val proceedButtonEnabled: Boolean,
    val clearButtonVisible: Boolean,
)