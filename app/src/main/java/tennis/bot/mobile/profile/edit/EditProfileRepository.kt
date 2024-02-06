package tennis.bot.mobile.profile.edit

import androidx.annotation.WorkerThread
import tennis.bot.mobile.onboarding.survey.toApiNumericFormat
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EditProfileRepository @Inject constructor(
	private val api: EditProfileApi
) {

	@WorkerThread
	suspend fun putNameSurname(name: String, surname: String): Boolean{
		val response = api.putNameSurname( NameSurnameNetwork(name, surname) ).execute()
		return response.isSuccessful
	}

	@WorkerThread
	suspend fun putBirthday(networkDateTime: String): Boolean {
		val response = api.putBirthday(BirthdayNetwork(networkDateTime)).execute()
		return response.isSuccessful
	}

	@WorkerThread
	suspend fun putLocation(cityId: Int): Boolean {
		val response = api.putLocation(CityNetwork(cityId)).execute()
		return response.isSuccessful
	}

	@WorkerThread
	suspend fun putPhoneNumber(phoneNumber: String): Boolean {
		val response = api.putPhoneNumber(PhoneNumberNetwork(phoneNumber.toApiNumericFormat())).execute()
		return response.isSuccessful
	}

	@WorkerThread
	suspend fun putTelegramIdNetwork(telegramId: String): Boolean {
		val response = api.putTelegramId(TelegramIdNetwork(telegramId)).execute()
		return response.isSuccessful
	}
}