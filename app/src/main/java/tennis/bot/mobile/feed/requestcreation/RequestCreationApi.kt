package tennis.bot.mobile.feed.requestcreation

import kotlinx.serialization.Serializable
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface RequestCreationApi {

	@GET("api/game-orders/permission-to-create")
	suspend fun getPermissionToCreate(): Response<Boolean>

	@POST("api/game-orders")
	suspend fun postNewRequest(
		@Body request: RequestNetwork
	): Response<Unit>
}

@Serializable
data class RequestNetwork(
	val targetPlayerId: Long? = null,
	val cityId: Int,
	val districtId: Int?,
	val date: String,
	val gameType: Int,
	val paymentTypeId: Int,
	val comment: String
)