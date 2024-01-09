package tennis.bot.mobile.onboarding.account

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

interface UserProfileApi {

	@GET("api/tennis-players/me")
	fun getProfile(@Header("Authorization") authHeader: String): Call<ProfileData>
}

@Serializable
data class ProfileData(
	val isRightHand: Boolean?,
	val isOneBackhand: Boolean?,
	val doublesRating: Int?,
	val mixedRating: Int?,
	val universalDoublesRating: Int?,
	val bonus: Int?,
	val bonusRank: Int?,
	val primaryLocation: String?,
	val secondaryLocation: String?,
	val racquet: String?,
	val racquetDetail: String?,
	val shoes: String?,
	val racquetStrings: String?,
	val surface: String?,
	val lastGame: String?,
	val premiumExpiration: String?,
	val id: Long,
	val name: String,
	val surName: String?,
	val birthday: String,
	val isMale: Boolean,
	val isInvited: Boolean?,
	val telegram: String?,
	val photo: String?,
	val countryId: Int?,
	val cityId: Int?,
	val districtId: Int?,
	val experience: Int,
	val initialRating: Int,
	val rating: Int,
	val games: Int?,
	val gamesWin: Int?
)
