package tennis.bot.mobile.onboarding.photopick

import android.net.Uri

sealed class PhotoPickUiState {
    object Loading : PhotoPickUiState()

    object Error : PhotoPickUiState()

    data class InitialWithIconList(
        val iconList: List<CircledImage>,
        val nextButtonEnabled: Boolean
    ) : PhotoPickUiState()

    data class PickedPreselectedImage(
        val iconListWithSelection: List<CircledImage>,
        val nextButtonEnabled: Boolean
    ) : PhotoPickUiState()

    data class PickedUserImage(
        val userPickedImage: Uri, // choose the correct type
        val iconList: List<CircledImage>,
        val nextButtonEnabled: Boolean
    ) : PhotoPickUiState()
}
