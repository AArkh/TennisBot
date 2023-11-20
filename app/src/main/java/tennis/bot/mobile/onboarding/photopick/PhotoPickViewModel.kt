package tennis.bot.mobile.onboarding.photopick

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import tennis.bot.mobile.R
import tennis.bot.mobile.onboarding.location.LocationDialogUiState
import javax.inject.Inject

@HiltViewModel
class PhotoPickViewModel @Inject constructor(): ViewModel() {

    val imageList = listOf(
        CircledImage(R.drawable.photo_picker_1),
        CircledImage(R.drawable.photo_picker_2),
        CircledImage(R.drawable.photo_picker_3),
        CircledImage(R.drawable.photo_picker_4),
        CircledImage(R.drawable.photo_picker_5),
        CircledImage(R.drawable.photo_picker_6),
        CircledImage(R.drawable.photo_picker_7),
        CircledImage(R.drawable.photo_picker_8),
        CircledImage(R.drawable.photo_picker_9),
        CircledImage(R.drawable.photo_picker_10),
        CircledImage(R.drawable.photo_picker_11),
        CircledImage(R.drawable.photo_picker_12)
    )

    private val _uiStateFlow = MutableStateFlow<PhotoPickUiState>(PhotoPickUiState.Loading)
    val uiStateFlow = _uiStateFlow.asStateFlow()

    init {
        _uiStateFlow.value = PhotoPickUiState.DataPassed(imageList)
    }
}