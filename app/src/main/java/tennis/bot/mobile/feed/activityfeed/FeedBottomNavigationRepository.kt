package tennis.bot.mobile.feed.activityfeed

import android.content.Context
import androidx.annotation.WorkerThread
import dagger.hilt.android.qualifiers.ApplicationContext
import tennis.bot.mobile.utils.showToast
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FeedBottomNavigationRepository @Inject constructor(
	private val api: FeedApi,
	@ApplicationContext private val context: Context
) {
	@WorkerThread
	suspend fun getActivities(): List<PostData>? {
		val response = api.getActivities()

		if (response.code() == 200) return response.body()?.items
		if (response.code() == 404) context.showToast("Something went wrong")

		return emptyList()
	}


}