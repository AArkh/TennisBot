package tennis.bot.mobile.feed.game

import kotlinx.serialization.Serializable
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface GameApi {

	@GET("api/game-orders/requests")
	suspend fun getAllRequests(
		@Query("skip") skip:Int = DEFAULT_SKIP,
		@Query("limit") limit:Int = DEFAULT_LIMIT
	): Response<GameBasicResponse>

	companion object{
		const val DEFAULT_SKIP = 0
		const val DEFAULT_LIMIT = 20
	}
}

@Serializable
data class GameBasicResponse(
	val totalCount: Int,
	val items: List<GamePostNetwork>
)

@Serializable
data class GamePostNetwork(
	val id: Long,
	val targetPlayerId: Int?,
	val player: GamePlayer,
	val isOwned: Boolean,
	val isResponsed: Boolean,
	val cityId: Int,
	val districtId: Int?,
	val date: String,
	val createdAt: String,
	val gameTypeId: Int,
	val isMale: Boolean?,
	val paymentTypeId: Int,
	val comment: String,
	val responseComment: String?
)

@Serializable
data class GamePlayer(
	val games: Int,
	val id: Long,
	val name: String,
	val surName: String?,
	val photo: String?,
	val photoUrl: String?,
	val isMale: Boolean,
	val experience: Int,
	val rating: Int,
	val doublesRating: Int
)
