package tennis.bot.mobile.onboarding.phone

import android.util.Log
import androidx.annotation.WorkerThread
import java.lang.StringBuilder
import javax.inject.Inject

class PhoneInputRepository @Inject constructor(
    private val smsApi: SmsApi
) {

    @WorkerThread
    suspend fun requestSmsCode(phone: String): Boolean {
        val result = kotlin.runCatching {
            smsApi.requestSmsCode(phone.toApiNumericFormat())
        }
        return result.getOrElse { return false }.isSuccessful
    }

    @WorkerThread
    suspend fun validateSmsCode(code: String, phone: String): Boolean {
        val response = kotlin.runCatching {
            smsApi.validateSmsCode(code.toApiNumericFormat(), phone.toApiNumericFormat())
        }.getOrElse { return false }
        return response.isSuccessful
    }

    private fun String.toApiNumericFormat(): String {
        val builder = StringBuilder()
        for (char in this) {
            if (char.isDigit()) {
                builder.append(char)
            }
        }
        return builder.toString()
    }
}