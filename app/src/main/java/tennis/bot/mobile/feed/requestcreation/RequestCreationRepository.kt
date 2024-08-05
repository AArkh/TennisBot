package tennis.bot.mobile.feed.requestcreation

import androidx.annotation.WorkerThread
import com.google.firebase.crashlytics.FirebaseCrashlytics
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RequestCreationRepository @Inject constructor(
	private val api: RequestCreationApi
) {

	@WorkerThread
	suspend fun getPermissionToCreate(): Boolean? {
		val response = kotlin.runCatching {
		api.getPermissionToCreate()
		}.getOrElse {
			FirebaseCrashlytics.getInstance().recordException(it)
			return false
		}

		return response.body()
	}

	@WorkerThread
	suspend fun postAddRequest(postBody: RequestNetwork): Boolean {
		val response = api.postNewRequest(postBody)
		if (!response.isSuccessful) {
			FirebaseCrashlytics.getInstance().log("postAddRequest code ${response.code()} and message: ${response.message()}")
			throw Exception("Network request failed with code: ${response.code()}") // had to manually throw the exception because the regular way didn't work with 400 code

		}

		return response.isSuccessful
	}
}