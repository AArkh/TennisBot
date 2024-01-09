package tennis.bot.mobile.onboarding.account

import tennis.bot.mobile.core.CoreUtilsItem
import tennis.bot.mobile.onboarding.survey.SurveyResultItem

sealed class AccountPageUiState {
	data class Loading(
		val isLoading: Boolean
	): AccountPageUiState()
	data class ProfileDataReceived(
		val receivedDataItems: List<CoreUtilsItem>,
		val gameDataList: List<SurveyResultItem>,
		val contactsList: List<SurveyResultItem>
	): AccountPageUiState()
	data class Error(
		val isError: Boolean
	): AccountPageUiState()
}