package tennis.bot.mobile.onboarding.location

import tennis.bot.mobile.onboarding.phone.CountryItem

sealed class LocationDialogUiState {
    object Loading : LocationDialogUiState()

    object Error : LocationDialogUiState()

    data class DataPassed(
        val dataList: List<CountryItem>
    ) : LocationDialogUiState()
}