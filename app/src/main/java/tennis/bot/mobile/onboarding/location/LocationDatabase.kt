package tennis.bot.mobile.onboarding.location

import androidx.room.Database
import androidx.room.ProvidedTypeConverter
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Database(entities = [Location::class], version = 1, exportSchema = false)
@TypeConverters(LocationCityConverter::class, LocationDistrictConverter::class)
abstract class LocationDatabase : RoomDatabase() {
    abstract fun locationDao() : LocationDao
}

@ProvidedTypeConverter
class LocationCityConverter {
    @TypeConverter
    fun fromList(value: List<Location.LocationCity>) = Json.encodeToString(value)

    @TypeConverter
    fun toList(value: String) = Json.decodeFromString<List<Location.LocationCity>>(value)
}

class LocationDistrictConverter {
    @TypeConverter
    fun fromList(value: List<Location.LocationCity.LocationDistrict>) = Json.encodeToString(value)

    @TypeConverter
    fun toList(value: String) = Json.decodeFromString<List<Location.LocationCity.LocationDistrict>>(value)
}
