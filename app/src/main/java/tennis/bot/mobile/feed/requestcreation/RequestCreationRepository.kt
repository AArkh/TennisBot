package tennis.bot.mobile.feed.requestcreation

import android.util.Log
import androidx.annotation.WorkerThread
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
		}.getOrElse { return false }

		return response.body()
	}

	@WorkerThread
	suspend fun postAddRequest(postBody: RequestNetwork): Boolean {
		val response = api.postNewRequest(postBody)
		if (!response.isSuccessful) {
			throw Exception("Network request failed with code: ${response.code()}") // had to manually throw the exception because the regular way didn't work with 400 code
		}

		return response.isSuccessful
	}
}