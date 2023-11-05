package tennis.bot.mobile.onboarding.phone

import androidx.annotation.StringDef
import retrofit2.Response
import retrofit2.http.POST
import retrofit2.http.Query

interface SmsApi {

    @POST("api/sms/send-verify-sms")
    suspend fun requestSmsCode(
        @Query("phone") phone: String, // format of 381621250963 or 79994114141
        @Query("operation") @RequestSmsCodeOperation operation: String = REGISTER_USER,
    ): Response<Unit> // 204 mean request was successful.

    @POST("api/sms/validate-sms-code")
    suspend fun validateSmsCode(
        @Query("code") code: String, // 4-symbol numeric code, 1234, for example
        @Query("phone") phone: String, // format of 381621250963 or 79994114141
        @Query("operation") @RequestSmsCodeOperation operation: String = REGISTER_USER,
    ): Response<Unit>


    @StringDef(REGISTER_USER, UPDATE_USER_PASSWORD)
    @Retention(AnnotationRetention.SOURCE)
    annotation class RequestSmsCodeOperation

    companion object {
        const val REGISTER_USER = "RegisterUser"
        const val UPDATE_USER_PASSWORD = "UpdateUserPassword"
    }
}