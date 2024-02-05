package tennis.bot.mobile.profile.edit

import kotlinx.serialization.Serializable
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.PUT

interface EditProfileApi {

	@PUT("api/tennis-players")
	fun putNameSurname(@Body nameSurname: NameSurname): Call<Unit>

	@PUT("api/tennis-players")
	fun putBirthday(@Body dateTime: String?): Call<Unit>

	@PUT("api/tennis-players")
	fun putLocation(@Body location: LocationNetwork): Call<Unit>

	@PUT("api/tennis-players")
	fun putPhoneNumber(@Body phoneNumber: String): Call<Unit>

	@PUT("api/tennis-players")
	fun putTelegramId(@Body telegramId: String): Call<Unit>
}

@Serializable
data class NameSurname(
	val name: String,
	val surname: String
)

data class LocationNetwork(
	val country: String?,
	val city: String?,
	val district: String?
)