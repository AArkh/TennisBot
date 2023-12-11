package tennis.bot.mobile.onboarding.survey

import androidx.annotation.IntDef
import kotlinx.serialization.Serializable
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AccountInfoApi {

	@POST("api/core-account/register")
	fun postRegister(@Body register: Register): Call<RegisterResponseError>

	@POST("api/new-player")
	fun postNewPlayer(@Body newPlayer: NewPlayer): Call<NewPlayer>
}

@Serializable
data class Register(
	val phoneNumber: String?,
	val password: String?,
	val smsVerifyCode: String?
)

@Serializable
data class NewPlayer(
	val name: String,
	val surName: String,
	val phoneNumber: String,
	val birthday: String,
	val isMale: Boolean,
	val countryId: Int,
	val cityId: Int,
	val districtId: Int,
	val telegramId: Int?,
	val surveyAnswer: List<SurveyAnswer>
) {
	@Serializable
	data class SurveyAnswer(
		@SurveyOptions val experience: Int,
		@SurveyOptions val forehand: Int,
		@SurveyOptions val backhand: Int,
		@SurveyOptions val slice: Int,
		@SurveyOptions val serve: Int,
		@SurveyOptions val net: Int,
		@SurveyOptions val speed: Int,
		@SurveyOptions val tournaments: Int,
		@SurveyOptions val prizes: Int
	)
}

@Serializable
data class RegisterResponseError(
	val code: String,
	val message: String,
	val details: String,
	val data: List<Data>,
	val validationErrors: List<ValidationError>
) {
	@Serializable
	data class Data(
		val additionalProp1: String,
		val additionalProp2: String,
		val additionalProp3: String
	)

	@Serializable
	data class ValidationError(
		val message: String,
		val member: String
	)
}

@IntDef(1, 2, 3, 4)
@Retention(AnnotationRetention.SOURCE)
annotation class SurveyOptions