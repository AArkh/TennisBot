package tennis.bot.mobile.onboarding.photopick

import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import tennis.bot.mobile.R
import javax.inject.Inject

@HiltViewModel
class PhotoPickViewModel @Inject constructor() : ViewModel() {

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
        onInitial()
    }

    fun onInitial() {
        _uiStateFlow.value = PhotoPickUiState.InitialWithIconList(imageList, false)
    }

    fun onPickedCircledImage(pickedCircledImage: CircledImage) {
        val newList = imageList.map { item ->
            if (item.isSelected) { // previously selected
                return@map item.copy(isSelected = false)
            } else if (item == pickedCircledImage) {
                return@map pickedCircledImage.copy(isSelected = true)
            } else {
                return@map item
            }
        }
        _uiStateFlow.value = PhotoPickUiState.PickedPreselectedImage(newList, true)
    }
}