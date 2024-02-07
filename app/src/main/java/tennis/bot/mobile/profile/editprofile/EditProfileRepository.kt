package tennis.bot.mobile.profile.editprofile

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
		val response = kotlin.runCatching { api.putNameSurname( NameSurnameNetwork(name, surname) ).execute() }.getOrElse { return false }
		return response.isSuccessful
	}

	@WorkerThread
	suspend fun putBirthday(networkDateTime: String): Boolean {
		val response = kotlin.runCatching { api.putBirthday(BirthdayNetwork(networkDateTime)).execute()  }.getOrElse { return false }
		return response.isSuccessful
	}

	@WorkerThread
	suspend fun putLocation(cityId: Int, districtId: Int?): Boolean {
		val response = kotlin.runCatching { api.putLocation(LocationNetwork(cityId, districtId)).execute()  }.getOrElse { return false }
		return response.isSuccessful
	}

	@WorkerThread
	suspend fun putPhoneNumber(phoneNumber: String): Boolean {
		val response =  kotlin.runCatching { api.putPhoneNumber(PhoneNumberNetwork(phoneNumber.toApiNumericFormat())).execute() }.getOrElse { return false }
		return response.isSuccessful
	}

	@WorkerThread
	suspend fun putTelegramIdNetwork(telegramId: String): Boolean {
		val response =  kotlin.runCatching { api.putTelegramId(TelegramIdNetwork(telegramId)).execute() }.getOrElse { return false }
		return response.isSuccessful
	}
}