package tennis.bot.mobile.onboarding.location

import tennis.bot.mobile.onboarding.phone.CountryItem

sealed class LocationDialogUiState(
    open val currentCountry: String,
    open val currentCity: String,
    open val currentDistrict: String,
) {

    data class Loading(
        override val currentCountry: String,
        override val currentCity: String,
        override val currentDistrict: String,
    ) : LocationDialogUiState(currentCountry, currentCity, currentDistrict)

    data class Error(
        override val currentCountry: String,
        override val currentCity: String,
        override val currentDistrict: String,
    ) : LocationDialogUiState(currentCountry, currentCity, currentDistrict)

    data class DataPassed(
        override val currentCountry: String,
        override val currentCity: String,
        override val currentDistrict: String,
        val dataList: List<CountryItem>,
        val searchInput: String? = null,
    ) : LocationDialogUiState(currentCountry, currentCity, currentDistrict)
}