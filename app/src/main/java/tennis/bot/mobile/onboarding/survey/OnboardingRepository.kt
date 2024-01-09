package tennis.bot.mobile.onboarding.survey

import android.content.Context
import android.util.Log
import androidx.annotation.WorkerThread
import dagger.hilt.android.qualifiers.ApplicationContext
import tennis.bot.mobile.core.AuthTokenRepository
import java.lang.StringBuilder
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OnboardingRepository @Inject constructor(
    @ApplicationContext context: Context,
    private val api: RegisterAndLoginApi,
    private val newPlayerApi: NewPlayerApi,
    private val tokenRepo: AuthTokenRepository,
) {

    private val sharedPreferences = context.getSharedPreferences("AccountInfo", Context.MODE_PRIVATE)
    private val surveyData = mutableMapOf<String, Int>()
    var registerResponse: RegisterResponse = RegisterResponse("", "", false, "")
    val rawSurveyAnswers = mutableListOf<Int>()
    val surveyAnswers = mutableListOf<String>()

    fun updateSurveyData() {
        val listOfHeaders = listOf(
            "experience",
            "forehand",
            "backhand",
            "slice",
            "serve",
            "net",
            "speed",
            "tournaments",
            "prizes"
        )

        val pairs = listOfHeaders.zip(rawSurveyAnswers)
        for ((header, answer) in pairs) {
            surveyData[header] = answer
        }
    }

    @WorkerThread
    suspend fun postRegister(): Boolean {
        val response = kotlin.runCatching {
            api.postRegister(
                Register(
                    phoneNumber = getPhoneNumber(),
                    password = getPassword(),
                    smsVerifyCode = getSmsVerifyCode()
                    // https://gist.github.com/meowkameow/19d1750b1016437fa86f25968f3b9349
                )
            )
        }.getOrElse { return false }

        if (response.isSuccessful) {
            registerResponse = response.body()!!
        }

        return response.isSuccessful
    }

    @WorkerThread
    suspend fun postLogin() {
        val response = api.loginUser(
            grantType = "password",
            clientId = "Core_Android",
            username = getPhoneNumber().toString(),
            password = getPassword().toString(),
            clientSecret = "Aitq2BWbQDR8pOiOwFS5SXGlss93xLlY",
            scope = "roles offline_access",
            audience = "Core"
        )

        if (response.code() == 200) {
            Log.d("1234567", "token has been recorded")
            tokenRepo.recordToken(
                TokenResponse(
                    accessToken = response.body()!!.accessToken,
                    tokenType = response.body()!!.tokenType,
                    expiresIn = response.body()!!.expiresIn,
                    refreshToken = response.body()!!.refreshToken
                )
            )
        }
    }

    @WorkerThread
    suspend fun postLogin(username: String, password: String): Int {
        val response = api.loginUser(
            grantType = "password",
            clientId = "Core_Android",
            username = username.toApiNumericFormat(),
            password = password,
            clientSecret = "Aitq2BWbQDR8pOiOwFS5SXGlss93xLlY",
            scope = "roles offline_access",
            audience = "Core"
        )

        if (response.code() == 200) {
            Log.d("1234567", "token has been recorded")
            tokenRepo.recordToken(
                TokenResponse(
                    accessToken = response.body()!!.accessToken,
                    tokenType = response.body()!!.tokenType,
                    expiresIn = response.body()!!.expiresIn,
                    refreshToken = response.body()!!.refreshToken
                )
            )
        }

        return response.code()
    }

	@WorkerThread
	suspend fun postNewPlayer(): Boolean {
		val response = kotlin.runCatching {
            newPlayerApi.postNewPlayer(
				NewPlayer(
					name = getName().toString(),
					surName = getSurName().toString(),
					phoneNumber = getPhoneNumber().toString(),
					birthday = registerResponse.creationTime,
					isMale = isMale(),
					countryId = getCountryId(),
					cityId = if (getCityId() == 0) null else getCityId(),
					districtId = if (getDistrictId() == 0) null else getDistrictId(),
					telegramId = getTelegramId().toString(),
					surveyAnswer = NewPlayer.SurveyAnswers(
						experience = surveyData["experience"] ?: 0,
						forehand = surveyData["forehand"] ?: 0,
						backhand = surveyData["backhand"] ?: 0,
						slice = surveyData["slice"] ?: 0,
						serve = surveyData["serve"] ?: 0,
						net = surveyData["net"] ?: 0,
						speed = surveyData["speed"] ?: 0,
						tournaments = surveyData["tournaments"] ?: 0,
						prizes = surveyData["prizes"] ?: 0
					)
				), tokenRepo.getAccessToken() ?: ""
			)
		}.getOrElse { return false }
		return response.isSuccessful
	}

	fun recordPhoneNumberAndSmsCode(phoneNumber: String, smsVerifyCode: String) {
		sharedPreferences.edit().putString(PHONE_NUMBER_HEADER, phoneNumber.toApiNumericFormat()).apply()
		sharedPreferences.edit().putString(SMS_VERIFY_CODE_HEADER, smsVerifyCode).apply()
	}

    fun recordNameSurnameAndGender(name: String, surname: String, gender: Int) {
        val booleanValue: Boolean = when (gender) {
            1 -> true
            2 -> false
            else -> return
        }
        sharedPreferences.edit().putString(NAME_HEADER, name).apply()
        sharedPreferences.edit().putString(SURNAME_HEADER, surname).apply()
        sharedPreferences.edit().putBoolean(IS_MALE_HEADER, booleanValue).apply()
    }

    fun recordLocationData(countryId: Int, cityId: Int, districtId: Int) {
        sharedPreferences.edit().putInt(COUNTRY_ID_HEADER, countryId).apply()
        sharedPreferences.edit().putInt(CITY_ID_HEADER, cityId).apply()
        sharedPreferences.edit().putInt(DISTRICT_ID_HEADER, districtId).apply()
    }

    fun recordPassword(password: String) {
        sharedPreferences.edit().putString(PASSWORD_HEADER, password).apply()
    }

    fun getPhoneNumber(): String? {
        return sharedPreferences.getString(PHONE_NUMBER_HEADER, null)
    }

    fun getSmsVerifyCode(): String? {
        return sharedPreferences.getString(SMS_VERIFY_CODE_HEADER, null)
    }

    fun getName(): String? {
        return sharedPreferences.getString(NAME_HEADER, null)
    }

    fun getSurName(): String? {
        return sharedPreferences.getString(SURNAME_HEADER, null)
    }

    fun isMale(): Boolean {
        return sharedPreferences.getBoolean(IS_MALE_HEADER, false)
    }

    fun getCountryId(): Int {
        return sharedPreferences.getInt(COUNTRY_ID_HEADER, 0)
    }

    fun getCityId(): Int {
        return sharedPreferences.getInt(CITY_ID_HEADER, 0)
    }

    fun getDistrictId(): Int {
        return sharedPreferences.getInt(DISTRICT_ID_HEADER, 0)
    }

    fun getTelegramId(): String? {
        return sharedPreferences.getString(TELEGRAM_ID_HEADER, null)
    }

    fun getPassword(): String? {
        return sharedPreferences.getString(PASSWORD_HEADER, null)
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

    companion object {
        const val PHONE_NUMBER_HEADER = "phoneNumber"
        const val PASSWORD_HEADER = "password"
        const val SMS_VERIFY_CODE_HEADER = "smsVerifyCode"
        const val NAME_HEADER = "name"
        const val SURNAME_HEADER = "surName"
        const val IS_MALE_HEADER = "isMale"
        const val COUNTRY_ID_HEADER = "countryId"
        const val CITY_ID_HEADER = "cityId"
        const val DISTRICT_ID_HEADER = "districtId"
        const val TELEGRAM_ID_HEADER = "telegramId"
    }
}


