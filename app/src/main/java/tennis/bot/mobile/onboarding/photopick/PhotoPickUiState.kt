package tennis.bot.mobile.onboarding.photopick

import tennis.bot.mobile.onboarding.location.LocationDialogUiState
import tennis.bot.mobile.onboarding.phone.CountryItem

sealed class PhotoPickUiState {
    object Loading : PhotoPickUiState()

    object Error : PhotoPickUiState()

    data class DataPassed(
        val iconList: List<CircledImage>
    ) : PhotoPickUiState()
}
