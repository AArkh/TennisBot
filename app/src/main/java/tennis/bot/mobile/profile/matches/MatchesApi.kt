package tennis.bot.mobile.profile.matches

import kotlinx.serialization.Serializable
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import tennis.bot.mobile.core.CoreUtilsItem

interface MatchesApi {
	@GET("api/games/scores/{playerId}")
	suspend fun getScores(
		@Path("playerId") playerId: Long,
		@Query("skip") skip:Int = DEFAULT_SKIP,
		@Query("limit") limit:Int = DEFAULT_LIMIT
	): Response<MatchBasicResponse>

	companion object{
		const val DEFAULT_SKIP = 0
		const val DEFAULT_LIMIT = 20
	}
}

@Serializable
data class MatchBasicResponse(
	val totalCount: Int,
	val items: List<MatchResponseItem>
)

@Serializable
data class MatchResponseItem(
	val id: Int,
	val win: Boolean,
	val isDouble: Boolean,
	val headToHead1: Int,
	val headToHead2: Int,
	val playedAt: String,
	val photo: String?,
	val video: String?,
	val tennisSets: List<TennisSetNetwork>,
	val players: List<Player>
)

@Serializable
data class TennisSetNetwork(
	val score1: Int,
	val score2: Int,
	val scoreTie1: Int?,
	val scoreTie2: Int?
): CoreUtilsItem()

@Serializable
data class Player(
	val id: Long,
	val name: String,
	val photo: String?,
	val oldRating: Int,
	val rating: Int
)


