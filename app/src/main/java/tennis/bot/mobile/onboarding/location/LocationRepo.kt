package tennis.bot.mobile.onboarding.location

import androidx.annotation.WorkerThread
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationRepo @Inject constructor(
    private val api: LocationApi,
    private val dao: LocationDao,
) {

    @WorkerThread
    suspend fun precacheLocations(): List<Location> {
        val networkLocations = api.getLocationData().execute().body()
        dao.insert(networkLocations!!)
        return networkLocations
    }

    @WorkerThread
    suspend fun getLocations() : List<Location> {
        val cachedLocations = dao.getLocations()
            // .sortedBy { it.cities != null } этот кусок кода наверное не сработает
        // добавить сортировку, чтобы страны, в которых ЕСТЬ города были в начале списка
        if (cachedLocations.isNotEmpty()) {
            return cachedLocations
        }
        return precacheLocations()
    }
}