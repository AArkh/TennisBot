package tennis.bot.mobile.onboarding.survey

import android.content.Context
import androidx.annotation.WorkerThread
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountInfoRepository @Inject constructor(
	@ApplicationContext context: Context,
	private val api: AccountInfoApi
) { // for storing account info throughout the onboarding process

	val sharedPreferences = context.getSharedPreferences("AccountInfo", Context.MODE_PRIVATE)
	val editor = sharedPreferences.edit()


	val surveyData = mutableMapOf<String, Int>()
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
	fun postRegister() {
		val post = api.postRegister(
			Register(
				phoneNumber = sharedPreferences.getString(PHONE_NUMBER_HEADER, "79774477181"),
				password = sharedPreferences.getString(PASSWORD_HEADER, "pass1234"),
				smsVerifyCode = sharedPreferences.getString(SMS_VERIFY_CODE_HEADER, "1234")
			)
		).execute()
		post.code()
	}

	fun putStringInSharedPref(key: String, value: String) {
		editor.putString(key, value)
	}

	fun putGenderInSharedPref(key: String, value: Int) {
		val booleanValue: Boolean = when (value) {
			1 -> true
			2 -> false
			else -> return
		}
		editor.putBoolean(key, booleanValue)
	}

	fun putIntInSharedPref(key: String, value: Int) {
		editor.putInt(key, value)
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


