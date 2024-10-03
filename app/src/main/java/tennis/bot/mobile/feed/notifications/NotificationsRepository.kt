package tennis.bot.mobile.feed.notifications

import android.content.Context
import androidx.annotation.WorkerThread
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.qualifiers.ApplicationContext
import tennis.bot.mobile.utils.showToast
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationsRepository @Inject constructor(
	private val notificationsApi: NotificationsApi,
	@ApplicationContext private val context: Context
) {

	@WorkerThread
	suspend fun getAllNotifications(position: Int): NotificationsBasicResponse? {
		val response = notificationsApi.getNotifications(skip = position)

		if (response.isSuccessful) return response.body()
		else {
			FirebaseCrashlytics.getInstance().log("getAllNotifications code ${response.code()} and message: ${response.message()}")
			context.showToast("Something went wrong")
		}

		return null
	}
}