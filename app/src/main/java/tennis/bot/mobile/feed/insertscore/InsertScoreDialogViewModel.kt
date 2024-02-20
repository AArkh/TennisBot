package tennis.bot.mobile.feed.insertscore

import android.widget.NumberPicker
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class InsertScoreDialogViewModel @Inject constructor(
	savedStateHandle: SavedStateHandle
): ViewModel() {

	companion object {
		const val REQUEST_SCORE_KEY = "REQUEST_SCORE_KEY"
		const val SELECTED_SCORE_KEY = "SELECTED_SCORE_KEY"
		const val SELECTED_SET_KEY = "SELECTED_SET_KEY"
	}

	val basicScoreVariants = arrayOf(
		"- - -", "6 - 0", "6 - 1", "6 - 2", "6 - 3", "6 - 4", "7 - 5", "7 - 6",
		"6 - 7", "5 - 7", "4 - 6", "3 - 6", "2 - 6", "1 - 6", "0 - 6"
	)

	val leftTieBreakScoreVariants = arrayOf(
		"0 - 7", "1 - 7", "2 - 7", "3 - 7", "4 - 7", "5 - 7",
		"6 - 8", "7 - 9", "8 - 10", "9 - 11", "10 - 12", "11 - 13", "12 - 14", "13 - 15"
	)

	val rightTieBreakScoreVariants = arrayOf(
		"7 - 0", "7 - 1", "7 - 2", "7 - 3", "7 - 4", "7 - 5",
		"8 - 6", "9 - 7", "10 - 8", "11 - 9", "12 - 10", "13 - 11", "14 - 12", "15 - 13"
	)

	private val _uiStateFlow = MutableStateFlow(
		InsertScoreDialogUiState(
			setNumber = savedStateHandle.get<Int>(InsertScoreFragment.SELECTED_SET_NUMBER),
			pickedValue = savedStateHandle.get<String>(InsertScoreFragment.SELECTED_SET_CURRENT_VALUE)
		)
	)
	val uiStateFlow = _uiStateFlow.asStateFlow()

	fun onScorePicked(valuePosition: Int, activity: FragmentActivity) {
		val pickedResult: String = basicScoreVariants[valuePosition]

		activity.supportFragmentManager.setFragmentResult(
			REQUEST_SCORE_KEY, // using set number to determine which value to change in InsertScoreFragment
			bundleOf(
				SELECTED_SCORE_KEY to pickedResult,
				SELECTED_SET_KEY to uiStateFlow.value.setNumber)
		)
	}

}

fun NumberPicker.setupWithCustomValues(scoreVariants: Array<String>) {
	minValue = 0
	maxValue = scoreVariants.size - 1
	displayedValues = scoreVariants
	descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
}
