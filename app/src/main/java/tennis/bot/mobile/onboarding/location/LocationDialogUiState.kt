package tennis.bot.mobile.onboarding.location

import tennis.bot.mobile.onboarding.phone.CountryItem

sealed class LocationDialogUiState(
    open val currentCountry: String,
    open val currentCity: String,
) {

    data class Loading(
        override val currentCountry: String,
        override val currentCity: String,
    ) : LocationDialogUiState(currentCountry, currentCity)

    data class Error(
        override val currentCountry: String,
        override val currentCity: String,
    ) : LocationDialogUiState(currentCountry, currentCity)

    data class DataPassed(
        override val currentCountry: String,
        override val currentCity: String,
        val dataList: List<CountryItem>,
        val searchInput: String? = null,
    ) : LocationDialogUiState(currentCountry, currentCity)
}