package tennis.bot.mobile.onboarding.phone

import android.content.Context
import androidx.annotation.WorkerThread
import dagger.hilt.android.qualifiers.ApplicationContext
import tennis.bot.mobile.R
import tennis.bot.mobile.onboarding.phone.SmsApi.Companion.UPDATE_USER_PASSWORD
import tennis.bot.mobile.utils.showToast
import java.lang.StringBuilder
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PhoneInputRepository @Inject constructor(
    private val smsApi: SmsApi,
    @ApplicationContext private val context: Context
) {

    @Throws(Exception::class)
    @WorkerThread
    suspend fun requestSmsCode(phone: String, isUpdatePassword: Boolean = false): Boolean {
        val result = kotlin.runCatching {
            if(!isUpdatePassword){
                smsApi.requestSmsCode(phone.toApiNumericFormat())
            } else {
                smsApi.requestSmsCode(phone.toApiNumericFormat(), operation = UPDATE_USER_PASSWORD)
            }

        }.getOrElse { return false }

        if(result.code() == 400){
            context.showToast(context.getString(R.string.phone_is_registered))
        }

        return result.isSuccessful
    }

    @Throws(Exception::class)
    @WorkerThread
    suspend fun validateSmsCode(code: String, phone: String, isUpdatePassword: Boolean = false): Boolean {
        val response = kotlin.runCatching {
            if(!isUpdatePassword){
                smsApi.validateSmsCode(code.toApiNumericFormat(), phone.toApiNumericFormat())
            } else {
                smsApi.validateSmsCode(code.toApiNumericFormat(), phone.toApiNumericFormat(), operation = UPDATE_USER_PASSWORD)
            }
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