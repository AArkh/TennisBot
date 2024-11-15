package tennis.bot.mobile.feed.notifications

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PushNotificationService: FirebaseMessagingService() {

	override fun onNewToken(token: String) {
		super.onNewToken(token)
		Log.d("FCM", "New token: $token")
	}

	override fun onMessageReceived(message: RemoteMessage) {
		super.onMessageReceived(message)

		// action to take on push
	}
}