package tennis.bot.mobile.feed.notifications

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import tennis.bot.mobile.MainActivity
import tennis.bot.mobile.R
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

		val intent = Intent(this, MainActivity::class.java).apply {
			flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
			putExtra("navigate_to_notifications", true)
		}

		val pendingIntent = PendingIntent.getActivity(
			this,
			0,
			intent,
			PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
		)

		val notification = NotificationCompat.Builder(this, "channel_id")
			.setSmallIcon(R.mipmap.ic_launcher)
			.setContentTitle(message.notification?.title ?: "Default Title")
			.setContentText(message.notification?.body ?: "Default Body")
			.setAutoCancel(true)
			.setContentIntent(pendingIntent)
			.build()

		val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
		notificationManager.notify(0, notification)
	}
}