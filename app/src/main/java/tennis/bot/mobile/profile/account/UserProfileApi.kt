package tennis.bot.mobile.profile.account

import kotlinx.serialization.Serializable
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

interface UserProfileApi {

	@GET("api/tennis-players/me")
	fun getProfile(): Call<ProfileData>
}

@Serializable
data class ProfileData(
	val id: Long,
	val name: String,
	val surName: String?,
	val birthday: String?,
	val isMale: Boolean,
	val isInvited: Boolean?,
	val telegram: String?,
	val photo: String?,
	val countryId: Int?,
	val cityId: Int,
	val districtId: Int?,
	val experience: Int?,
	val initialRating: Int,
	val rating: Int,
	val doublesRating: Int?,
	val games: Int?,
	val gamesWin: Int?,
	val isRightHand: Boolean?,
	val isOneBackhand: Boolean?,
	val mixedRating: Int?,
	val universalDoublesRating: Int?,
	val bonus: Int?,
	val bonusRank: Int?,
	val primaryLocation: Int?,
	val secondaryLocation: Int?,
	val racquet: Int?,
	val racquetDetail: String?,
	val shoes: Int?,
	val racquetStrings: Int?,
	val surface: Int?,
	val lastGame: String?,
	val premiumExpiration: String?
)
