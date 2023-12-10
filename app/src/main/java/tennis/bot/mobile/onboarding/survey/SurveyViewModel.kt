package tennis.bot.mobile.onboarding.survey

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import tennis.bot.mobile.R
import javax.inject.Inject

@HiltViewModel
class SurveyViewModel @Inject constructor(
	private val accountInfo: AccountInfoRepository,
	@ApplicationContext context: Context,
): ViewModel() {

	private val optionsList = listOf(
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
			true
		),
		SurveyItem(
			context.getString(R.string.survey_options_item_set4_no),
			context.getString(R.string.survey_options_item_set4_yes),
			"",
			"",
			ContextCompat.getString(context, R.string.survey_side_note_title_9),
			ContextCompat.getString(context, R.string.survey_side_note_text_9),
			true
		)
	)
	private val questionsTitlesList = listOf(
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

	val surveyUiState = MutableStateFlow(SurveyUiState(
		progress = 0,
		title = questionsTitlesList[0],
		selectedPage = 0,
		surveyPages = optionsList
	))

	fun onBackClicked() {
		if (surveyUiState.value.selectedPage in 1..8 ) {
			val newPageIndex = surveyUiState.value.selectedPage - 1
			val newProgress = calculateProgressPercent(newPageIndex)
			val newTitle = questionsTitlesList[newPageIndex]
			surveyUiState.value = surveyUiState.value.copy(
				progress = newProgress,
				title = newTitle,
				selectedPage = newPageIndex,
			)
		}
		return
	}

	fun onPickedOption(pickedOptionId: Int, pickedOptionTitle: String) {
		val currentState: SurveyUiState = surveyUiState.value
		val currentPage = currentState.surveyPages[currentState.selectedPage]
		val updatePage = currentPage.copy(pickedOptionId = pickedOptionId)
		val newPageIndex = if (currentState.selectedPage in 0..7) {
			currentState.selectedPage + 1
		} else return
		val newProgress = calculateProgressPercent(newPageIndex)
		val newTitle = questionsTitlesList[newPageIndex]
		recordEntry(currentState.selectedPage, pickedOptionId, pickedOptionTitle)

		surveyUiState.value = currentState.copy(
			progress = newProgress,
			title = newTitle,
			selectedPage = newPageIndex,
			surveyPages = currentState.surveyPages.toMutableList().apply {
				set(currentState.selectedPage, updatePage)
			}
		)
	}

	fun onLastPickedOption(pickedOptionId: Int, pickedOptionTitle: String) {
		val currentState: SurveyUiState = surveyUiState.value
		val currentPage = currentState.surveyPages[currentState.selectedPage]
		val updatePage = currentPage.copy(pickedOptionId = pickedOptionId)
		recordEntry(currentState.selectedPage, pickedOptionId, pickedOptionTitle)
		accountInfo.updateSurveyData()

		surveyUiState.value = currentState.copy(
			surveyPages = currentState.surveyPages.toMutableList().apply {
				set(currentState.selectedPage, updatePage)
			}
		)
	}

	private fun recordEntry(position: Int, id: Int, answer: String) {
		accountInfo.rawSurveyAnswers.add(position, id)
		accountInfo.surveyAnswers.add(position, answer)
	}

	private fun calculateProgressPercent(position: Int): Int {
		return (100 / questionsTitlesList.size) * position
	}
}