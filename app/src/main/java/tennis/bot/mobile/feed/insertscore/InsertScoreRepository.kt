package tennis.bot.mobile.feed.insertscore

import android.util.Log
import androidx.annotation.WorkerThread
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InsertScoreRepository @Inject constructor(
	private val api: InsertScoreApi
) {

	@WorkerThread
	suspend fun postAddScore(postBody: InsertScoreItem): Boolean {
		val response = kotlin.runCatching {
			api.postAddScore(postBody)
		}.getOrElse { return false }

		return response.isSuccessful
	}
}