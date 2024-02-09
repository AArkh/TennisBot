package tennis.bot.mobile.profile.editgamedata

import kotlinx.serialization.Serializable
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.PUT

interface EditGameDataApi {

	@PUT("api/tennis-players")
	fun putIsRightHand(@Body isRightHand: IsRightHandNetwork): Call<Unit>

	@PUT("api/tennis-players")
	fun putIsOneBackhand(@Body isOneBackhand: IsOneBackhandNetwork): Call<Unit>

	@PUT("api/tennis-players")
	fun putSurface(@Body surface: SurfaceNetwork): Call<Unit>

	@PUT("api/tennis-players")
	fun putShoes(@Body shoes: ShoesNetwork): Call<Unit>

	@PUT("api/tennis-players")
	fun putRacquet(@Body racquet: RacquetNetwork): Call<Unit>

	@PUT("api/tennis-players")
	fun putRacquetStrings(@Body racquetStrings: RacquetStringsNetwork): Call<Unit>
}

@Serializable
data class IsRightHandNetwork(val isRightHand: Boolean?)

@Serializable
data class IsOneBackhandNetwork(val isOneBackhand: Boolean?)

@Serializable
data class SurfaceNetwork(val surface: Int?)

@Serializable
data class ShoesNetwork(val shoes: Int?)

@Serializable
data class RacquetNetwork(val racquet: Int?)

@Serializable
data class RacquetStringsNetwork(val racquetStrings: Int?)