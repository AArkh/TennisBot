package tennis.bot.mobile.feed.game

import android.content.Context
import androidx.annotation.WorkerThread
import dagger.hilt.android.qualifiers.ApplicationContext
import tennis.bot.mobile.feed.activityfeed.MatchRequestPostItem
import tennis.bot.mobile.feed.activityfeed.formatLocationDataForPost
import tennis.bot.mobile.onboarding.location.LocationDataMapper
import tennis.bot.mobile.onboarding.location.LocationRepository
import tennis.bot.mobile.profile.account.AccountPageViewModel
import tennis.bot.mobile.profile.account.UserProfileAndEnumsRepository
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

	@WorkerThread
	suspend fun getAllRequests(position: Int): GameBasicResponse? {
		val response = gameApi.getAllRequests(skip = position)

		if (response.code() == 200) return response.body()
		if (response.code() == 404) context.showToast("Something went wrong")

		return null
	}

	@WorkerThread
	suspend fun getIncomingRequests(position: Int): GameBasicResponse? {
		val response = gameApi.getIncomingRequests(skip = position)

		if (response.code() == 200) return response.body()
		if (response.code() == 404) context.showToast("Something went wrong")

		return null
	}

	@WorkerThread
	suspend fun getOutcomingRequests(position: Int): GameBasicResponse? {
		val response = gameApi.getOutcomingRequests(skip = position)

		if (response.code() == 200) return response.body()
		if (response.code() == 404) context.showToast("Something went wrong")

		return null
	}

	@WorkerThread
	suspend fun getAcceptedRequests(position: Int): GameBasicResponse? {
		val response = gameApi.getAcceptedRequests(skip = position)

		if (response.code() == 200) return response.body()
		if (response.code() == 404) context.showToast("Something went wrong")

		return null
	}

	@WorkerThread
	suspend fun postRequestResponse(id: Long, comment: String?): Boolean {
		val response = kotlin.runCatching {
			gameApi.postRequestResponse(id, comment)
		}.getOrElse { return false }

		return response.isSuccessful
	}

	@WorkerThread
	suspend fun deleteGameRequest(id: Long): Boolean {
		val response = kotlin.runCatching {
			gameApi.deleteGameRequest(id)
		}.getOrElse { return false }

		return response.isSuccessful
	}

	@WorkerThread
	suspend fun deleteMyGameResponse(id: Long): Boolean {
		val response = kotlin.runCatching {
			gameApi.deleteMyGameResponse(id)
		}.getOrElse { return false }

		return response.isSuccessful
	}

	suspend fun mapGameToMatchRequestPostItem(gameList: List<GamePostNetwork>?): List<MatchRequestPostItem> {
		if (gameList?.isEmpty() == true) return emptyList()

		else return gameList!!.map { game ->
			MatchRequestPostItem(
				id = game.id,
				postType = 2,
				totalLikes = 0,
				liked = false,
				addedAt = game.createdAt,
				matchDate = game.date,
				playerPhoto = game.player.photoUrl,
				playerName = game.player.name,
				playerRating = game.player.rating,
				locationSubTitle = formatLocationDataForPost(game.cityId, game.districtId, locationRepository, locationDataMapper),
				experience = userProfileRepository.getEnumById(Pair(AccountPageViewModel.EXPERIENCE_TITLE, game.player.experience)),
				comment = game.comment,
				isOwned = game.isOwned,
				isResponsed = game.isResponsed
			)
		}
	}
}