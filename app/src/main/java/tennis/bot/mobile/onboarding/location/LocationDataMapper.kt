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
}