package tennis.bot.mobile.onboarding.photopick

import android.net.Uri

sealed class PhotoPickUiState(
    open val userPickedImage: Uri? = null,
) {
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
        override val userPickedImage: Uri,
        val iconList: List<CircledImage>,
        val nextButtonEnabled: Boolean
    ) : PhotoPickUiState(userPickedImage)
}
