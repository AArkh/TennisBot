package tennis.bot.mobile.profile.account

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface AllEnumsDao {
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insert(items: List<EnumType>)

	@Update
	suspend fun update(item: EnumType)

	@Delete
	suspend fun delete(item: EnumType)

	@Query("SELECT * from enumType WHERE type = :type")
	fun getEnumType(type: String): EnumType

	@Query("SELECT * from enumType")
	fun getAllEnumTypes(): List<EnumType>

}