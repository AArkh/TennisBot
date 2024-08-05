package tennis.bot.mobile.onboarding.phone

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import tennis.bot.mobile.R
import tennis.bot.mobile.utils.AppCoroutineScopes
import tennis.bot.mobile.utils.getCountryCodeForPhoneNumber
import javax.inject.Inject

@HiltViewModel
open class PhoneInputViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: PhoneInputRepository
) : ViewModel() {

    private val _uiStateFlow = MutableStateFlow(
        PhoneInputUiState(
            countryCode = "",
            userInput = "",
            errorMessage = null,
            proceedButtonEnabled = false,
            clearButtonVisible = false
        )
    )
    val uiStateFlow = _uiStateFlow.asStateFlow()

    private val errorText = context.getString(R.string.onboarding_text_incorrect_phone_number)

//    fun onTextInput(text: CharSequence) {
//        val prevState: PhoneInputUiState = _uiStateFlow.value
//        val isClearButtonVisible = text.isNotEmpty()
//        val errorMessage = if ( text.isNotEmpty() && text.length < 14) {
//            errorText
//        } else {
//            null
//        }
//        _uiStateFlow.value = prevState.copy(
//            userInput = text.toString(),
//            clearButtonVisible = isClearButtonVisible,
//            errorMessage = errorMessage,
//            proceedButtonEnabled = text.length == 14 //todo переработать для разных стран
//        )
//    }

    fun onTextInput(text: CharSequence) {
        val prevState: PhoneInputUiState = _uiStateFlow.value
        val isClearButtonVisible = text.isNotEmpty()
        val countryCode = getCountryCodeForPhoneNumber(text.toString()) ?: ""
        val errorMessage = if ( text.isNotEmpty() && text.length < 15) {
            errorText
        } else {
            null
        }
        _uiStateFlow.value = prevState.copy(
            countryCode = countryCode.lowercase(), // temp val for country code
            userInput = text.toString(),
            clearButtonVisible = isClearButtonVisible,
            errorMessage = errorMessage,
            proceedButtonEnabled = text.length >= 15
        )
    }

//    private fun getFlagEmoji(countryCode: String): String { // kept it in case we decide to use the more offline flag format
//        return countryCode
//            .uppercase(Locale.ROOT)
//            .map { char ->
//                Character.toChars(char.code - 'A'.code + 0x1F1E6)
//            }
//            .joinToString("") { it.concatToString() }
//    }

    open fun onNextClicked(isUpdatePassword: Boolean = false, callback: (phoneNumber: String, isSuccess: Boolean) -> Unit) {
        AppCoroutineScopes.appWorkerScope.launch {
            val value = uiStateFlow.value
            val phoneNumber = value.userInput.toString()
            kotlin.runCatching {
                if(!isUpdatePassword) {
                    if (!repository.requestSmsCode(phoneNumber))
                        throw IllegalArgumentException("Failed to post Register")
                } else {
                    if (!repository.requestSmsCode(phoneNumber, true))
                        throw IllegalArgumentException("Failed to post Register")
                }
            }.onFailure {
                if (it !is CancellationException) {
                    Log.e("PhoneInputViewModel", "Failed to request sms code")
                }
                if (it is IllegalArgumentException) { // for now used to caught the registered number
                    callback.invoke(phoneNumber, false)
                }
            }.onSuccess {
                callback.invoke(phoneNumber, true)
            }
        }
    }
}