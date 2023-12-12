package tennis.bot.mobile.onboarding.survey

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.annotation.WorkerThread
import dagger.hilt.android.qualifiers.ApplicationContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import tennis.bot.mobile.App.Companion.ctx
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountInfoRepository @Inject constructor(
	@ApplicationContext context: Context,
	private val api: AccountInfoApi
) { // for storing account info throughout the onboarding process

	val sharedPreferences = context.getSharedPreferences("AccountInfo", Context.MODE_PRIVATE)


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
		api.postRegister(
			Register(
				phoneNumber = sharedPreferences.getString(PHONE_NUMBER_HEADER, "79774477181"),
				password = sharedPreferences.getString(PASSWORD_HEADER, "pass1234"),
				smsVerifyCode = sharedPreferences.getString(SMS_VERIFY_CODE_HEADER, "1234")
				// https://gist.github.com/meowkameow/19d1750b1016437fa86f25968f3b9349
			)
		).enqueue(object: Callback<Register> {
			override fun onResponse(call: Call<Register>, response: Response<Register>) {
				Toast.makeText(ctx, "Data posted to API", Toast.LENGTH_SHORT).show()
				val body = response.body()
				val responseHere = "Response Code: ${response.code()} \n Response Body: $body"
				Log.d("1235467", responseHere)
			}

			override fun onFailure(call: Call<Register>, t: Throwable) {
				Log.d("1235467", "Error: ${t.message}")
			}

		})
	}

	fun recordPhoneNumberAndSmsCode(phoneNumber: String, smsVerifyCode: String) {
		sharedPreferences.edit().putString(PHONE_NUMBER_HEADER, phoneNumber).apply()
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

	fun recordPassword(password: String){
		sharedPreferences.edit().putString(PASSWORD_HEADER, password).apply()
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


