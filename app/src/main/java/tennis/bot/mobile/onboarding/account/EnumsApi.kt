package tennis.bot.mobile.onboarding.account

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import retrofit2.Call
import retrofit2.http.GET

interface EnumsApi {

	@GET("api/dictionaries/enums")
	fun getAllEnums(): Call<List<EnumType>>
}

@Serializable
data class EnumType(
	val type: String,
	val enums: List<EnumData>
)

@Serializable
data class EnumData(
	val id: Int,
	val name: String,
	@SerialName("nameEn") val nameEnglish: String
)
