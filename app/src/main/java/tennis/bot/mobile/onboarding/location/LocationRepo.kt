package tennis.bot.mobile.onboarding.location

import android.content.Context
import androidx.annotation.WorkerThread
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import tennis.bot.mobile.onboarding.phone.CountryItem
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

    class DataMapper {
        fun getCountryList(responseData: List<Location>): List<CountryItem> {
            return responseData.map { return@map CountryItem(0, it.countryName, "") }
        }

        fun getCityList(responseData: List<Location>, selectedCountry: String): List<CountryItem> {
            val country = responseData.find { return@find it.countryName == selectedCountry }
            if (country?.cities?.isNotEmpty() == true) {
                return country.cities.map { return@map CountryItem(0, it.name, "") }
            } else {
                return emptyList()
            }
        }

        fun getDistrictList(responseData: List<Location>, selectedCountry: String, selectedCity: String): List<CountryItem> {
            val country = responseData.find { return@find it.countryName == selectedCountry }
            val city = country?.cities?.find { return@find it.name == selectedCity }
            if (country?.cities?.isNotEmpty() == true && city?.districts?.isNotEmpty() == true) {
                return city.districts.map { return@map CountryItem(0, it.title, "") }
            } else {
                return emptyList()
            }
        }
    }
}