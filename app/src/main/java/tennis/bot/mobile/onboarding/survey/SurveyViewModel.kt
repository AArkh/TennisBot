package tennis.bot.mobile.onboarding.survey

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import tennis.bot.mobile.R
import tennis.bot.mobile.onboarding.photopick.CircledImage
import tennis.bot.mobile.onboarding.photopick.PhotoPickUiState
import javax.inject.Inject

@HiltViewModel
class SurveyViewModel @Inject constructor(
	@ApplicationContext context: Context,
	private val repository: AccountInfoRepository

): ViewModel() {

	val questionsTitlesList = listOf(
		context.getString(R.string.survey_questionsTitlesList_1),
		context.getString(R.string.survey_questionsTitlesList_2),
		context.getString(R.string.survey_questionsTitlesList_3),
		context.getString(R.string.survey_questionsTitlesList_4),
		context.getString(R.string.survey_questionsTitlesList_5),
		context.getString(R.string.survey_questionsTitlesList_6),
		context.getString(R.string.survey_questionsTitlesList_7),
		context.getString(R.string.survey_questionsTitlesList_8),
		context.getString(R.string.survey_questionsTitlesList_9)
	)

	val optionsList = listOf(
		SurveyItem(
			context.getString(R.string.survey_options_item_set1_1),
			context.getString(R.string.survey_options_item_set1_2),
			context.getString(R.string.survey_options_item_set1_3),
			context.getString(R.string.survey_options_item_set1_4),
			ContextCompat.getString(context, R.string.survey_side_note_title_1),
			ContextCompat.getString(context, R.string.survey_side_note_text_1)
		),
		SurveyItem(
			context.getString(R.string.survey_options_item_set1_1),
			context.getString(R.string.survey_options_item_set1_2),
			context.getString(R.string.survey_options_item_set1_3),
			context.getString(R.string.survey_options_item_set1_4),
			ContextCompat.getString(context, R.string.survey_side_note_title_2),
			ContextCompat.getString(context, R.string.survey_side_note_text_2)
		),
		SurveyItem(
			context.getString(R.string.survey_options_item_set2_1),
			context.getString(R.string.survey_options_item_set2_2),
			context.getString(R.string.survey_options_item_set2_3),
			context.getString(R.string.survey_options_item_set2_4),
			ContextCompat.getString(context, R.string.survey_side_note_title_3),
			ContextCompat.getString(context, R.string.survey_side_note_text_3)
		),
		SurveyItem(
			context.getString(R.string.survey_options_item_set2_1),
			context.getString(R.string.survey_options_item_set2_2),
			context.getString(R.string.survey_options_item_set2_3),
			context.getString(R.string.survey_options_item_set2_4),
			ContextCompat.getString(context, R.string.survey_side_note_title_4),
			ContextCompat.getString(context, R.string.survey_side_note_text_4)
		),
		SurveyItem(
			context.getString(R.string.survey_options_item_set3_1),
			context.getString(R.string.survey_options_item_set3_2),
			context.getString(R.string.survey_options_item_set3_3),
			context.getString(R.string.survey_options_item_set3_4),
			ContextCompat.getString(context, R.string.survey_side_note_title_5),
			ContextCompat.getString(context, R.string.survey_side_note_text_5)
		),
		SurveyItem(
			context.getString(R.string.survey_options_item_set3_1),
			context.getString(R.string.survey_options_item_set3_2),
			context.getString(R.string.survey_options_item_set3_3),
			context.getString(R.string.survey_options_item_set3_4),
			ContextCompat.getString(context, R.string.survey_side_note_title_6),
			ContextCompat.getString(context, R.string.survey_side_note_text_6)
		),
		SurveyItem(
			context.getString(R.string.survey_options_item_set3_1),
			context.getString(R.string.survey_options_item_set3_2),
			context.getString(R.string.survey_options_item_set3_3),
			context.getString(R.string.survey_options_item_set3_4),
			ContextCompat.getString(context, R.string.survey_side_note_title_7),
			ContextCompat.getString(context, R.string.survey_side_note_text_7)
		),
		SurveyItem(
			context.getString(R.string.survey_options_item_set4_no),
			context.getString(R.string.survey_options_item_set4_yes),
			"",
			"",
			ContextCompat.getString(context, R.string.survey_side_note_title_8),
			ContextCompat.getString(context, R.string.survey_side_note_text_8),
			null,
			true
		),
		SurveyItem(
			context.getString(R.string.survey_options_item_set4_no),
			context.getString(R.string.survey_options_item_set4_yes),
			"",
			"",
			ContextCompat.getString(context, R.string.survey_side_note_title_9),
			ContextCompat.getString(context, R.string.survey_side_note_text_9),
			null,
			true
		)
	)

	private val _uiStateFlow = MutableStateFlow<SurveyUiState>(
		SurveyUiState.OverallGameSkill(
			progressPercent = 0,
			questionsTitlesList[0],
			null,
			optionsList
		)
	)
	val uiStateFlow = _uiStateFlow.asStateFlow()

	fun onOverallGameSkill() {
		val prevState = _uiStateFlow.value
		_uiStateFlow.value = SurveyUiState.OverallGameSkill(
			progressPercent = 0,
			questionsTitlesList[0],
			prevState = prevState,
			optionsList
		)
	}

	fun onForehandLevel() {
		val prevState = _uiStateFlow.value
		_uiStateFlow.value = SurveyUiState.ForehandLevel(
			progressPercent = calculateProgressPercent(1),
			questionsTitlesList[1],
			prevState = prevState
		)
	}

	fun onBackhandLevel() {
		val prevState = _uiStateFlow.value
		_uiStateFlow.value = SurveyUiState.BackhandLevel(
			progressPercent = calculateProgressPercent(2),
			questionsTitlesList[2],
			prevState = prevState
		)
	}

	fun onSliceShotLevel() {
		val prevState = _uiStateFlow.value
		_uiStateFlow.value = SurveyUiState.SliceShotLevel(
			progressPercent = calculateProgressPercent(3),
			questionsTitlesList[3],
			prevState = prevState
		)
	}

	fun onServeLevel() {
		val prevState = _uiStateFlow.value
		_uiStateFlow.value = SurveyUiState.ServeLevel(
			progressPercent = calculateProgressPercent(4),
			questionsTitlesList[4],
			prevState = prevState
		)
	}

	fun onNetGameLevel() {
		val prevState = _uiStateFlow.value
		_uiStateFlow.value = SurveyUiState.NetGameLevel(
			progressPercent = calculateProgressPercent(5),
			questionsTitlesList[5],
			prevState = prevState
		)
	}

	fun onGameSpeedLevel() {
		val prevState = _uiStateFlow.value
		_uiStateFlow.value = SurveyUiState.GameSpeedLevel(
			progressPercent = calculateProgressPercent(6),
			questionsTitlesList[6],
			prevState = prevState
		)
	}

	fun onTournamentParticipation() {
		val prevState = _uiStateFlow.value
		_uiStateFlow.value = SurveyUiState.TournamentParticipation(
			progressPercent = calculateProgressPercent(7),
			questionsTitlesList[7],
			prevState = prevState
		)
	}

	fun onTournamentTopPlaces() {
		val prevState = _uiStateFlow.value
		_uiStateFlow.value = SurveyUiState.TournamentTopPlaces(
			progressPercent = calculateProgressPercent(8),
			questionsTitlesList[8],
			prevState = prevState
		)
	}

	fun onPreviousItem(uiState: SurveyUiState) {
		_uiStateFlow.value = uiState
	}

//	fun onPickedCircledImage(pickedCircledImage: CircledImage) {
//		val newList = imageList.map { item ->
//			if (item.isSelected) { // previously selected
//				return@map item.copy(isSelected = false)
//			} else if (item == pickedCircledImage) {
//				return@map pickedCircledImage.copy(isSelected = true)
//			} else {
//				return@map item
//			}
//		}
//		_uiStateFlow.value = PhotoPickUiState.PickedPreselectedImage(newList, true)
//	}

//	fun onCurrentItemChange(currentItem: Int) {
//		val state = when (repository.optionsList.getOrNull(currentItem) ?: return) { // fixme works
//			// Forehand -> { set state }
//			// Backhand -> { set state }
//		}
//	}

	private fun calculateProgressPercent(position: Int): Int {
		return (100 / questionsTitlesList.size) * position
	}


}