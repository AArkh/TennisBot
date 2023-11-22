package tennis.bot.mobile.onboarding.phone

import android.content.Context
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import tennis.bot.mobile.R
import javax.inject.Inject

@HiltViewModel
class PhoneInputViewModel @Inject constructor(
    @ApplicationContext context: Context
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
        val isClearButtonVisible = text.isNotEmpty()
        val errorMessage = if ( text.isNotEmpty() && text.length < 14) {
            errorText
        } else {
            null
        }
        _uiStateFlow.value = prevState.copy(
            userInput = text.toString(),
            clearButtonVisible = isClearButtonVisible,
            errorMessage = errorMessage,
            proceedButtonEnabled = text.length == 14 //todo переработать для разных стран
        )
    }

    fun onCountryPicked(countryCode: String, countryIcon: Int) {
        val prevState: PhoneInputUiState = _uiStateFlow.value
        _uiStateFlow.value = prevState.copy(
            prefix = countryCode,
            iconRes = countryIcon,
        )
    }
}