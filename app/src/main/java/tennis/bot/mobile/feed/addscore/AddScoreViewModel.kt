package tennis.bot.mobile.feed.addscore

import android.content.Context
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import tennis.bot.mobile.R
import tennis.bot.mobile.feed.searchopponent.SearchOpponentsFragment.Companion.SCORE_TYPE_REQUEST_KEY
import tennis.bot.mobile.feed.searchopponent.SearchOpponentsFragment.Companion.SELECTED_SCORE_TYPE_OPTION
import tennis.bot.mobile.onboarding.survey.SurveyResultItem
import javax.inject.Inject

@HiltViewModel
class AddScoreViewModel @Inject constructor(
	@ApplicationContext private val context: Context
): ViewModel() {

	private val _uiStateFlow = MutableStateFlow(
		AddScoreUiState(
			nextButtonEnabled = false,
			sideNoteTitle = context.getString(R.string.add_score_side_note_title),
			sideNoteText = context.getString(R.string.add_score_side_note_text)
		)
	)
	val uiStateFlow = _uiStateFlow.asStateFlow()

	val sideNoteItems = listOf(
		SurveyResultItem(context.getString(R.string.side_note_item_title_1), context.getString(R.string.side_note_item_value_1)),
		SurveyResultItem(context.getString(R.string.side_note_item_title_2), context.getString(R.string.side_note_item_value_2)),
		SurveyResultItem(context.getString(R.string.side_note_item_title_3), context.getString(R.string.side_note_item_value_3)),
		SurveyResultItem(context.getString(R.string.side_note_item_title_4), context.getString(R.string.side_note_item_value_4))
	)

	fun onOptionPicked(activity: FragmentActivity, pickedOption: Int?){
		if (pickedOption == null) return

		when(pickedOption) {
			AddScoreFragment.SCORE_SINGLE -> {
				_uiStateFlow.value = _uiStateFlow.value.copy(
					pickedOption = pickedOption,
					nextButtonEnabled = true,
					sideNoteTitle = context.getString(R.string.add_score_single_side_note_title),
					sideNoteText = context.getString(R.string.add_score_single_side_note_text),
					sideNoteContainer = false)
			}
			AddScoreFragment.SCORE_DOUBLE -> {
				_uiStateFlow.value = _uiStateFlow.value.copy(
					pickedOption = pickedOption,
					nextButtonEnabled = true,
					sideNoteTitle = context.getString(R.string.add_score_double_side_note_title),
					sideNoteText = context.getString(R.string.add_score_double_side_note_text),
					sideNoteContainer = false)
			}
			AddScoreFragment.SCORE_TOURNAMENT -> {}
			AddScoreFragment.SCORE_FRIENDLY -> {
				_uiStateFlow.value = _uiStateFlow.value.copy(
					pickedOption = pickedOption,
					nextButtonEnabled = true,
					sideNoteTitle = context.getString(R.string.add_score_friendly_side_note_title),
					sideNoteText = context.getString(R.string.add_score_friendly_side_note_text),
					sideNoteContainer = false)
			}
		}

		_uiStateFlow.value = _uiStateFlow.value.copy(pickedOption = pickedOption, nextButtonEnabled = true)

		activity.supportFragmentManager.setFragmentResult(
			SCORE_TYPE_REQUEST_KEY,
			bundleOf(SELECTED_SCORE_TYPE_OPTION to pickedOption)
		)
	}

	fun onNextButtonClicked(navigationCallback: () -> Unit){
		navigationCallback.invoke()
	}
}