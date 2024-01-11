package tennis.bot.mobile.onboarding.account

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

	@Query("SELECT * from location WHERE id = :id")
	fun getEnumType(id: Int): EnumType

	@Query("SELECT * from location ORDER BY countryName ASC")
	fun getAllEnumTypes(): List<EnumType>

}