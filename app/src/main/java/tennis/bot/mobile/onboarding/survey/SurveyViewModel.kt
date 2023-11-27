package tennis.bot.mobile.onboarding.survey

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SurveyViewModel @Inject constructor(
	private val repository: AccountInfoRepository
): ViewModel() {

}