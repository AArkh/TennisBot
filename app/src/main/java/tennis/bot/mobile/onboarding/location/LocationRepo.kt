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

//    var data: List<Location> = emptyList()
//    private val notStubData = api.getLocationData().enqueue(object: Callback<List<Location>> {
//        override fun onResponse(call: Call<List<Location>>, response: Response<List<Location>>) {
//            data = response.body()!!
//            Log.d("1234567", "onResponse is a Success")
//        }
//
//        override fun onFailure(call: Call<List<Location>>, t: Throwable) {
//            Log.d("1234567", "onFailure is triggered by $t")
//        }
//
//    })

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

    // fixme Это херовый паттерн. Вынести в отдельный файл, через DI и передаваить в конструктор viewModel
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