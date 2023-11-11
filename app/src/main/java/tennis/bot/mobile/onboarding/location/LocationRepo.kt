package tennis.bot.mobile.onboarding.location

import android.content.Context
import androidx.annotation.WorkerThread
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationRepo @Inject constructor(
    @ApplicationContext val context: Context,
    private val api: LocationApi,
    private val dao: LocationDao, // Room shit
) {

//     This is called for precaching in mainActivity
    @WorkerThread
    suspend fun precacheLocations() = getLocations()

//     This is called at location fragment and dialog

    @WorkerThread
    suspend fun getLocations() : List<Location> {
        dao.insert(Location(1, "Russia", "Russian flag", listOf(
                Location.LocationCity(2, "Moscow",
                listOf(
                    Location.LocationCity.LocationDistrict(3, "Khimky"),
                    Location.LocationCity.LocationDistrict(4, "Arbat"),
                    Location.LocationCity.LocationDistrict(5, "Red Square"))),
            Location.LocationCity(6, "SPb", listOf(
                Location.LocationCity.LocationDistrict(7, "Petroga"),
                Location.LocationCity.LocationDistrict(8, "Vasilevsky Island"),
                Location.LocationCity.LocationDistrict(9, "Devyatkino")
            )))))
        val cachedLocations = dao.getLocations()
        if (cachedLocations.isNotEmpty()) {
            return cachedLocations
        }
//        val networkLocations = api.getLocationData().execute().body()
//        dao.saveLocations(networkLocations)
//        return networkLocations
    return emptyList()
    }
}