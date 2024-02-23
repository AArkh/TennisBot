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
			val updatedList = listOf(uiStateFlow.value.mediaItemList[0].copy(isPhotoBackgroundActive = false))
			_uiStateFlow.value = uiStateFlow.value.copy(
				setsList = newSets,
				isAddSetButtonActive = false,
				isAddSuperTieBreakActive = isSuperTieBreakButtonActive(),
				mediaItemList = updatedList)
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
		val updatedList = listOf(uiStateFlow.value.mediaItemList[0].copy(isPhotoBackgroundActive = true))

		_uiStateFlow.value = uiStateFlow.value.copy(
			setsList = newSetList,
			isAddSetButtonActive = true,
			isAddSuperTieBreakActive = isSuperTieBreakButtonActive(),
			mediaItemList = updatedList)
	}

	fun onPickedPhoto(pickedImageUri: Uri) {
		val updatedList = listOf(uiStateFlow.value.mediaItemList[0].copy(pickedPhoto = pickedImageUri))
		_uiStateFlow.value = uiStateFlow.value.copy(mediaItemList = updatedList)
	}

	fun onPickedVideo(pickedVideoUri: Uri) {
		val updatedList = listOf(uiStateFlow.value.mediaItemList[0].copy(pickedVideo = pickedVideoUri))
		_uiStateFlow.value = uiStateFlow.value.copy(mediaItemList = updatedList)
	}

	fun onDeletePickedPhoto() {
		val updatedList = listOf(uiStateFlow.value.mediaItemList[0].copy(pickedPhoto = null))
		_uiStateFlow.value = uiStateFlow.value.copy(mediaItemList = updatedList)
	}

	fun onDeletePickedVideo() {
		val updatedList = listOf(uiStateFlow.value.mediaItemList[0].copy(pickedVideo = null))
		_uiStateFlow.value = uiStateFlow.value.copy(mediaItemList = updatedList)
	}

	private fun isSuperTieBreakButtonActive(): Boolean {
		return uiStateFlow.value.setsList.size == 4 || uiStateFlow.value.setsList.size == 2
	}
}