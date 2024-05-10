package tennis.bot.mobile.feed.requestcreation

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import tennis.bot.mobile.R
import tennis.bot.mobile.feed.insertscore.InsertScoreMediaItem
import tennis.bot.mobile.onboarding.survey.SurveyResultItem
import tennis.bot.mobile.profile.account.UserProfileAndEnumsRepository
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class RequestCreationViewModel @Inject constructor(
	private val repository: UserProfileAndEnumsRepository,
	@ApplicationContext private val context: Context
): ViewModel() {

	private val _uiStateFlow = MutableStateFlow(
		RequestCreationUiState(
			emptyList()
		)
	)
	val uiStateFlow = _uiStateFlow.asStateFlow()

	fun onStartup() {
		viewModelScope.launch (Dispatchers.IO) {
			val profileRating = repository.getProfile().rating
			val recommendedValues = "${profileRating - 150} - ${profileRating + 150}"
			val dateTime = getCurrentDateAndTime()

			val layoutList = listOf(
				SurveyResultItem(context.getString(R.string.district_title), context.getString(R.string.survey_option_null)),
				SurveyResultItem(context.getString(R.string.gametype_title), context.getString(R.string.score_type_single)),
				SurveyResultItem(context.getString(R.string.payment_title), context.getString(R.string.payment_split)),
				SurveyResultItem(context.getString(R.string.date_title), dateTime.first), // today's date
				SurveyResultItem(context.getString(R.string.time_title), dateTime.second), // current time
				GaugeAndCommentItem(
					profileRating,
					context.getString(R.string.request_lower_values, recommendedValues.substringBefore(" ")),
					context.getString(R.string.request_recommended_values, recommendedValues),
					context.getString(R.string.request_higher_values, recommendedValues.substringAfter("- ")),
					context.getString(R.string.request_creation_comment,
						recommendedValues,
						context.getString(R.string.payment_split),
						"Хард") // кандидат на удаление (покрытие не выставляем)
				)
			)

			_uiStateFlow.value = uiStateFlow.value.copy(layoutItemsList = layoutList)
		}
	}
	fun onValueChanged(title: String, value: String) {
		val list = uiStateFlow.value.layoutItemsList
		val item = list.find { (it as? SurveyResultItem)?.resultTitle == title }
		item?.let {
			val itemIndex = list.indexOf(it)
			val updatedList = list.mapIndexed { index, item ->
				if (index == itemIndex) item.let{ (item as SurveyResultItem).copy(resultOption = value) } else item
			}
			_uiStateFlow.value = uiStateFlow.value.copy(layoutItemsList = updatedList)
		}
	}

	fun onDatePicked(date: String) {
		onValueChanged(context.getString(R.string.date_title), date)
	}

	fun onTimePicked(time:String) {
		onValueChanged(context.getString(R.string.time_title), time)
	}


	private fun getCurrentDateAndTime(): Pair<String, String> {
		val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
		val dateTime = dateFormat.format(Calendar.getInstance().time)
		return Pair(dateTime.substringBefore(" "), dateTime.substringAfter(" "))
	}
}