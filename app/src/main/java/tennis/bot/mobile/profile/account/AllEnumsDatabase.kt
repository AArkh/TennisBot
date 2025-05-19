package tennis.bot.mobile.profile.account

import androidx.room.Database
import androidx.room.ProvidedTypeConverter
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


@Database(entities = [EnumType::class], version = 2, exportSchema = false)
@TypeConverters(EnumTypeConverter::class)
abstract class AllEnumsDatabase : RoomDatabase() {
	abstract fun allEnumsDao() : AllEnumsDao
}

@ProvidedTypeConverter
class EnumTypeConverter {
	@TypeConverter
	fun typeFromList(value: List<EnumData>) = Json.encodeToString(value)

	@TypeConverter
	fun typeToList(value: String) = Json.decodeFromString<List<EnumData>>(value)
}