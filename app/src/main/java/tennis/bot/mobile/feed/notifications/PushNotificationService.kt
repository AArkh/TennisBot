package tennis.bot.mobile.feed.notifications

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class PushNotificationService: FirebaseMessagingService() {

	@Inject
	lateinit var notificationsRepository: NotificationsRepository

	override fun onNewToken(token: String) {
		super.onNewToken(token)
		Log.d("FCM", "New token: $token")
//		kotlin.runCatching {
			CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
				notificationsRepository.postSetFirebaseToken(token)
			}
//		}
	}

	override fun onMessageReceived(message: RemoteMessage) {
		super.onMessageReceived(message)

		// action to take on push
	}
}