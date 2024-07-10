package tennis.bot.mobile.onboarding.survey

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.annotation.WorkerThread
import androidx.core.net.toUri
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import tennis.bot.mobile.core.authentication.AuthTokenRepository
import tennis.bot.mobile.onboarding.photopick.PhotoPickApi
import tennis.bot.mobile.utils.uriToFile
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class OnboardingRepository @Inject constructor(
	@ApplicationContext private val context: Context,
	private val registerAndLoginApi: RegisterAndLoginApi,
	private val newPlayerApi: NewPlayerApi,
	private val photoPickApi: PhotoPickApi,
	private val tokenRepo: AuthTokenRepository,
) {

    private val sharedPreferences = context.getSharedPreferences("AccountInfo", Context.MODE_PRIVATE)
    private val surveyData = mutableMapOf<String, Int>()
    private var registerResponse: RegisterResponse = RegisterResponse("", "", false, "")
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
            registerAndLoginApi.postRegister(
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
        val response = registerAndLoginApi.loginUser(
            grantType = GRANT_TYPE,
            clientId = CLIENT_ID,
            username = getPhoneNumber().toString(),
            password = getPassword().toString(),
            clientSecret = CLIENT_SECRET,
            scope = SCOPE,
            audience = AUDIENCE
        )

        if (response.code() == 200) {
            tokenRepo.recordToken(
                TokenResponse(
                    accessToken = response.body()!!.accessToken,
                    tokenType = response.body()!!.tokenType,
                    expiresIn = response.body()!!.expiresIn,
                    refreshToken = response.body()!!.refreshToken
                )
            )
            Log.d("1234567", "token has been recorded")
        }
    }

    @WorkerThread
    suspend fun postLogin(username: String, password: String): Int {
        val response = registerAndLoginApi.loginUser(
            grantType = GRANT_TYPE,
            clientId = CLIENT_ID,
            username = username.toApiNumericFormat(),
            password = password,
            clientSecret = CLIENT_SECRET,
            scope = SCOPE,
            audience = AUDIENCE
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
                    photo = getUserPicture(),
					isMale = isMale(),
					countryId = getCountryId(),
					cityId = if (getCityId() == 0) null else getCityId(),
					districtId = if (getDistrictId() == 0) null else getDistrictId(),
					telegram = getTelegram().toString(),
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
		}.onSuccess {
            postLogin()
        }.getOrElse { return false }
		return response.isSuccessful
	}

    @WorkerThread
    suspend fun postRegistrationProfilePicture(pickedPhotoUri: Uri): Int {
        val file = uriToFile(context, pickedPhotoUri)
        val requestFile = file?.asRequestBody("multipart/form-data".toMediaTypeOrNull())
        val imagePart = MultipartBody.Part.createFormData("Content", file?.name, requestFile!!)

        val response = photoPickApi.postRegistrationProfilePicture(
            Content = imagePart,
            name = ""
        )

        if (response.code() == 200) {
            response.body()?.let { recordUserPicture(it) }
            Log.d("123456", "profilePic has been received and recorded")
        } else if (response.code() == 400) {
            Log.d("123456", "profilePic has crashed and burned")
        }

        return response.code()
    }

    @WorkerThread
    suspend fun postProfilePicture(profilePhotoUri: String, isRegister: Boolean): Int {
        val file = uriToFile(context, profilePhotoUri.toUri())
        val requestFile = file?.asRequestBody("multipart/form-data".toMediaTypeOrNull())
        val imagePart = MultipartBody.Part.createFormData("Content", file?.name, requestFile!!)

        val response = photoPickApi.postProfilePicture(
            registerPhoto = isRegister,
            Content = imagePart,
            name = "",
            authHeader = tokenRepo.getAccessToken() ?: ""
        )

        if (response.code() == 204) {
            Log.d("123456", "profilePic was posted")
        } else if (response.code() == 400) {
            Log.d("123456", "profilePic has crashed and burned")
        }

        return response.code()
    }

    @WorkerThread
    suspend fun putUpdatePassword(password: String): Boolean {
        val response = kotlin.runCatching {
            registerAndLoginApi.updatePassword(Register(
                phoneNumber = getPhoneNumber(),
                password = password,
                smsVerifyCode = getSmsVerifyCode()
            ))
        }.getOrElse { return false }

        return response.isSuccessful
    }

	fun recordPhoneNumberAndSmsCode(phoneNumber: String, smsVerifyCode: String?) {
		sharedPreferences.edit().putString(PHONE_NUMBER_HEADER, phoneNumber.toApiNumericFormat()).apply()
        if (smsVerifyCode != null) {
            sharedPreferences.edit().putString(SMS_VERIFY_CODE_HEADER, smsVerifyCode).apply()
        }
	}

    fun recordUserPicture(userPicture: String) {
        sharedPreferences.edit().putString(USER_PROFILE_PICTURE, userPicture).apply()
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

    fun recordLocationData(countryId: Int, cityId: Int, districtId: Int?) {
        sharedPreferences.edit().putInt(COUNTRY_ID_HEADER, countryId).apply()
        sharedPreferences.edit().putInt(CITY_ID_HEADER, cityId).apply()
        if (districtId != null) {
            sharedPreferences.edit().putInt(DISTRICT_ID_HEADER, districtId).apply()
        }
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

    fun getTelegram(): String? {
        return sharedPreferences.getString(TELEGRAM_HEADER, null)
    }

    fun getUserPicture(): String? {
        return sharedPreferences.getString(USER_PROFILE_PICTURE, null)
    }

    fun getPassword(): String? {
        return sharedPreferences.getString(PASSWORD_HEADER, null)
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
        const val TELEGRAM_HEADER = "telegram"
        const val USER_PROFILE_PICTURE = "userProfilePicture"

        private const val CLIENT_SECRET = "Aitq2BWbQDR8pOiOwFS5SXGlss93xLlY"
        private const val GRANT_TYPE = "password"
        private const val CLIENT_ID = "Core_Android"
        private const val SCOPE = "roles offline_access"
        private const val AUDIENCE = "Core"
    }
}

fun String.toApiNumericFormat(): String {
    val builder = StringBuilder()
    for (char in this) {
        if (char.isDigit()) {
            builder.append(char)
        }
    }
    return builder.toString()
}


