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
	@ApplicationContext context: Context,
): ViewModel() {

	private val optionsList = listOf(
		NewRefreshedCoolSurveyItem(
			context.getString(R.string.survey_options_item_set1_1),
			context.getString(R.string.survey_options_item_set1_2),
			context.getString(R.string.survey_options_item_set1_3),
			context.getString(R.string.survey_options_item_set1_4),
			ContextCompat.getString(context, R.string.survey_side_note_title_1),
			ContextCompat.getString(context, R.string.survey_side_note_text_1)
		),
		NewRefreshedCoolSurveyItem(
			context.getString(R.string.survey_options_item_set1_1),
			context.getString(R.string.survey_options_item_set1_2),
			context.getString(R.string.survey_options_item_set1_3),
			context.getString(R.string.survey_options_item_set1_4),
			ContextCompat.getString(context, R.string.survey_side_note_title_2),
			ContextCompat.getString(context, R.string.survey_side_note_text_2)
		),
		NewRefreshedCoolSurveyItem(
			context.getString(R.string.survey_options_item_set2_1),
			context.getString(R.string.survey_options_item_set2_2),
			context.getString(R.string.survey_options_item_set2_3),
			context.getString(R.string.survey_options_item_set2_4),
			ContextCompat.getString(context, R.string.survey_side_note_title_3),
			ContextCompat.getString(context, R.string.survey_side_note_text_3)
		),
		NewRefreshedCoolSurveyItem(
			context.getString(R.string.survey_options_item_set2_1),
			context.getString(R.string.survey_options_item_set2_2),
			context.getString(R.string.survey_options_item_set2_3),
			context.getString(R.string.survey_options_item_set2_4),
			ContextCompat.getString(context, R.string.survey_side_note_title_4),
			ContextCompat.getString(context, R.string.survey_side_note_text_4)
		),
		NewRefreshedCoolSurveyItem(
			context.getString(R.string.survey_options_item_set3_1),
			context.getString(R.string.survey_options_item_set3_2),
			context.getString(R.string.survey_options_item_set3_3),
			context.getString(R.string.survey_options_item_set3_4),
			ContextCompat.getString(context, R.string.survey_side_note_title_5),
			ContextCompat.getString(context, R.string.survey_side_note_text_5)
		),
		NewRefreshedCoolSurveyItem(
			context.getString(R.string.survey_options_item_set3_1),
			context.getString(R.string.survey_options_item_set3_2),
			context.getString(R.string.survey_options_item_set3_3),
			context.getString(R.string.survey_options_item_set3_4),
			ContextCompat.getString(context, R.string.survey_side_note_title_6),
			ContextCompat.getString(context, R.string.survey_side_note_text_6)
		),
		NewRefreshedCoolSurveyItem(
			context.getString(R.string.survey_options_item_set3_1),
			context.getString(R.string.survey_options_item_set3_2),
			context.getString(R.string.survey_options_item_set3_3),
			context.getString(R.string.survey_options_item_set3_4),
			ContextCompat.getString(context, R.string.survey_side_note_title_7),
			ContextCompat.getString(context, R.string.survey_side_note_text_7)
		),
		NewRefreshedCoolSurveyItem(
			context.getString(R.string.survey_options_item_set4_no),
			context.getString(R.string.survey_options_item_set4_yes),
			"",
			"",
			ContextCompat.getString(context, R.string.survey_side_note_title_8),
			ContextCompat.getString(context, R.string.survey_side_note_text_8),
			true
		),
		NewRefreshedCoolSurveyItem(
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
	val newRefreshedCoolSurveyUiState = MutableStateFlow(NewRefreshedCoolSurveyUiState(
		progress = 0,
		title = questionsTitlesList[0],
		selectedPage = 0,
		surveyPages = optionsList
	))

	fun onBackClicked() {
		newRefreshedCoolSurveyUiState.value = newRefreshedCoolSurveyUiState.value.copy(
			selectedPage = newRefreshedCoolSurveyUiState.value.selectedPage - 1 // todo fixme для нулевого возвращаемся домой на предыдущий экран
		)
	}

	fun onPickedOption(pickedOptionId: Int) {
		val currentState: NewRefreshedCoolSurveyUiState = newRefreshedCoolSurveyUiState.value
		val currentPage = currentState.surveyPages[currentState.selectedPage]
		val updatePage = currentPage.copy(pickedOptionId = pickedOptionId)
		val newPageIndex = currentState.selectedPage + 1 // fixme тут крэш почини, тут для последнего вперед идем
		val newProgress = calculateProgressPercent(newPageIndex)
		val newTitle = questionsTitlesList[newPageIndex]

		newRefreshedCoolSurveyUiState.value = currentState.copy(
			progress = newProgress,
			title = newTitle,
			selectedPage = newPageIndex,
			surveyPages = currentState.surveyPages.toMutableList().apply {
				set(currentState.selectedPage, updatePage)
			}
		)
	}

	private fun calculateProgressPercent(position: Int): Int {
		return (100 / questionsTitlesList.size) * position
	}
}