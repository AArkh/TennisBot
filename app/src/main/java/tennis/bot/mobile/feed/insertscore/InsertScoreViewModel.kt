package tennis.bot.mobile.feed.insertscore

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.lifecycle.ViewModel
import coil.dispose
import coil.load
import com.google.android.material.imageview.ShapeableImageView
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import tennis.bot.mobile.profile.account.UserProfileAndEnumsRepository
import javax.inject.Inject

@HiltViewModel
class InsertScoreViewModel @Inject constructor(
	private val userProfileRepository: UserProfileAndEnumsRepository,
): ViewModel() {

	companion object {
		const val DEFAULT_SCORE = "0 - 0"
	}

	private val _uiStateFlow = MutableStateFlow(
		InsertScoreUiState(
			player1Image = null,
			player1Name = "",
			player2Id = 0,
			player2Image = null,
			player2Name = ""
		)
	)
	val uiStateFlow = _uiStateFlow.asStateFlow()

	fun onInitial(opponentId: Long, opponentPhoto: String?, opponentName: String) {
		val player1 = userProfileRepository.getProfile()
		_uiStateFlow.value = _uiStateFlow.value.copy(
			player1Image = player1.photo,
			player1Name = player1.name.substringBefore(" "),
			player2Id = opponentId,
			player2Image = opponentPhoto,
			player2Name = opponentName.substringBefore(" "),
		)
	}

	fun onAddingSetItem() {
		val currentSets = uiStateFlow.value.setsList
		if(currentSets.size < 5) {
			val newSets = currentSets + TennisSetItem((currentSets.size + 1),  DEFAULT_SCORE)
			_uiStateFlow.value =
				uiStateFlow.value.copy(setsList = newSets, isAddSetButtonActive = false, isAddSuperTieBreakActive = isSuperTieBreakButtonActive())
		} else {
			_uiStateFlow.value = uiStateFlow.value.copy(isAddSetButtonActive = false, isAddSuperTieBreakActive = false)
		}
	}

	fun onDeletingSetItem(setNumber: Int) {
		val currentSets = uiStateFlow.value.setsList
		val position = setNumber - 1

		if (position == 0) {
			val newSets = listOf(currentSets[position].copy(score = DEFAULT_SCORE))
			_uiStateFlow.value = uiStateFlow.value.copy(
				setsList = newSets,
				isAddSetButtonActive = false,
				isAddSuperTieBreakActive = isSuperTieBreakButtonActive(),
				isPhotoBackgroundActive = false)
		} else {
			val newSets = currentSets - currentSets[position]
			_uiStateFlow.value = uiStateFlow.value.copy(
				setsList = newSets,
				isAddSetButtonActive = true,
				isAddSuperTieBreakActive = isSuperTieBreakButtonActive())
		}
	}

	fun onScoreReceived(setNumber: Int,score: String) {
		val newSetList = uiStateFlow.value.setsList.map { setItem ->
			if (setItem.setNumber == setNumber) {
				score.let { setItem.copy(score = it) }
			} else {
				setItem
			}
		}
		_uiStateFlow.value = uiStateFlow.value.copy(
			setsList = newSetList,
			isAddSetButtonActive = true,
			isAddSuperTieBreakActive = isSuperTieBreakButtonActive(),
			isPhotoBackgroundActive = true)
	}

	fun onPickedPhoto(pickedImageUri: Uri) {
		_uiStateFlow.value = uiStateFlow.value.copy(pickedPhoto = pickedImageUri)
	}

	fun onPickedVideo(pickedVideoUri: Uri) {
		_uiStateFlow.value = uiStateFlow.value.copy(pickedVideo = pickedVideoUri)
	}

	fun onDeletePickedPhoto(imageView: ShapeableImageView) {
		imageView.dispose()
		imageView.load(null)

		_uiStateFlow.value = uiStateFlow.value.copy(pickedPhoto = null)
	}

	fun onDeletePickedVideo(imageView: ShapeableImageView) {
		imageView.dispose()
		imageView.load(null)

		_uiStateFlow.value = uiStateFlow.value.copy(pickedVideo = null)
	}

	fun getVideoDuration(context: Context, videoUri: Uri?): String  {
		val retriever = MediaMetadataRetriever()

		try {
			retriever.setDataSource(context, videoUri)
			val durationString = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
			val durationInMillis = durationString?.toLong() ?: 0L

			val minutes = (durationInMillis / 1000 / 60).toInt()
			val seconds = (durationInMillis / 1000 % 60).toInt()

			return String.format("%02d:%02d", minutes, seconds)
		} catch (e: Exception) {
			e.printStackTrace()
		} finally {
			retriever.release()
		}

		return "00:00" // Return a default value if there's an error
	}


	private fun isSuperTieBreakButtonActive(): Boolean {
		return uiStateFlow.value.setsList.size == 4 || uiStateFlow.value.setsList.size == 2
	}
}