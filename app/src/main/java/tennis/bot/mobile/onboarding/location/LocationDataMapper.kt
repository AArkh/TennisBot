package tennis.bot.mobile.onboarding.location

import tennis.bot.mobile.onboarding.phone.CountryItem
import tennis.bot.mobile.utils.isRuLocale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationDataMapper @Inject constructor() {
    private val isRuLocale = isRuLocale()

    fun getCountryList(responseData: List<Location>): List<CountryItem> {
        return responseData.map { return@map CountryItem("", if(isRuLocale) it.countryName else it.countryNameEn, "") }
    }

    fun getCityList(responseData: List<Location>, selectedCountry: String): List<CountryItem> {
        val country = responseData.find { return@find (if(isRuLocale) it.countryName else it.countryNameEn) == selectedCountry }
        if (country?.cities?.isNotEmpty() == true) {
            return country.cities.map { return@map CountryItem("", if(isRuLocale) it.name else it.nameEn, "") }
        } else {
            return emptyList()
        }
    }

    fun getDistrictList(
        responseData: List<Location>,
        selectedCountry: String,
        selectedCity: String
    ): List<CountryItem> {
        val country = responseData.find { return@find (if(isRuLocale) it.countryName else it.countryNameEn) == selectedCountry }
        val city = country?.cities?.find { return@find (if(isRuLocale) it.name else it.nameEn) == selectedCity }
        if (country?.cities?.isNotEmpty() == true && city?.districts?.isNotEmpty() == true) {
            return city.districts.map { return@map CountryItem("", if(isRuLocale) it.title else it.nameEn, "") }
        } else {
            return emptyList()
        }
    }

    fun findCountryFromCity(responseData: List<Location>, selectedCity: Int): String? {
        for (country in responseData) {
            val cities: List<Location.LocationCity> = country.cities
            val city = cities.find { it.id == selectedCity }
            if (city != null) {
                return if(isRuLocale()) country.countryName else country.countryNameEn
            }
        }
        return null
    }

    fun findCityIntFromString(responseData: List<Location>, cityString: String): Int? {
        for (country in responseData) {
            val cities: List<Location.LocationCity> = country.cities
            val city = cities.find { (if(isRuLocale) it.name else it.nameEn) == cityString }
            if (city != null) {
                return city.id
            }
        }
        return null
    }

    fun findCityString(responseData: List<Location>, selectedCity: Int): String? {
        for (country in responseData) {
            val cities: List<Location.LocationCity> = country.cities
            val city = cities.find { it.id == selectedCity }
            if (city != null) {
                return if(isRuLocale()) city.name else city.nameEn
            }
        }
        return null
    }

    fun findDistrictIntFromString(responseData: List<Location>, selectedCity: Int?, selectedDistrict: String?): Int? {
        if (selectedCity == null || selectedDistrict == null) return null

        for (country in responseData) {
            val cities: List<Location.LocationCity> = country.cities
            val city = cities.find { it.id == selectedCity }
            if (city != null) {
                val district = city.districts.find { (if(isRuLocale) it.title else it.nameEn) == selectedDistrict }
                if (district != null) {
                    return district.id
                }
            }
        }
        return null
    }

    fun findDistrictStringFromCity(responseData: List<Location>, selectedCity: Int, selectedDistrict: Int): String? { // figure out a proper way for districts
        for (country in responseData) {
            val cities: List<Location.LocationCity> = country.cities
            val city = cities.find { it.id == selectedCity }
            if (city != null) {
                val district = city.districts.find { it.id == selectedDistrict }
                if (district != null) {
                    return if(isRuLocale()) district.title else district.nameEn
                }
            }
        }
        return null
    }
}