package tennis.bot.mobile.onboarding.location

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface LocationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(items: List<Location>)

    @Update
    suspend fun update(item: Location)

    @Delete
    suspend fun delete(item: Location)

    @Query("SELECT * from location WHERE id = :id")
    fun getLocation(id: Int): Location

    @Query("SELECT * from location ORDER BY name ASC")
    fun getLocations(): List<Location>

}
