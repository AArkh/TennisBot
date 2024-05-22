package tennis.bot.mobile.feed.requestcreation

import kotlinx.serialization.Serializable
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface RequestCreationApi {

	@POST("api/game-orders")
	suspend fun postNewRequest(
		@Body request: RequestNetwork
	): Response<Unit>
}

@Serializable
data class RequestNetwork(
	val targetPlayerId: Long? = null, // не факт, что вообще стоит оставлять
	val cityId: Int,
	val districtId: Int?,
	val date: String,
	val gameType: Int,
	val paymentTypeId: Int,
	val comment: String
)