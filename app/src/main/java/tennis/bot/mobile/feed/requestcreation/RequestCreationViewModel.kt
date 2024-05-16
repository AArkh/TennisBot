package tennis.bot.mobile.feed.requestcreation

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.text.SpannableStringBuilder
import android.widget.EditText
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import tennis.bot.mobile.R
import tennis.bot.mobile.onboarding.location.LocationDataMapper
import tennis.bot.mobile.onboarding.location.LocationDialogFragment
import tennis.bot.mobile.onboarding.location.LocationFragment
import tennis.bot.mobile.onboarding.location.LocationRepository
import tennis.bot.mobile.onboarding.survey.SurveyResultItem
import tennis.bot.mobile.profile.account.UserProfileAndEnumsRepository
import tennis.bot.mobile.utils.showToast
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class RequestCreationViewModel @Inject constructor(
	private val repository: UserProfileAndEnumsRepository,
	private val locationRepository: LocationRepository,
	private val locationDataMapper: LocationDataMapper,
	@ApplicationContext private val context: Context
): ViewModel() {

	private val _uiStateFlow = MutableStateFlow(
		RequestCreationUiState(
			emptyList()
		)
	)
	val uiStateFlow = _uiStateFlow.asStateFlow()
	private var currentRating: Int = 0
	private var currentGamePay: String = context.getString(R.string.payment_split)

	fun onStartup() {
		viewModelScope.launch (Dispatchers.IO) {
			val profileRating = repository.getProfile().rating
			val recommendedValues = "${profileRating - 150} - ${profileRating + 150}"
			val dateTime = getCurrentDateAndTime()

			val layoutList = listOf(
				SurveyResultItem(context.getString(R.string.district_title), context.getString(R.string.survey_option_null)),
				SurveyResultItem(context.getString(R.string.gametype_title), context.getString(R.string.score_type_single)),
				SurveyResultItem(context.getString(R.string.payment_title), currentGamePay),
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
		if (title == context.getString(R.string.payment_title)) { //todo узнать у Андрея насколько это норм вариант так сделать
			currentGamePay = value
		}

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

	fun onDistrictPressed(childFragmentManager: FragmentManager) {
		viewModelScope.launch (Dispatchers.IO) {
			val locations = locationRepository.getLocations()
			val profileCityId = repository.getProfile().cityId
			val cityString = locationDataMapper.findCityString(locations, profileCityId)
			val countryString = locationDataMapper.findCountryFromCity(locations, profileCityId)
			val hasDistricts = locations.find { it.countryName == countryString }
				?.cities!!.find { it.name == cityString }
				?.districts!!.isNotEmpty()

			if (hasDistricts) {
				val bottomSheet = LocationDialogFragment()
				bottomSheet.arguments = bundleOf(
					LocationFragment.SELECT_ACTION_KEY to LocationFragment.SELECT_DISTRICT,
					LocationFragment.SELECTED_COUNTRY_NAME_KEY to countryString,
					LocationFragment.SELECTED_CITY_NAME_KEY to cityString,
				)
				bottomSheet.show(childFragmentManager, bottomSheet.tag)
			} else {
				context.showToast("В этом городе округа не поддерживаются")
			}
		}
	}

	fun onDistrictPicked(district: String) {
		onValueChanged(context.getString(R.string.district_title), district)
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

	fun updateRating(commentItem: EditText, rating: Int) {
		currentRating = rating
		val recommendedValues = "${currentRating - 150} - ${currentRating + 150}"
		commentItem.text = SpannableStringBuilder.valueOf(
			context.getString(R.string.request_creation_comment,
			recommendedValues,
			currentGamePay,
			"Хард")
		)
	}
}