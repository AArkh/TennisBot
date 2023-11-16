package tennis.bot.mobile.onboarding.location

import tennis.bot.mobile.onboarding.phone.CountryItem

sealed class LocationDialogUiState {

    object Loading : LocationDialogUiState()

    data class dataPassed(
        val dataList: List<CountryItem>
    ) : LocationDialogUiState()
}