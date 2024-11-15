package tennis.bot.mobile.onboarding.location

import androidx.room.Database
import androidx.room.ProvidedTypeConverter
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Database(entities = [Location::class], version = 4, exportSchema = false)
@TypeConverters(LocationCityConverter::class)
abstract class LocationDatabase : RoomDatabase() {
    abstract fun locationDao() : LocationDao
}

@ProvidedTypeConverter
class LocationCityConverter {
    @TypeConverter
    fun cityFromList(value: List<Location.LocationCity>) = Json.encodeToString(value)

    @TypeConverter
    fun cityToList(value: String) = Json.decodeFromString<List<Location.LocationCity>>(value)
}
