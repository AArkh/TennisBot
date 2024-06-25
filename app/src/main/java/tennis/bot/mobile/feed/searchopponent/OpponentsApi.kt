package tennis.bot.mobile.feed.searchopponent

import kotlinx.serialization.Serializable
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface OpponentsApi {
	@GET("api/tennis-players/")
	suspend fun getOpponents(
		@Query("name") playerName: String,
		@Query("ticks") skip: Int = DEFAULT_SKIP,
		@Query("limit") limit: Int = DEFAULT_LIMIT,
	): Response<OpponentsBasicResponse>

	companion object{
		const val DEFAULT_SKIP = 0
		const val DEFAULT_LIMIT = 20
	}
}

@Serializable
data class OpponentsBasicResponse(
	val totalCount: Int,
	val items: List<OpponentResponseItem>
)

@Serializable
data class OpponentResponseItem(
	val id: Long,
	val name: String,
	val surName: String?,
	val birthday: String?,
	val isMale: Boolean?,
	val telegram: String?,
	val photo: String?,
	val photoUrl: String?,
	val countryID: Int?,
	val cityId: Int?,
	val districtId: Int?,
	val experience: Int?,
	val initialRating: Int?,
	val rating: Int?,
	val doublesRating: Int?,
	val games: Int?,
	val gamesWin: Int?,
	val isInvited: Boolean?
)