package tennis.bot.mobile.feed.requestcreation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import tennis.bot.mobile.R
import tennis.bot.mobile.onboarding.survey.SurveyResultItem
import tennis.bot.mobile.profile.account.UserProfileAndEnumsRepository
import javax.inject.Inject

@HiltViewModel
class RequestCreationViewModel @Inject constructor(
	private val repository: UserProfileAndEnumsRepository,
	@ApplicationContext private val context: Context
): ViewModel() {

	private val _uiStateFlow = MutableStateFlow(
		RequestCreationUiState(
			emptyList()
			// rate
		)
	)
	val uiStateFlow = _uiStateFlow.asStateFlow()

	var profileRating: Int = 0

	fun onStartup() {
		viewModelScope.launch {
			profileRating = repository.getProfile().rating
			val recommendedValues = "${profileRating - 150} - ${profileRating + 150}"

			val layoutList = listOf(
				SurveyResultItem(context.getString(R.string.district_title), context.getString(R.string.survey_option_null)),
				SurveyResultItem(context.getString(R.string.gametype_title), context.getString(R.string.score_type_single)),
				SurveyResultItem(context.getString(R.string.payment_title), context.getString(R.string.payment_split)),
				SurveyResultItem(context.getString(R.string.date_title), ), // today's date
				SurveyResultItem(context.getString(R.string.time_title), ), // current time
				GaugeAndCommentItem(
					profileRating,
					recommendedValues.substringBefore(" "),
					recommendedValues,
					recommendedValues.substringAfter("- "),
					context.getString(R.string.request_creation_comment,
						recommendedValues,
						context.getString(R.string.payment_split),
						"Хард")
				)
			)

			_uiStateFlow.value = uiStateFlow.value.copy(layoutItemsList = layoutList)
		}
	}


}