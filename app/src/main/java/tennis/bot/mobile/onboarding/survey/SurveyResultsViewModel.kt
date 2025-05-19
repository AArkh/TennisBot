package tennis.bot.mobile.onboarding.survey

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import tennis.bot.mobile.R
import tennis.bot.mobile.utils.showToast
import javax.inject.Inject

@HiltViewModel
class SurveyResultsViewModel @Inject constructor(
	private val onboardingRepository: OnboardingRepository,
	@ApplicationContext private val context: Context
) : ViewModel() {

	private val surveyResultItemList = listOf(
		SurveyResultItem(context.getString(R.string.survey_results_title_1), onboardingRepository.surveyAnswers[0]),
		SurveyResultItem(context.getString(R.string.survey_results_title_2), onboardingRepository.surveyAnswers[1]),
		SurveyResultItem(context.getString(R.string.survey_results_title_3), onboardingRepository.surveyAnswers[2]),
		SurveyResultItem(context.getString(R.string.survey_results_title_4), onboardingRepository.surveyAnswers[3]),
		SurveyResultItem(context.getString(R.string.survey_results_title_5), onboardingRepository.surveyAnswers[4]),
		SurveyResultItem(context.getString(R.string.survey_results_title_6), onboardingRepository.surveyAnswers[5]),
		SurveyResultItem(context.getString(R.string.survey_results_title_7), onboardingRepository.surveyAnswers[6]),
		SurveyResultItem(context.getString(R.string.survey_results_title_8), onboardingRepository.surveyAnswers[7]),
		SurveyResultItem(context.getString(R.string.survey_results_title_9), onboardingRepository.surveyAnswers[8], true),
	)
	private val buttonContinueText = context.getString(R.string.button_continue)


	private val _uiStateFlow = MutableStateFlow<SurveyResultsUiState>(
		SurveyResultsUiState.InitialWithAnswers(
			answers = surveyResultItemList,
			buttonContinueText = buttonContinueText
		)
	)

	val uiStateFlow = _uiStateFlow.asStateFlow()

	private fun showLoading() {
		_uiStateFlow.value = SurveyResultsUiState.Loading
	}

	private fun onError() {
		context.showToast("${context.getString(R.string.error_text)}. Попробуйте еще раз")

		_uiStateFlow.value = SurveyResultsUiState.InitialWithAnswers(
			answers = surveyResultItemList,
			buttonContinueText = buttonContinueText
		)
	}

	fun onContinueButtonClicked(navigationCallback: () -> Unit) {
		showLoading()
		viewModelScope.launch(Dispatchers.IO) {
			kotlin.runCatching {
				if (!onboardingRepository.postNewPlayer())
					throw IllegalArgumentException("Failed to post NewPlayer")
			}.onFailure {
				onError()
			}.onSuccess {
				navigationCallback.invoke()
			}
		}
	}
}