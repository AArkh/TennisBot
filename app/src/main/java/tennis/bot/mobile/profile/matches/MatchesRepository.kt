package tennis.bot.mobile.profile.matches

import androidx.annotation.WorkerThread
import tennis.bot.mobile.core.AuthTokenRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MatchesRepository @Inject constructor(
	private val api: MatchesApi,
	private val tokenRepo: AuthTokenRepository
) {
	@WorkerThread
	suspend fun getMatches() : List<MatchResponseItem> {
		return api.getScores().execute().body() ?: emptyList()
	}
}