package tennis.bot.mobile.onboarding.phone

sealed class SmsCodeUiState(
    open val title: String,
    open val input: String,
    open val retryButtonBlockedSec: Int,
) {

    data class Loading(
        override val title: String,
        override val input: String,
        override val retryButtonBlockedSec: Int,
    ) : SmsCodeUiState(title, input, retryButtonBlockedSec)

    data class Error(
        override val title: String,
        override val input: String,
        override val retryButtonBlockedSec: Int,
        val errorMessage: String,
    ) : SmsCodeUiState(title, input, retryButtonBlockedSec)

    data class UserInput(
        override val title: String,
        override val input: String,
        override val retryButtonBlockedSec: Int,
        val validateButtonBlocked: Boolean,
    ) : SmsCodeUiState(title, input, retryButtonBlockedSec)

    companion object {
        const val RETRY_BUTTON_UNBLOCKED = -1
        const val RETRY_BUTTON_BLOCKED_NO_COUNTDOWN = -2
    }

    fun updateRetryButton(retryButtonBlockedSec: Int): SmsCodeUiState {
        return when(this) {
            is Loading -> copy(retryButtonBlockedSec = retryButtonBlockedSec)
            is Error -> copy(retryButtonBlockedSec = retryButtonBlockedSec)
            is UserInput -> copy(retryButtonBlockedSec = retryButtonBlockedSec)
        }
    }
}