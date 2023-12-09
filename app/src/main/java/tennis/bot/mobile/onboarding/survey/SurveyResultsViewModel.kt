package tennis.bot.mobile.onboarding.survey

import android.content.Context
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import tennis.bot.mobile.R
import tennis.bot.mobile.onboarding.photopick.PhotoPickUiState
import javax.inject.Inject

@HiltViewModel
class SurveyResultsViewModel @Inject constructor(
	private val accountInfo: AccountInfoRepository,
	@ApplicationContext context: Context
): ViewModel() {

	val surveyResultItemList = listOf(
		SurveyResultItem(context.getString(R.string.survey_results_title_1)),
		SurveyResultItem(context.getString(R.string.survey_results_title_2)),
		SurveyResultItem(context.getString(R.string.survey_results_title_3)),
		SurveyResultItem(context.getString(R.string.survey_results_title_4)),
		SurveyResultItem(context.getString(R.string.survey_results_title_5)),
		SurveyResultItem(context.getString(R.string.survey_results_title_6)),
		SurveyResultItem(context.getString(R.string.survey_results_title_7)),
		SurveyResultItem(context.getString(R.string.survey_results_title_8)),
		SurveyResultItem(context.getString(R.string.survey_results_title_9), "", true),
	)

	private val _uiStateFlow = MutableStateFlow<SurveyResultsUiState>(SurveyResultsUiState.InitialWithAnswers(
		answers = surveyResultItemList
	))

	val uiStateFlow = _uiStateFlow.asStateFlow()

	private fun set1AnswersConverter(id: Int): String {
		var stringAnswer = ""
		when(id) {
			1 -> { stringAnswer = "Новичок"}
			2 -> { stringAnswer = "Любитель"}
			3 -> { stringAnswer = "Продвинутый"}
			4 -> { stringAnswer = "Профессионал"}
		}

		return stringAnswer
	}

}