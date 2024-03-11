package tennis.bot.mobile.feed.insertscore

import kotlinx.serialization.Serializable
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import tennis.bot.mobile.profile.matches.TennisSetNetwork

interface InsertScoreApi {

	@POST("api/games/score")
	suspend fun postAddScore(@Body newScore: InsertScoreItem): Response<Unit>
}

@Serializable
data class InsertScoreItem(
	val opponentPlayerId: Long,
	val secondOpponentPlayerId: Long? = null,
	val teammatePlayerId: Long? = null,
	val tournamentId: Int? = null,
	val photo: String? = null, // content is sent using a different post. here's just the name of the content. same for video
	val video: String? = null,
	val sets: List<TennisSetNetwork>
)