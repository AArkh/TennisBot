package tennis.bot.mobile.onboarding.account

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import retrofit2.Call
import retrofit2.http.GET

interface EnumsApi {

	@GET("api/dictionaries/enums")
	fun getAllEnums(): Call<List<EnumType>>
}

@Entity(tableName = "enumType")
@Serializable
data class EnumType(
	@PrimaryKey(autoGenerate = true) @Transient val id: Int = 0,
	@ColumnInfo(name = "type") val type: String,
	@ColumnInfo(name = "enums") val enums: List<EnumData>
)

@Serializable
data class EnumData(
	val id: Int,
	val name: String,
	@SerialName("nameEn") val nameEnglish: String
)
