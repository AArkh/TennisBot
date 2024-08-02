package tennis.bot.mobile.onboarding.location

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import retrofit2.Call
import retrofit2.http.GET
import tennis.bot.mobile.core.CoreUtilsItem

interface LocationApi {
    @GET("api/dictionaries/countries")
    fun getLocationData(): Call<List<Location>>
}

@Entity(tableName = "location")
@Serializable
data class Location(
    @PrimaryKey @ColumnInfo(name = "id") val id: Int,
    @SerialName("name") @ColumnInfo(name = "name") val countryName: String,
    @SerialName("nameEn") @ColumnInfo(name = "nameEn") val countryNameEn: String?,
    @ColumnInfo(name = "flag") val flag: String,
    @ColumnInfo(name = "cities") val cities: List<LocationCity>,
) : CoreUtilsItem() {
    @Serializable
    data class LocationCity(
        val id: Int,
        val name: String,
        val nameEn: String?,
        val districts: List<LocationDistrict>
    ) {
        @Serializable
        data class LocationDistrict(
            val id: Int,
            val title: String
        )
    }
}