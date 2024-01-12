package tennis.bot.mobile.onboarding.account

import tennis.bot.mobile.core.CoreUtilsItem
import tennis.bot.mobile.onboarding.survey.SurveyResultItem

sealed class AccountPageUiState {
	object Loading: AccountPageUiState()
	data class ProfileDataReceived(
		val receivedDataItems: List<CoreUtilsItem>,
		val gameDataList: List<SurveyResultItem>,
		val contactsList: List<SurveyResultItem>
	): AccountPageUiState()
	object Error: AccountPageUiState()
}