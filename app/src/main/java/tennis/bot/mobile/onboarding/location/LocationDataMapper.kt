package tennis.bot.mobile.onboarding.location

import tennis.bot.mobile.onboarding.phone.CountryItem
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationDataMapper @Inject constructor() {

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

    fun getDistrictList(
        responseData: List<Location>,
        selectedCountry: String,
        selectedCity: String
    ): List<CountryItem> {
        val country = responseData.find { return@find it.countryName == selectedCountry }
        val city = country?.cities?.find { return@find it.name == selectedCity }
        if (country?.cities?.isNotEmpty() == true && city?.districts?.isNotEmpty() == true) {
            return city.districts.map { return@map CountryItem(0, it.title, "") }
        } else {
            return emptyList()
        }
    }

    fun findCountryFromCity(responseData: List<Location>, selectedCity: Int): String? {
        for (country in responseData) {
            val cities: List<Location.LocationCity> = country.cities
            val city = cities.find { it.id == selectedCity }
            if (city != null) { // ??
                return country.countryName
            }
        }
        return null
    }

    fun findCityString(responseData: List<Location>, selectedCity: Int): String? {
        for (country in responseData) {
            val cities: List<Location.LocationCity> = country.cities
            val city = cities.find { it.id == selectedCity }
            if (city != null) { // ??
                return city.name

            }
        }
        return null
    }

//    fun findDistrictFromCity(responseData: List<Location>,country: String, selectedCity: Int): String? { // figure out a proper way for districts
//        for (country in responseData) {
//            val cities: List<Location.LocationCity> = country.cities
//            val city = cities.find { it.id == selectedCity }
//            val districts = city?.districts
//            val dis
//            if (city != null) { // ??
//                return city.name
//            }
//        }
//        return null
//    }
}