package tennis.bot.mobile.onboarding.location

import android.content.Context
import androidx.annotation.WorkerThread
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationRepo @Inject constructor(
    @ApplicationContext val context: Context,
    val api: LocationApi,
    val dao: LocationDao, // Room shit
) {

    // This is called for precaching in mainActivity
    @WorkerThread
    suspend fun precacheLocations() = getLocations()

    // This is called at location fragment and dialog
    @WorkerThread
    suspend fun getLocations() : List<Any> {
        val cachedLocations = dao.getLocations()
        if (cachedLocations.isNotEmpty()) {
            return cachedLocations
        }
        val networkLocations = api.getLocationData().execute().body()
        dao.saveLocations(networkLocations)
        return networkLocations
    }
}