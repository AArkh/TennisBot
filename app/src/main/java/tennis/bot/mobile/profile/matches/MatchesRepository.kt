package tennis.bot.mobile.profile.matches

import android.content.Context
import android.icu.text.SimpleDateFormat
import androidx.annotation.WorkerThread
import dagger.hilt.android.qualifiers.ApplicationContext
import tennis.bot.mobile.core.AuthTokenRepository
import tennis.bot.mobile.profile.account.UserProfileAndEnumsRepository
import tennis.bot.mobile.utils.showToast
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MatchesRepository @Inject constructor(
	private val api: MatchesApi,
	private val userProfileAndEnumsRepository: UserProfileAndEnumsRepository,
	private val tokenRepo: AuthTokenRepository,
	@ApplicationContext private val context: Context
) {

	private val dateTimeFormatter = SimpleDateFormat( "yyyy-MM-dd'T'hh:mm:ss'Z'", Locale.getDefault())
	private val someOtherFormatter = SimpleDateFormat("d MMMM, HH:mm", Locale("ru", "RU"))

	@WorkerThread
	suspend fun getMatches() : MatchBasicResponse? {
		val response = api.getScores(userProfileAndEnumsRepository.getProfile().id)
		if (response.code() == 200) return response.body()
		if (response.code() == 404) context.showToast("Something went wrong")

		return MatchBasicResponse(0, emptyList())
	}

	@WorkerThread
	suspend fun getMatchItems(): List<MatchItem> {
		val networkMatches = getMatches()
		val matchResponseItems = networkMatches?.items
		return if (!matchResponseItems.isNullOrEmpty()) matchResponseItems.convertToMatchItemList()
		else emptyList()
	}

	private fun List<MatchResponseItem>.convertToMatchItemList(): List<MatchItem> {
		return map { matchResponseItem ->
			val playerOneProfilePic = matchResponseItem.players.getOrNull(0)?.photo
			val playerTwoProfilePic = matchResponseItem.players.getOrNull(1)?.photo

			val timeStampMs = dateTimeFormatter.parse(matchResponseItem.playedAt)
			val dateTime = someOtherFormatter.format(timeStampMs) ?: ""

			MatchItem(
				matchResponseItem.win,
				matchResponseItem.isDouble,
				playerOneProfilePic,
				matchResponseItem.players.getOrNull(0)?.name ?: "",
				matchResponseItem.players.getOrNull(0)?.rating.toString(),
				matchResponseItem.players.getOrNull(0)?.oldRating.toString(),
				playerTwoProfilePic,
				matchResponseItem.players.getOrNull(1)?.name ?: "",
				matchResponseItem.players.getOrNull(1)?.rating.toString(),
				matchResponseItem.players.getOrNull(1)?.oldRating.toString(),
				"${matchResponseItem.headToHead1} - ${matchResponseItem.headToHead2}",
				matchResponseItem.gameSets,
				dateTime
			)
		}
	}
}