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

	@WorkerThread
	suspend fun getNotificationIndicators(): NotificationIndicators? {
		val response = notificationsApi.getNotificationIndicators()

		if (response.isSuccessful) return response.body()
		else {
			FirebaseCrashlytics.getInstance().log("getNotificationIndicators code ${response.code()} and message: ${response.message()}")
			context.showToast("Something went wrong")
		}

		return null
	}

	@WorkerThread
	suspend fun postReadAllNotifications(type: Int, lastIndicator: Int): Boolean {
		val response = kotlin.runCatching {
			notificationsApi.postReadAllNotifications(type, lastIndicator)
		}.getOrElse {
			FirebaseCrashlytics.getInstance().recordException(it)
			return false
		}

		return response.isSuccessful
	}

	@WorkerThread
	suspend fun postSendTestPush(token: String): Boolean {
		val response = kotlin.runCatching {
			notificationsApi.postSendTestPush(token)
		}.getOrElse {
			FirebaseCrashlytics.getInstance().recordException(it)
			return false
		}

		return response.isSuccessful
	}

	@WorkerThread
	suspend fun postSetFirebaseToken(token: String): Boolean {
		val response = kotlin.runCatching {
			notificationsApi.postSetFirebaseToken(token)
		}.getOrElse {
			FirebaseCrashlytics.getInstance().recordException(it)
			return false
		}

		return response.isSuccessful
	}
}