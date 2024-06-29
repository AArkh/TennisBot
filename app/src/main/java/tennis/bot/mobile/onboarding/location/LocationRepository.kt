package tennis.bot.mobile.onboarding.location

import androidx.annotation.WorkerThread
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationRepository @Inject constructor(
    private val api: LocationApi,
    private val dao: LocationDao,
) {

    private val memoCache : List<Location> = emptyList() // todo small optimisation, чтобы не лазить в бд каждый раз

    @WorkerThread
    suspend fun precacheLocations(): List<Location> {
        val networkLocations = api.getLocationData().execute().body()
        dao.insert(networkLocations!!)
        return networkLocations
    }

    @WorkerThread
    suspend fun getLocations() : List<Location> {
        if (memoCache.isNotEmpty()) {
            return memoCache
        }
        val cachedLocations = dao.getLocations().sortedWith(compareBy { -it.cities.size })
        if (cachedLocations.isNotEmpty()) {
            return cachedLocations
        }
        return precacheLocations()
    }

    @WorkerThread
    suspend fun getLocationsById() : Map<Int, Location> {
        if (memoCache.isNotEmpty()) {
            return memoCache
        }
        val cachedLocations = dao.getLocations().sortedWith(compareBy { -it.cities.size })
        if (cachedLocations.isNotEmpty()) {
            return cachedLocations
        }
        return precacheLocations()
    }
}