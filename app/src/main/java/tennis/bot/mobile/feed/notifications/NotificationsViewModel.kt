package tennis.bot.mobile.feed.notifications

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import tennis.bot.mobile.profile.matches.TennisSetNetwork
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor(
	private val notificationsRepository: NotificationsRepository
): ViewModel()  {

	// JSON 1: Type 1 (Player Notification)
	val notification1 = NotificationData(
		id = 1,
		type = 1,
		status = 1,
		createdAt = "2024-08-12T16:49:47.185489Z",
		content = NotificationContentParent.GameOrderInvite(
		type = 1,
		playerName = "Руслан Громов",
		selfNotify = false,
		gameOrderId = 24772,
		playerPhoto = "3a14b437-12ff-893e-e4ed-bd1961853247.jpg"
	))

	// JSON 2: Type 2 (Player with ID)
	val playerWithId = NotificationData(
		id = 2,
		type = 2,
		status = 1,
		createdAt = "2024-08-12T16:49:47.185489Z",
		content =NotificationContentParent.GameOrderResponse(
		type = 2,
		playerId = 465796307,
		playerName = "Александр Ящук",
		selfNotify = false,
		gameOrderId = 24928,
		playerPhoto = "default7.png"
	))

	// JSON 3: Match with two players (Type 3)
	val match1 = NotificationData(
		id = 3,
		type = 3,
		status = 1,
		createdAt = "2024-08-12T16:49:47.185489Z",
		content = NotificationContentParent.SinglesScore(
		sets = listOf(
			TennisSetNetwork(score1 = 7, score2 = 6, scoreTie1 = 7, scoreTie2 = 5),
			TennisSetNetwork(score1 = 7, score2 = 6, scoreTie1 = 7, scoreTie2 = 5)
		),
		type = 3,
		player1 = NotificationContentParent.NotificationPlayer(id = 362029671, name = "Denis Zabozlaev", photo = "3a123c91-249c-f2e3-b64d-edfb5e42af01.png"),
		player2 = NotificationContentParent.NotificationPlayer(id = 194747948, name = "Mikhail Lipaev", photo = "3a123c9e-2078-719c-2826-9325b80c49ed.png"),
		scoreId = 29827,
		selfNotify = false,
		gameOrderId = null
	))

	// JSON 4: Type 4 (Score Update)
	val scoreUpdate = NotificationData(
			id = 4,
	type = 4,
	status = 1,
	createdAt = "2024-08-12T16:49:47.185489Z",
	content = NotificationContentParent.Bonus(type = 4, scoreId = 29827, addBonus = 0, oldBonus = 0))

	// JSON 5: PlayerContact (Type 5)
	val playerContact = NotificationData(
		id = 5,
		type = 5,
		status = 1,
		createdAt = "2024-08-12T16:49:47.185489Z",
		content =NotificationContentParent.GameOrderAccept(
		type = 5,
		phone = "79111549965",
		playerId = 269397757,
		telegram = "alamagus",
		playerName = "Самир Мамеев",
		selfNotify = false,
		gameOrderId = 24928,
		playerPhoto = "3a123cb2-65a4-94ef-815e-030aa8e84ce7.png"
	))

	// JSON 6: Type 6 (Notification with SelfNotify)
	val notification2 = NotificationData(
		id = 6,
		type = 6,
		status = 1,
		createdAt = "2024-08-12T16:49:47.185489Z",
		content = NotificationContentParent.GameOrderCreate(
		type = 6,
		playerName = "Самир Мамеев",
		selfNotify = true,
		gameOrderId = 24928,
		playerPhoto = "3a123cb2-65a4-94ef-815e-030aa8e84ce7.png"
	))

	// JSON 7: Type 7 (Another Player Notification)
	val notification3 = NotificationData(
		id = 7,
		type = 7,
		status = 1,
		createdAt = "2024-08-12T16:49:47.185489Z",
		content =NotificationContentParent.GameOrderResponseDecline(
		type = 7,
		playerName = "Кирилл Наумов",
		selfNotify = false,
		gameOrderId = 24905,
		playerPhoto = "3a13f745-c31f-c9c6-3ebc-9921c3e75835.jpg"
	))

	// JSON 8: Match with four players (Type 8)
	val match2 = NotificationData(
		id = 8,
		type = 8,
		status = 1,
		createdAt = "2024-08-12T16:49:47.185489Z",
		content = NotificationContentParent.DoublesScore(
		sets = listOf(
			TennisSetNetwork(score1 = 7, score2 = 6, scoreTie1 = 7, scoreTie2 = 5),
			TennisSetNetwork(score1 = 7, score2 = 6, scoreTie1 = 7, scoreTie2 = 5)
		),
		type = 8,
		player1 = NotificationContentParent.NotificationPlayer(id = 1385385831, name = "Андрей Попов", photo = "3a123ca4-fa5d-1f4b-3875-e131eca908de.png"),
		player2 = NotificationContentParent.NotificationPlayer(id = 95077064, name = "Анатолий Попов", photo = "3a123ca9-d55c-eea5-9a0b-714ece0c64b9.png"),
		player3 = NotificationContentParent.NotificationPlayer(id = 109456877, name = "Алексей Мазунин", photo = "3a123c9f-0c3b-bf49-9c85-58e88ee45886.png"),
		player4 = NotificationContentParent.NotificationPlayer(id = 130191095, name = "Алексей Шашкин", photo = "3a123cb3-e4b6-83b3-f1f8-caa114fda2c2.png"),
		scoreId = 29826,
		selfNotify = false,
		gameOrderId = null
	))

	val list = listOf(notification1, playerWithId, match1, scoreUpdate, playerContact, notification2, notification3, match2)
}
