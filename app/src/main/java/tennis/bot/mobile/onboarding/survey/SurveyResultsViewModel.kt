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
import tennis.bot.mobile.onboarding.password.PasswordUiState
import tennis.bot.mobile.utils.showToast
import javax.inject.Inject

@HiltViewModel
class SurveyResultsViewModel @Inject constructor(
	private val accountInfo: AccountInfoRepository,
	@ApplicationContext private val context: Context
) : ViewModel() {

	private val surveyResultItemList = listOf(
		SurveyResultItem(context.getString(R.string.survey_results_title_1), accountInfo.surveyAnswers[0]),
		SurveyResultItem(context.getString(R.string.survey_results_title_2), accountInfo.surveyAnswers[1]),
		SurveyResultItem(context.getString(R.string.survey_results_title_3), accountInfo.surveyAnswers[2]),
		SurveyResultItem(context.getString(R.string.survey_results_title_4), accountInfo.surveyAnswers[3]),
		SurveyResultItem(context.getString(R.string.survey_results_title_5), accountInfo.surveyAnswers[4]),
		SurveyResultItem(context.getString(R.string.survey_results_title_6), accountInfo.surveyAnswers[5]),
		SurveyResultItem(context.getString(R.string.survey_results_title_7), accountInfo.surveyAnswers[6]),
		SurveyResultItem(context.getString(R.string.survey_results_title_8), accountInfo.surveyAnswers[7]),
		SurveyResultItem(context.getString(R.string.survey_results_title_9), accountInfo.surveyAnswers[8], true),
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
				if (!accountInfo.postNewPlayer())
					throw IllegalArgumentException("Failed to post NewPlayer")
			}.onFailure {
				onError()
			}.onSuccess {
				navigationCallback.invoke()
			}
		}
	}




}