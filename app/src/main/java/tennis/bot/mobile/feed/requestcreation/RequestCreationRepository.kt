package tennis.bot.mobile.feed.requestcreation

import androidx.annotation.WorkerThread
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RequestCreationRepository @Inject constructor(
	private val api: RequestCreationApi
) {

	@WorkerThread
	suspend fun postAddRequest(postBody: RequestNetwork): Boolean {
		val response = kotlin.runCatching {
			api.postNewRequest(postBody)
		}.getOrElse { return false }

		return response.isSuccessful
	}
}