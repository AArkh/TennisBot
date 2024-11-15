package tennis.bot.mobile.feed.notifications

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import tennis.bot.mobile.core.CoreUtilsItem
import tennis.bot.mobile.profile.matches.TennisSetNetwork

interface NotificationsApi {

	@GET("api/notifications")
	suspend fun getNotifications(
		@Query("skip") skip:Int = DEFAULT_SKIP,
		@Query("limit") limit:Int = DEFAULT_LIMIT
	): Response<NotificationsBasicResponse>

	@GET("api/notifications/new-indicators")
	suspend fun getNotificationIndicators(): Response<NotificationIndicators>

	@POST("api/notifications/read-all-indicators")
	suspend fun postReadAllNotifications(
		@Query("type") type: Int,
		@Query("lastIndicator") lastIndicator: Int
	): Response<Unit>

	@POST("api/notifications/send-test-push")
	suspend fun postSendTestPush(
		@Query("token") token: String,
	): Response<Unit>

	@POST("api/tennis-players/set-firebase-token")
	suspend fun postSetFirebaseToken(
		@Query("token") token: String,
	): Response<Unit>

	companion object{
		const val DEFAULT_SKIP = 0
		const val DEFAULT_LIMIT = 20
	}
}

@Serializable
data class NotificationIndicators(
	val notifications: Int,
	val gameOrdersAll: Int,
	val gameOrdersInput: Int,
	val gameOrdersOutput: Int,
	val gameOrdersAccepted: Int
)

@Serializable
data class NotificationsBasicResponse(
	val totalCount: Int,
	val items: List<NotificationData>
)

@Serializable
data class NotificationData(
	val id: Int,
	val type: Int,
	val status: Int,
	val createdAt: String,
	val content: NotificationContentParent
): CoreUtilsItem()

@Serializable
sealed class NotificationContentParent {

	@Serializable
	data class NotificationPlayer(
		val id: Long,
		val name: String,
		val photo: String?
	)

	@Serializable
	@SerialName("1")
	data class GameOrderInvite(
		val type: Int,
		val gameOrderId: Int,
		val playerName: String,
		val playerPhoto: String?,
		val selfNotify: Boolean
	): NotificationContentParent()

	@Serializable
	@SerialName("2")
	data class GameOrderResponse(
		val type: Int,
		val gameOrderId: Int,
		val playerName: String,
		val playerPhoto: String?,
		val playerId: Long,
		val selfNotify: Boolean
	):NotificationContentParent()

	@Serializable
	@SerialName("3")
	data class SinglesScore(
		val type: Int,
		val gameOrderId: Int?,
		val sets: List<TennisSetNetwork>,
		val player1: NotificationPlayer,
		val player2: NotificationPlayer,
		val scoreId: Int,
		val selfNotify: Boolean
	):NotificationContentParent()

	@Serializable
	@SerialName("4")
	data class Bonus(
		val type: Int,
		val scoreId: Int,
		val addBonus: Int,
		val oldBonus: Int
	):NotificationContentParent()

	@Serializable
	@SerialName("5")
	data class GameOrderAccept(
		val type: Int,
		val phone: String,
		val playerId: Long,
		val telegram: String,
		val playerName: String,
		val selfNotify: Boolean,
		val gameOrderId: Int,
		val playerPhoto: String?
	):NotificationContentParent()

	@Serializable
	@SerialName("6")
	data class GameOrderCreate(
		val type: Int,
		val gameOrderId: Int,
		val playerName: String,
		val playerPhoto: String?,
		val selfNotify: Boolean
	): NotificationContentParent()

	@Serializable
	@SerialName("7")
	data class GameOrderResponseDecline(
		val type: Int,
		val gameOrderId: Int,
		val playerName: String,
		val playerPhoto: String?,
		val selfNotify: Boolean
	): NotificationContentParent()

	@Serializable
	@SerialName("8")
	data class DoublesScore(
		val type: Int,
		val gameOrderId: Int?,
		val sets: List<TennisSetNetwork>,
		val player1: NotificationPlayer,
		val player2: NotificationPlayer,
		val player3: NotificationPlayer,
		val player4: NotificationPlayer,
		val scoreId: Int,
		val selfNotify: Boolean
	):NotificationContentParent()
}