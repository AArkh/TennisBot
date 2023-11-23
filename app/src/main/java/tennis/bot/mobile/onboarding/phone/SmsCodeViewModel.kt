package tennis.bot.mobile.onboarding.phone

import android.content.Context
import android.os.CountDownTimer
import android.util.Log
import androidx.annotation.WorkerThread
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import tennis.bot.mobile.R
import tennis.bot.mobile.onboarding.phone.SmsCodeFragment.Companion.PHONE_NUMBER_ARGUMENT
import tennis.bot.mobile.onboarding.phone.SmsCodeUiState.Companion.RETRY_BUTTON_BLOCKED_NO_COUNTDOWN
import tennis.bot.mobile.onboarding.phone.SmsCodeUiState.Companion.RETRY_BUTTON_UNBLOCKED
import tennis.bot.mobile.utils.showToast
import javax.inject.Inject

@HiltViewModel
class SmsCodeViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: PhoneInputRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val phone = savedStateHandle.get<String>(PHONE_NUMBER_ARGUMENT) ?: ""
    private val _uiStateFlow = MutableStateFlow<SmsCodeUiState>(SmsCodeUiState.UserInput(
        title = context.getString(R.string.onboarding_sms_input_title, phone),
        input = "",
        retryButtonBlockedSec = COUNTDOWN_SEC,
        validateButtonBlocked = true
    ))
    val uiStateFlow = _uiStateFlow.asStateFlow()

    init {
        startTimer()
    }

    private fun startTimer() {
        object : CountDownTimer(COUNTDOWN_MS, COUNTDOWN_TICK) {
            override fun onTick(millisUntilFinished: Long) {
                val currentState = _uiStateFlow.value
                if (currentState.retryButtonBlockedSec == RETRY_BUTTON_BLOCKED_NO_COUNTDOWN) {
                    this.cancel()
                    return
                }
                val timeRemainingSec = (millisUntilFinished / 1000).toInt()
                _uiStateFlow.value = currentState.updateRetryButton(timeRemainingSec)
            }

            override fun onFinish() {
                val currentState = _uiStateFlow.value
                _uiStateFlow.value = currentState.updateRetryButton(RETRY_BUTTON_UNBLOCKED)
            }
        }.start()
    }

    fun onUserInput(validatedInput: String) {
        val currentState = _uiStateFlow.value
        val newState = SmsCodeUiState.UserInput(
            title = currentState.title,
            input = validatedInput,
            retryButtonBlockedSec = currentState.retryButtonBlockedSec,
            validateButtonBlocked = validatedInput.length != SMS_CODE_LENGTH
        )
        _uiStateFlow.value = newState
    }

    fun onNextButtonClicked(navigationCallback: () -> Unit) {
        showLoading()
        viewModelScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                if (!repository.validateSmsCode(_uiStateFlow.value.input, phone))
                    throw IllegalArgumentException("Failed to request sms code for phone $phone")
            }.onFailure {
                if (it is IllegalArgumentException) {
                    // Неверный код, возвращаем ошибку
                    context.showToast(context.getString(R.string.onboarding_sms_input_failed_to_validate_sms))
                    _uiStateFlow.value = SmsCodeUiState.Error(
                        title = _uiStateFlow.value.title,
                        input = _uiStateFlow.value.input,
                        retryButtonBlockedSec = RETRY_BUTTON_BLOCKED_NO_COUNTDOWN,
                        errorMessage = context.getString(R.string.onboarding_sms_input_failed_to_validate_sms)
                    )
                } else {
                    // Сетевая ошибка, даем возможность переотправить код
                    context.showToast(context.getString(R.string.error_no_network_message))
                    _uiStateFlow.value = SmsCodeUiState.UserInput(
                        title = _uiStateFlow.value.title,
                        input = _uiStateFlow.value.input,
                        retryButtonBlockedSec = _uiStateFlow.value.retryButtonBlockedSec,
                        validateButtonBlocked = _uiStateFlow.value.input.length != SMS_CODE_LENGTH,
                    )
                }
            }.onSuccess {
                navigationCallback.invoke()
            }
        }
    }

    fun onResendSmsButtonClicked() {
        showLoading()
        viewModelScope.launch(Dispatchers.IO) {
            retrySmsCodeSending()
        }
    }

    @WorkerThread
    private suspend fun retrySmsCodeSending() {
        runCatching {
            if (!repository.requestSmsCode(phone))
                throw IllegalStateException("Failed to request sms code for phone $phone")
        }.onFailure {
            Log.e("1234567", "Failed to request sms code for phone $phone", it)
            _uiStateFlow.value = SmsCodeUiState.Error(
                title = _uiStateFlow.value.title,
                input = _uiStateFlow.value.input,
                retryButtonBlockedSec = RETRY_BUTTON_UNBLOCKED,
                errorMessage = context.getString(R.string.onboarding_sms_input_failed_to_request_sms, phone)
            )
        }.onSuccess {
            _uiStateFlow.value = SmsCodeUiState.UserInput(
                title = _uiStateFlow.value.title,
                input = _uiStateFlow.value.input,
                retryButtonBlockedSec = COUNTDOWN_SEC,
                validateButtonBlocked = _uiStateFlow.value.input.length != SMS_CODE_LENGTH
            )
            viewModelScope.launch {
                startTimer()
            }
        }
    }

    private fun showLoading() {
        val currentState = _uiStateFlow.value
        _uiStateFlow.value = SmsCodeUiState.Loading(
            title = currentState.title,
            input = currentState.input,
            retryButtonBlockedSec = RETRY_BUTTON_BLOCKED_NO_COUNTDOWN,
        )
    }

    companion object {
        const val SMS_CODE_LENGTH = 4
        private const val COUNTDOWN_SEC = 30
        private const val COUNTDOWN_MS = 30000L
        private const val COUNTDOWN_TICK = 1000L
    }
}