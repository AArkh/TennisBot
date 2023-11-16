package tennis.bot.mobile.onboarding.location

import tennis.bot.mobile.onboarding.phone.CountryItem

sealed class LocationDialogUiState {

    object Loading : LocationDialogUiState()

    data class countryDataPassed(
        val dataList: List<CountryItem>
    ): LocationDialogUiState()

    data class cityDataPassed(
        val dataList: List<CountryItem>
    ): LocationDialogUiState()

    data class districtDataPassed(
        val dataList: List<CountryItem>
    ): LocationDialogUiState()
}