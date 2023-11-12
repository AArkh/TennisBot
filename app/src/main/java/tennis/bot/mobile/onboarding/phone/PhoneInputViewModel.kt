package tennis.bot.mobile.onboarding.phone

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import tennis.bot.mobile.R
import tennis.bot.mobile.utils.showToast
import javax.inject.Inject

@HiltViewModel
class PhoneInputViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: PhoneInputRepository,
) : ViewModel() {

    private val _uiStateFlow = MutableStateFlow(PhoneInputUiState(
        iconRes = R.drawable.russia,
        prefix = "+7",
        userInput = "",
        errorMessage = null,
        proceedButtonEnabled = false,
        clearButtonVisible = false
    ))
    val uiStateFlow = _uiStateFlow.asStateFlow()

    private val errorText = context.getString(R.string.onboarding_text_incorrect_phone_number)

    fun onTextInput(text: CharSequence) {
        val prevState: PhoneInputUiState = _uiStateFlow.value
        val prefix = prevState.prefix
        val icon = prevState.iconRes
        val isClearButtonVisible = text.isNotEmpty()
        val errorMessage = if (text.length in 1..13) {
            errorText
        } else {
            null
        }
        _uiStateFlow.value = PhoneInputUiState(
            prefix = prefix,
            iconRes = icon,
            clearButtonVisible = isClearButtonVisible,
            errorMessage = errorMessage,
            userInput = text,
            proceedButtonEnabled = text.length == 14
        )
    }

    fun onCountryPicked(countryCode: String, countryIcon: Int) {
        val prevState: PhoneInputUiState = _uiStateFlow.value
        _uiStateFlow.value = prevState.copy(
            prefix = countryCode,
            iconRes = countryIcon,
        )
    }

    fun onNextClicked(successCallback: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                val value = uiStateFlow.value
                repository.requestSmsCode(value.prefix + value.userInput)
            }.onFailure {
                context.showToast("Failed to request sms code")
            }.onSuccess {
                successCallback.invoke()
            }
        }
    }
}