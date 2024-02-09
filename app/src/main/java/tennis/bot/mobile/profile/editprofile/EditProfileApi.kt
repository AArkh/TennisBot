package tennis.bot.mobile.profile.editprofile

import kotlinx.serialization.Serializable
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.PUT

interface EditProfileApi {

	@PUT("api/tennis-players")
	fun putNameSurname(@Body nameSurname: NameSurnameNetwork): Call<Unit>

	@PUT("api/tennis-players")
	fun putBirthday(@Body birthdayNetwork: BirthdayNetwork): Call<Unit>

	@PUT("api/tennis-players")
	fun putLocation(@Body locationNetwork: LocationNetwork): Call<Unit>

	@PUT("api/tennis-players")
	fun putPhoneNumber(@Body phoneNumber: PhoneNumberNetwork): Call<Unit>

	@PUT("api/tennis-players")
	fun putTelegramId(@Body telegramId: TelegramIdNetwork): Call<Unit>
}

@Serializable
data class NameSurnameNetwork(
	val name: String,
	val surname: String
)

@Serializable
data class BirthdayNetwork (
	val birthday: String
)

@Serializable
data class LocationNetwork (
	val cityId: Int,
	val districtId: Int?
)

@Serializable
data class PhoneNumberNetwork (
	val phoneNumber: String
)

@Serializable
data class TelegramIdNetwork (
	val telegram: String
)

