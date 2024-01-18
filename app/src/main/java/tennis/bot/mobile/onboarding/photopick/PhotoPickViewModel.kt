package tennis.bot.mobile.onboarding.photopick

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import androidx.lifecycle.ViewModel
import coil.load
import coil.request.Disposable
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import tennis.bot.mobile.R
import tennis.bot.mobile.onboarding.survey.OnboardingRepository
import javax.inject.Inject
import kotlin.coroutines.coroutineContext

@HiltViewModel
class PhotoPickViewModel @Inject constructor(
    private val onboardingRepository: OnboardingRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val imageList = listOf(
        CircledImage(1, R.drawable.photo_picker_1),
        CircledImage(2,R.drawable.photo_picker_2),
        CircledImage(3,R.drawable.photo_picker_3),
        CircledImage(4,R.drawable.photo_picker_4),
        CircledImage(5,R.drawable.photo_picker_5),
        CircledImage(6,R.drawable.photo_picker_6),
        CircledImage(7,R.drawable.photo_picker_7),
        CircledImage(8,R.drawable.photo_picker_8),
        CircledImage(9,R.drawable.photo_picker_9),
        CircledImage(10,R.drawable.photo_picker_10),
        CircledImage(11,R.drawable.photo_picker_11),
        CircledImage(12,R.drawable.photo_picker_12)
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
            if (item.isSelected) {
                return@map item.copy(isSelected = false)
            } else if (item == pickedCircledImage) {
                return@map pickedCircledImage.copy(isSelected = true)
            } else {
                return@map item
            }
        }
        _uiStateFlow.value = PhotoPickUiState.PickedPreselectedImage(newList, true)
    }

    fun onPickedUserImage(pickedImageUri: Uri) {
        _uiStateFlow.value = PhotoPickUiState.PickedUserImage(
            userPickedImage = pickedImageUri,
            iconList = imageList,
            nextButtonEnabled = true
        )
    }

    fun onButtonNextClicked(navigationCallback: () -> Unit) {
        when(uiStateFlow.value) {
            is PhotoPickUiState.PickedPreselectedImage -> {
                val theId = (uiStateFlow.value as PhotoPickUiState.PickedPreselectedImage).iconListWithSelection.find { it.isSelected }?.id
                onboardingRepository.recordPreselectedPictureId(theId)
                Log.d("123456", "$theId prepicked image is successfully recorded")
            }
            is PhotoPickUiState.PickedUserImage -> {
                val theUrl = getRealPathFromUri(context, (uiStateFlow.value as PhotoPickUiState.PickedUserImage).userPickedImage)
                onboardingRepository.recordUserPickedPictureUri( theUrl.toString() )
                Log.d("123456", "$theUrl Url is successfully recorded as a String")
            }
            else -> {}
        }
        navigationCallback.invoke()
    }

}

fun getRealPathFromUri(context: Context, uri: Uri): String? {
    var realPath: String? = null
    val projection = arrayOf(MediaStore.Images.Media.DATA)
    val cursor: Cursor? = context.contentResolver.query(uri, projection, null, null, null)

    cursor?.use {
        val columnIndex: Int = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        it.moveToFirst()
        realPath = it.getString(columnIndex)
    }

    return realPath
}
