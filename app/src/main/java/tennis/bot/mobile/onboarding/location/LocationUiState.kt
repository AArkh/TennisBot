package tennis.bot.mobile.onboarding.location

sealed class LocationUiState {
    object Error : LocationUiState()
    object Loading : LocationUiState()
    data class CountrySelected(
        val country: String,
        val nextButtonEnabled: Boolean, // true of there's no city in the country's dictionary
    ) : LocationUiState()

    data class CitySelected(
        val country: String,
        val city: String,
        val nextButtonEnabled: Boolean, // true of there's no district in the country's dictionary
    ) : LocationUiState()

    data class DistrictSelected(
        val country: String,
        val city: String,
        val district: String,
        val nextButtonEnabled: Boolean = true,
    ) : LocationUiState()
}
