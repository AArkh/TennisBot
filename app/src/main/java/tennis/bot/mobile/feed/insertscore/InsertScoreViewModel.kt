package tennis.bot.mobile.feed.insertscore

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import tennis.bot.mobile.R
import tennis.bot.mobile.feed.searchopponent.OpponentItem
import tennis.bot.mobile.profile.account.UserProfileAndEnumsRepository
import tennis.bot.mobile.profile.matches.TennisSetNetwork
import tennis.bot.mobile.utils.showToast
import javax.inject.Inject

@HiltViewModel
class InsertScoreViewModel @Inject constructor(
	private val userProfileRepository: UserProfileAndEnumsRepository,
	private val repository: InsertScoreRepository
): ViewModel() {

	companion object {
		const val DEFAULT_SCORE = "0 : 0"
		const val MEDIA_INDEX = 1
		private const val SINGLE = 1
		private const val DOUBLE = 3
	}

	private val _uiStateFlow = MutableStateFlow(
		InsertScoreUiState(
			player1Image = null,
			player1Name = "",
			player2 = null
		)
	)
	val uiStateFlow = _uiStateFlow.asStateFlow()

	fun onInitial(opponents: Array<OpponentItem>) {

		val player1 = userProfileRepository.getProfile()
		if (opponents.size == SINGLE) {
			_uiStateFlow.value = _uiStateFlow.value.copy(
				player1Image = player1.photo,
				player1Name = player1.name.substringBefore(" "),
				player2 = opponents[0]
			)
		} else if (opponents.size == DOUBLE) {
			_uiStateFlow.value = _uiStateFlow.value.copy(
				player1Image = player1.photo,
				player1Name = player1.name.substringBefore(" "),
				player2 = opponents[0],
				player3 = opponents[1],
				player4 = opponents[2]
			)
		}
	}

	fun onAddingSetItem() {
		val currentSets = uiStateFlow.value.setsList
		if(currentSets.size < 5) {
			val newSets = currentSets + TennisSetItem((currentSets.size + 1),  DEFAULT_SCORE, isActive = true)

			_uiStateFlow.value =
				uiStateFlow.value.copy(setsList = newSets)
		}
	}

	fun onAddingSuperTieBreakItem() {
		val currentSets = uiStateFlow.value.setsList
		if(currentSets.size < 5) {
			val newSets = currentSets + TennisSetItem((currentSets.size + 1),  DEFAULT_SCORE, isSuperTieBreak = true, isActive = true)

			_uiStateFlow.value =
				uiStateFlow.value.copy(setsList = newSets)
		}
	}

	fun onDeletingSetItem(setNumber: Int) {
		val currentSets = uiStateFlow.value.setsList
		val position = setNumber - 1

		if (position == 0) {
			val newSets = listOf(currentSets[position].copy(score = DEFAULT_SCORE))
			val updatedList = uiStateFlow.value.mediaItemList.mapIndexed { index, item ->
				if (index == MEDIA_INDEX) item.let{ (item as InsertScoreMediaItem).copy(isPhotoBackgroundActive = false) } else item
			}

			_uiStateFlow.value = uiStateFlow.value.copy(
				setsList = newSets,
				mediaItemList = updatedList)
		} else {
			val newSets = (currentSets - currentSets[position])
				.mapIndexed { index, tennisSetItem ->  tennisSetItem.copy(setNumber = index + 1) }

			_uiStateFlow.value = uiStateFlow.value.copy(
				setsList = newSets)
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
		val updatedList = uiStateFlow.value.mediaItemList.mapIndexed { index, item ->
			if (index == MEDIA_INDEX) item.let{ (item as InsertScoreMediaItem).copy(isPhotoBackgroundActive = true) } else item
		}

		_uiStateFlow.value = uiStateFlow.value.copy(
			setsList = newSetList,
			mediaItemList = updatedList)
	}

	fun appointActiveSetItem() {
		val newList = uiStateFlow.value.setsList.mapIndexed { index, tennisSetItem ->
			if (index == uiStateFlow.value.setsList.lastIndex) tennisSetItem.copy(isActive = true) else tennisSetItem.copy(isActive = false)
		}

		_uiStateFlow.value = uiStateFlow.value.copy(setsList = newList)
	}

	fun onPickedPhoto(pickedImageUri: Uri) {
		val updatedList = uiStateFlow.value.mediaItemList.mapIndexed { index, item ->
			if (index == MEDIA_INDEX) item.let{ (item as InsertScoreMediaItem).copy(pickedPhoto = pickedImageUri) } else item
		}
		_uiStateFlow.value = uiStateFlow.value.copy(
			photoUri = pickedImageUri,
			mediaItemList = updatedList)
	}

	fun onPickedVideo(pickedVideoUri: Uri) {
		val updatedList = uiStateFlow.value.mediaItemList.mapIndexed { index, item ->
			if (index == MEDIA_INDEX) item.let{ (item as InsertScoreMediaItem).copy(pickedVideo = pickedVideoUri) } else item
		}
		_uiStateFlow.value = uiStateFlow.value.copy(
			videoUri = pickedVideoUri,
			mediaItemList = updatedList)
	}

	fun onDeletePickedPhoto() {
		val updatedList = uiStateFlow.value.mediaItemList.mapIndexed { index, item ->
			if (index == MEDIA_INDEX) item.let{ (item as InsertScoreMediaItem).copy(pickedPhoto = null) } else item
		}
		_uiStateFlow.value = uiStateFlow.value.copy(
			photoUri = null,
			mediaItemList = updatedList)
	}

	fun onDeletePickedVideo() {
		val updatedList = uiStateFlow.value.mediaItemList.mapIndexed { index, item ->
			if (index == MEDIA_INDEX) item.let{ (item as InsertScoreMediaItem).copy(pickedVideo = null) } else item
		}
		_uiStateFlow.value = uiStateFlow.value.copy(
			videoUri = null,
			mediaItemList = updatedList)
	}

	fun isAddSetButtonActive() {
		val setsList = uiStateFlow.value.setsList
		val lastSet = setsList.last()

		val player1Wins = setsList.count { getSetWinner(it.score) == 1 }
		val player2Wins = setsList.count { getSetWinner(it.score) == 2 }

		val isActive = lastSet.score != DEFAULT_SCORE && !setsList.any { it.isSuperTieBreak } && (player1Wins < 3 && player2Wins < 3) && setsList.size < 5

		_uiStateFlow.value = uiStateFlow.value.copy(isAddSetButtonActive = isActive)
	}

	fun isSuperTieBreakButtonActive() {
		val setsList = uiStateFlow.value.setsList

		val requiredWins = if (setsList.size <= 3) 1 else 2
		val requiredSize = setsList.size == 2 || setsList.size == 4
		val player1Wins = setsList.count { getSetWinner(it.score) == 1 }
		val player2Wins = setsList.count { getSetWinner(it.score) == 2 }

		val isActive = if (!setsList.any { it.isSuperTieBreak }) {
			if (requiredSize) player1Wins == requiredWins && player2Wins == requiredWins else false
		} else false

		_uiStateFlow.value = uiStateFlow.value.copy(isAddSuperTieBreakActive = isActive)
	}

	private fun getSetsScore(): List<TennisSetNetwork> {
		val setsList = uiStateFlow.value.setsList
		val convertedSetsList = mutableListOf<TennisSetNetwork>()

		for(set in setsList) {
			if (set.score.contains("(")) {
				val gamesPart = set.score.substringBefore(" (")
				val (player1Games, player2Games) = gamesPart.split(" : ").map { it.trim().toInt() }
				val tiebreakPart = set.score.substringAfter(" (").removeSuffix(")")
				val (player1Tiebreak, player2Tiebreak) = tiebreakPart.split(" - ").map { it.trim().toInt() }

				convertedSetsList.add(TennisSetNetwork(
					score1 = player1Games,
					score2 = player2Games,
					scoreTie1 = player1Tiebreak,
					scoreTie2 = player2Tiebreak))
				Log.d("getSetsScore", "convertedSetsList: $convertedSetsList")
			} else {
				val (player1Games, player2Games) = set.score.split(" : ").map { it.toInt() }

				convertedSetsList.add(TennisSetNetwork(
					score1 = player1Games,
					score2 = player2Games,
					scoreTie1 = 0,
					scoreTie2 = 0))
				Log.d("getSetsScore", "convertedSetsList: $convertedSetsList")
			}
		}

		return convertedSetsList.toList()
	}

	private fun getSetWinner(score: String): Int? {
		if (score.contains("(")) {
			val tiebreakPart = score.substringAfter(" (").removeSuffix(")")
			val (player1Tiebreak, player2Tiebreak) = tiebreakPart.split(" - ").map { it.trim().toInt() }
			val isValidTiebreak = (player1Tiebreak >= 7 || player2Tiebreak >= 7)

			return if (isValidTiebreak) {
				if (player1Tiebreak > player2Tiebreak) 1 else 2
			} else {
				null
			}
		} else {
			val (player1Games, player2Games) = score.split(" : ").map { it.toInt() }
			val isValidSet = (player1Games >= 6 || player2Games >= 6) &&
					((player1Games - player2Games) >= 2 || (player1Games - player2Games) <= -2)

			return if (isValidSet){
				if (player1Games > player2Games) 1 else 2
			} else {
				null
			}
		}
	}

	fun isMatchValid() {
		val requiredWins = if (uiStateFlow.value.setsList.size == 1) 1 else if (uiStateFlow.value.setsList.size <= 3) 2 else 3
		val player1Wins = uiStateFlow.value.setsList.count { getSetWinner(it.score) == 1 }
		val player2Wins = uiStateFlow.value.setsList.count { getSetWinner(it.score) == 2 }

		_uiStateFlow.value = uiStateFlow.value.copy(isSendButtonActive = player1Wins >= requiredWins || player2Wins >= requiredWins)
	}

	private fun showLoading() {
		val currentState = _uiStateFlow.value
		_uiStateFlow.value = currentState.copy(
			isLoading = true
		)
	}

	private fun onStopLoading() {
		val currentState = _uiStateFlow.value
		_uiStateFlow.value = currentState.copy(
			isLoading = false
		)
	}

	fun onSendButtonClicked(context: Context, navigationCallback: () -> Unit) {
		viewModelScope.launch(Dispatchers.IO) {
			kotlin.runCatching {
				showLoading()
				if(uiStateFlow.value.player3 != null && uiStateFlow.value.player4 != null) {
					repository.postDoublesScore(InsertScoreItem(
						teammatePlayerId = uiStateFlow.value.player2!!.id, // at this point it should not be null
						opponentPlayerId = uiStateFlow.value.player3!!.id,
						secondOpponentPlayerId = uiStateFlow.value.player4!!.id,
						photo = repository.postPhoto(uiStateFlow.value.photoUri), // works nicely but ask Andrey if that's a good practice
						video = repository.postVideo(uiStateFlow.value.videoUri),
						sets = getSetsScore()
					))
				} else {
					repository.postSingleScore(InsertScoreItem(
						opponentPlayerId = uiStateFlow.value.player2!!.id, // at this point it should not be null
						photo = repository.postPhoto(uiStateFlow.value.photoUri),
						video = repository.postVideo(uiStateFlow.value.videoUri),
						sets = getSetsScore()
					))
				}
			}.onFailure {
				onStopLoading()
				context.showToast(context.getString(R.string.error_no_network_message))
			}.onSuccess {
				onStopLoading()
				navigationCallback.invoke()
			}
		}
	}
}