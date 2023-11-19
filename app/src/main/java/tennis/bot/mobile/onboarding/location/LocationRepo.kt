package tennis.bot.mobile.onboarding.location

import android.content.Context
import android.util.Log
import androidx.annotation.WorkerThread
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import tennis.bot.mobile.onboarding.phone.CountryItem
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationRepo @Inject constructor(
    @ApplicationContext val context: Context,
    private val api: LocationApi,
    private val dao: LocationDao, // Room shit
) {

    private val stubData = listOf(Location(1, "Russia", "Russian flag", listOf(
        Location.LocationCity(2, "Moscow",
            listOf(
                Location.LocationCity.LocationDistrict(3, "Khimky"),
                Location.LocationCity.LocationDistrict(4, "Arbat"),
                Location.LocationCity.LocationDistrict(5, "Red Square"))),
        Location.LocationCity(6, "SPb", listOf(
            Location.LocationCity.LocationDistrict(7, "Petroga"),
            Location.LocationCity.LocationDistrict(8, "Vasilevsky Island"),
            Location.LocationCity.LocationDistrict(9, "Devyatkino")
        )))),
        Location(10, "Serbia", "Serbian flag", listOf(
            Location.LocationCity(11, "Belgrade", listOf())
        )),
        Location(12, "USA", "USA flag", listOf()),
        Location(13, "UK", "UK flag", listOf()),
    )

    @WorkerThread
    suspend fun precacheLocations(): List<Location> {
        val networkLocations = api.getLocationData().execute().body()
        dao.insert(networkLocations!!)
        return networkLocations
    }

    @WorkerThread
    suspend fun getLocations() : List<Location> {
        val cachedLocations = dao.getLocations()
        if (cachedLocations.isNotEmpty()) {
            return cachedLocations
        }
        return precacheLocations()
    }
}