package tennis.bot.mobile.feed.game

import android.content.Context
import androidx.annotation.WorkerThread
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.qualifiers.ApplicationContext
import tennis.bot.mobile.R
import tennis.bot.mobile.feed.activityfeed.AcceptedGameItem
import tennis.bot.mobile.feed.activityfeed.MatchRequestPostItem
import tennis.bot.mobile.feed.activityfeed.formatLocationDataForPost
import tennis.bot.mobile.feed.searchopponent.OpponentItem
import tennis.bot.mobile.onboarding.location.LocationDataMapper
import tennis.bot.mobile.onboarding.location.LocationRepository
import tennis.bot.mobile.profile.account.AccountPageViewModel
import tennis.bot.mobile.profile.account.UserProfileAndEnumsRepository
import tennis.bot.mobile.utils.formatDateForMatchPostItem
import tennis.bot.mobile.utils.showToast
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GameRepository @Inject constructor(
	private val gameApi: GameApi,
	private val userProfileRepository: UserProfileAndEnumsRepository,
	private val locationRepository: LocationRepository,
	private val locationDataMapper: LocationDataMapper,
	@ApplicationContext private val context: Context
) {

	fun getPlayerId(): Long {
		return userProfileRepository.getProfile().id
	}

	@WorkerThread
	suspend fun getAllRequests(position: Int): GameBasicResponse? {
		val response = gameApi.getAllRequests(skip = position)

		if (response.isSuccessful) return response.body()
		else {
			FirebaseCrashlytics.getInstance().log("getAllRequests code ${response.code()} and message: ${response.message()}")
			context.showToast("Something went wrong")
		}

		return null
	}

	@WorkerThread
	suspend fun getIncomingRequests(position: Int): GameBasicResponse? {
		val response = gameApi.getIncomingRequests(skip = position)

		if (response.isSuccessful) return response.body()
		else {
			FirebaseCrashlytics.getInstance().log("getIncomingRequests code ${response.code()} and message: ${response.message()}")
			context.showToast("Something went wrong")
		}

		return null
	}

	@WorkerThread
	suspend fun getOutcomingRequests(position: Int): GameBasicResponse? {
		val response = gameApi.getOutcomingRequests(skip = position)

		if (response.isSuccessful) return response.body()
		else {
			FirebaseCrashlytics.getInstance().log("getOutcomingRequests code ${response.code()} and message: ${response.message()}")
			context.showToast("Something went wrong")
		}

		return null
	}

	@WorkerThread
	suspend fun getAcceptedRequests(position: Int): GameAcceptedResponse? {
		val response = gameApi.getAcceptedRequests(skip = position)

		if (response.isSuccessful) return response.body()
		else {
			FirebaseCrashlytics.getInstance().log("getAcceptedRequests code ${response.code()} and message: ${response.message()}")
			context.showToast("Something went wrong")
		}

		return null
	}

	@WorkerThread
	suspend fun postRequestResponse(id: Long, comment: String?): Boolean {
		val response = kotlin.runCatching {
				gameApi.postRequestResponse(id, comment)
		}.getOrElse {
			FirebaseCrashlytics.getInstance().recordException(it)
			return false
		}

		return response.isSuccessful
	}

	@WorkerThread
	suspend fun deleteGameRequest(id: Long): Boolean {
		val response = kotlin.runCatching {
			gameApi.deleteGameRequest(id)
		}.getOrElse {
			FirebaseCrashlytics.getInstance().recordException(it)
			return false
		}

		return response.isSuccessful
	}

	@WorkerThread
	suspend fun deleteMyGameResponse(id: Long): Boolean {
		val response = kotlin.runCatching {
			gameApi.deleteMyGameResponse(id)
		}.getOrElse {
			FirebaseCrashlytics.getInstance().recordException(it)
			return false
		}

		return response.isSuccessful
	}

	suspend fun mapGameToMatchRequestPostItem(gameList: List<GamePostNetwork>?): List<MatchRequestPostItem> {
		if (gameList?.isEmpty() == true) return emptyList()

		else return gameList!!.map { game ->
			MatchRequestPostItem(
				id = game.id,
				gameOrderId = game.id,
				postType = 2,
				totalLikes = 0,
				liked = false,
				addedAt = game.createdAt,
				matchDate = formatDateForMatchPostItem(game.date),
				playerId = game.player.id,
				playerPhoto = game.player.photoUrl,
				playerName = game.player.name,
				playerRating = game.player.rating,
				locationSubTitle = formatLocationDataForPost(game.cityId, game.districtId, locationRepository, locationDataMapper),
				experience = userProfileRepository.getEnumById(Pair(AccountPageViewModel.EXPERIENCE_TITLE, game.player.experience)),
				comment = game.comment,
				responseComment = game.responseComment,
				isOwned = game.isOwned,
				isResponsed = game.isResponsed,
				targetPlayerId = game.targetPlayerId
			)
		}
	}

	suspend fun mapAcceptedGameToPostItem(gameList: List<GameAcceptedPost>?): List<AcceptedGameItem> {
		if (gameList?.isEmpty() == true) return emptyList()

		else return gameList!!.map { game ->
			AcceptedGameItem(
				id = game.id,
				matchDate = formatDateForMatchPostItem(game.date),
				player = OpponentItem(
					id = game.player.id,
					profilePicture = game.player.photoUrl,
					nameSurname = game.player.name,
					infoPanel = context.getString(R.string.player_info_panel_no_location,
						game.player.rating,
						game.player.doublesRating,
						userProfileRepository.getEnumById(Pair(AccountPageViewModel.EXPERIENCE_TITLE, game.player.experience))
					)),
				targetPlayer = OpponentItem(
					id = game.player.id,
					profilePicture = game.targetPlayer.photoUrl,
					nameSurname = game.targetPlayer.name,
					infoPanel = context.getString(R.string.player_info_panel_no_location,
						game.targetPlayer.rating,
						game.targetPlayer.doublesRating,
						userProfileRepository.getEnumById(Pair(AccountPageViewModel.EXPERIENCE_TITLE, game.targetPlayer.experience))
						))
			)
		}
	}
}