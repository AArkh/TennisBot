package tennis.bot.mobile.feed.activityfeed

import android.content.Context
import androidx.annotation.WorkerThread
import dagger.hilt.android.qualifiers.ApplicationContext
import tennis.bot.mobile.utils.showToast
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FeedBottomNavigationRepository @Inject constructor(
	private val feedApi: FeedApi,
	private val likesApi: LikesApi,
	@ApplicationContext private val context: Context
) {
	@WorkerThread
	suspend fun getActivities(): List<PostData>? {
		val response = feedApi.getActivities()

		if (response.code() == 200) return response.body()?.items
		if (response.code() == 404) context.showToast("Something went wrong")

		return emptyList()
	}

	@WorkerThread
	suspend fun postLike(postId: Long): Boolean {
		val response = kotlin.runCatching {
			likesApi.postLike(postId)
		}.getOrElse{ return false }


		return response.isSuccessful
	}

	@WorkerThread
	suspend fun postUnlike(postId: Long): Boolean {
		val response = kotlin.runCatching {
			likesApi.postUnlike(postId)
		}.getOrElse { return false }

		return response.isSuccessful

	}


}