package tennis.bot.mobile.onboarding.location

import androidx.room.Database
import androidx.room.ProvidedTypeConverter
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

@Database(entities = [Location::class], version = 1, exportSchema = false)
@TypeConverters(LocationCityConverter::class)
abstract class LocationDatabase : RoomDatabase() {
    abstract fun locationDao() : LocationDao
}

@ProvidedTypeConverter
class LocationCityConverter {
    @TypeConverter
    fun CityfromList(value: List<Location.LocationCity>) = Json.encodeToString(value) //todo naming here

    @TypeConverter
    fun CitytoList(value: String) = Json.decodeFromString<List<Location.LocationCity>>(value)

    // todo why so black?? research
    @TypeConverter
    fun DistrictfromList(value: List<Location.LocationCity.LocationDistrict>) = Json.encodeToString(value)

    @TypeConverter
    fun DistricttoList(value: String) = Json.decodeFromString<List<Location.LocationCity.LocationDistrict>>(value)
}
