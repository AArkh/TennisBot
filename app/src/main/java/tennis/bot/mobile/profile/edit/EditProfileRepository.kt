package tennis.bot.mobile.profile.edit

import androidx.annotation.WorkerThread
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EditProfileRepository @Inject constructor(
	private val api: EditProfileApi
) {

	@WorkerThread
	suspend fun putNameSurname(name: String, surname: String): Boolean{
		val response = api.putNameSurname( NameSurname(name, surname) ).execute()
		return response.isSuccessful
	}

	@WorkerThread
	suspend fun putBirthday (networkDateTime: String): Boolean {
		val response = api.putBirthday(networkDateTime).execute()
		return response.isSuccessful
	}
}