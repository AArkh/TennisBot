package tennis.bot.mobile.onboarding.photopick

import android.widget.ImageView

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
        val customSelectedImage: ImageView, // choose the correct type
        val nextButtonEnabled: Boolean
    ) : PhotoPickUiState()
}
