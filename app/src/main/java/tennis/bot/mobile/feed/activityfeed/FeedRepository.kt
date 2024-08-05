package tennis.bot.mobile.feed.activityfeed

import android.content.Context
import androidx.annotation.WorkerThread
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.qualifiers.ApplicationContext
import tennis.bot.mobile.utils.showToast
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FeedRepository @Inject constructor(
	private val feedApi: FeedApi,
	private val likesApi: LikesApi,
	@ApplicationContext private val context: Context
) {
	@WorkerThread
	suspend fun getActivities(position: Int): ActivityBasicResponse? {
		val response = feedApi.getActivities(skip = position)

		if (response.isSuccessful) return response.body()
		else {
			FirebaseCrashlytics.getInstance().log("getActivities code ${response.code()} and message: ${response.message()}")
			context.showToast("Something went wrong")
		}

		return null
	}

	@WorkerThread
	suspend fun postLike(postId: Long): Boolean {
		val response = kotlin.runCatching {
			likesApi.postLike(postId)
		}.getOrElse{
			FirebaseCrashlytics.getInstance().recordException(it)
			return false
		}


		return response.isSuccessful
	}

	@WorkerThread
	suspend fun postUnlike(postId: Long): Boolean {
		val response = kotlin.runCatching {
			likesApi.postUnlike(postId)
		}.getOrElse {
			FirebaseCrashlytics.getInstance().recordException(it)
			return false
		}

		return response.isSuccessful

	}
}