package tennis.bot.mobile.feed.game

import kotlinx.serialization.Serializable
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface GameApi {

	@GET("api/game-orders/requests")
	suspend fun getAllRequests(
		@Query("skip") skip:Int = DEFAULT_SKIP,
		@Query("limit") limit:Int = DEFAULT_LIMIT
	): Response<GameBasicResponse>

	@GET("api/game-orders/incoming-requests")
	suspend fun getIncomingRequests(
		@Query("skip") skip:Int = DEFAULT_SKIP,
		@Query("limit") limit:Int = DEFAULT_LIMIT
	): Response<GameBasicResponse>

	@GET("api/game-orders/outcoming-requests")
	suspend fun getOutcomingRequests(
		@Query("skip") skip:Int = DEFAULT_SKIP,
		@Query("limit") limit:Int = DEFAULT_LIMIT
	): Response<GameBasicResponse>

	@GET("api/game-orders/accepted-requests")
	suspend fun getAcceptedRequests(
		@Query("skip") skip:Int = DEFAULT_SKIP,
		@Query("limit") limit:Int = DEFAULT_LIMIT
	): Response<GameAcceptedResponse>

	@POST("api/game-orders/{id}/response")
	suspend fun postRequestResponse(
		@Path("id") id: Long,
		@Query("comment") comment: String?,
	): Response<GameRequestResponse>

	@POST("api/game-orders/{id}/accept/{playerId}")
	suspend fun postInviteAccept(
		@Path("id") id: Long,
		@Path("playerId") playerId: Long,
	): Response<GameRequestResponse>

	@DELETE("api/game-orders/{id}/response/{playerId}")
	suspend fun postInviteDecline(
		@Path("id") id: Long,
		@Path("playerId") playerId: Long,
	): Response<GameRequestResponse>

	@DELETE("api/game-orders/{id}")
	suspend fun deleteGameRequest(
		@Path("id") id: Long
	): Response<GameRequestResponse>

	@DELETE("api/game-orders/{id}/my-response")
	suspend fun deleteMyGameResponse(
		@Path("id") id: Long
	): Response<GameRequestResponse>

	companion object{
		const val DEFAULT_SKIP = 0
		const val DEFAULT_LIMIT = 20
	}
}

@Serializable
data class GameRequestResponse(
	val id: Long
)

@Serializable
data class GameBasicResponse(
	val totalCount: Int,
	val items: List<GamePostNetwork>
)

@Serializable
data class GameAcceptedResponse(
	val totalCount: Int,
	val items: List<GameAcceptedPost>
)

@Serializable
data class GameAcceptedPost(
	val id: Long,
	val player: GamePlayer,
	val targetPlayer: GamePlayer,
	val cityId: Int,
	val districtId: Int?,
	val date: String,
	val isMale: Boolean?,
	val paymentTypeId: Int,
	val comment: String,
	val responseComment: String?
)

@Serializable
data class GamePostNetwork(
	val id: Long,
	val targetPlayerId: Long?,
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
	val games: Int? = null,
	val id: Long,
	val name: String,
	val surName: String?,
	val photo: String?,
	val photoUrl: String?,
	val isMale: Boolean,
	val experience: Int?,
	val rating: Int,
	val doublesRating: Int
)
