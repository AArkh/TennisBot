package tennis.bot.mobile.feed.requestcreation

import android.app.DatePickerDialog
import android.content.Context
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.icu.util.TimeZone
import android.text.SpannableStringBuilder
import android.util.Log
import android.widget.DatePicker
import android.widget.EditText
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import com.wdullaer.materialdatetimepicker.time.Timepoint
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
	private val repository: RequestCreationRepository,
	private val userProfileAndEnumsRepository: UserProfileAndEnumsRepository,
	private val locationRepository: LocationRepository,
	private val locationDataMapper: LocationDataMapper,
	@ApplicationContext private val context: Context
): ViewModel() {

	private val _uiStateFlow = MutableStateFlow(
		RequestCreationUiState(
			layoutItemsList = emptyList(),
			isLoading = false,
			isCreateButtonActive = false
		)
	)
	val uiStateFlow = _uiStateFlow.asStateFlow()
	private var currentRating: Int = 0
	private var currentGamePay: String = context.getString(R.string.payment_split)
	private var currentGamePayId: Int = 1
	private var currentGameTypeId: Int = 1

	fun onStartup() {
		viewModelScope.launch (Dispatchers.IO) {
			val profileRating = userProfileAndEnumsRepository.getProfile().rating
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
	fun onValueChanged(title: String, value: String, valueId: Int) {
		if (title == context.getString(R.string.payment_title)) { //todo узнать у Андрея насколько это норм вариант так сделать. После последних изменений выглядит совсем грустно
			currentGamePay = value
			currentGamePayId = valueId
		} else if (title == context.getString(R.string.gametype_title)) {
			currentGameTypeId = valueId
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
			val profileCityId = userProfileAndEnumsRepository.getProfile().cityId
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

	fun showDatePickerDialog(context: Context): DatePickerDialog {
		val calendar = Calendar.getInstance()
		val currentYear = calendar.get(Calendar.YEAR)
		val currentMonth = calendar.get(Calendar.MONTH)
		val currentDay = calendar.get(Calendar.DAY_OF_MONTH)

		val datePickerDialog = DatePickerDialog(
			context,
			{ _: DatePicker?, year: Int, month: Int, dayOfMonth: Int ->
				val selectedDate = "${String.format("%02d", dayOfMonth)}.${String.format("%02d", month + 1)}.$year"
				Log.d("123456", "Selected Date: $selectedDate")
				onDatePicked(selectedDate)
			},
			currentYear,
			currentMonth,
			currentDay
		)
		datePickerDialog.datePicker.minDate = System.currentTimeMillis()
		return datePickerDialog
	}

	fun showTimePickerDialog(): TimePickerDialog {
		val calendar = Calendar.getInstance()
		val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
		val currentMinute = calendar.get(Calendar.MINUTE)
		val currentDate = "${String.format("%02d", calendar.get(Calendar.DAY_OF_MONTH))}.${String.format("%02d", calendar.get(Calendar.MONTH) + 1)}.${calendar.get(Calendar.YEAR)}"
		val pickedDate = (uiStateFlow.value.layoutItemsList[3] as SurveyResultItem).resultOption

		val timePickerDialog = TimePickerDialog.newInstance({ _, selectedHour, roundedMinute, _ ->
			onTimePicked(String.format("%02d:%02d", selectedHour, roundedMinute))
		}, currentHour, currentMinute, true)

		if (pickedDate == currentDate) {
			timePickerDialog.setMinTime(Timepoint(currentHour, currentMinute))
		}
		timePickerDialog.setTimeInterval(1, 30)
		return timePickerDialog
	}

	fun onDistrictPicked(district: String) {
		onValueChanged(context.getString(R.string.district_title), district, 0)
	}

	private fun onDatePicked(date: String) {
		onValueChanged(context.getString(R.string.date_title), date, 0)
	}

	private fun onTimePicked(time:String) {
		onValueChanged(context.getString(R.string.time_title), time, 0)
		_uiStateFlow.value = uiStateFlow.value.copy(isCreateButtonActive = true)
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

	private fun showLoading() {
		val currentState = _uiStateFlow.value
		_uiStateFlow.value = currentState.copy(
			isLoading = true
		)
	}

	private fun onStopLoading() {
		val currentState = _uiStateFlow.value
		_uiStateFlow.value = currentState.copy(
			isLoading = false
		)
	}

	private fun formatDateAndTimeForRequest(date: String, time: String): String { // fixme
		val dateTimeString = "$date $time"
		val dateTimeFormat = SimpleDateFormat("dd.MM.yyyy HH:mm")
		val dateTime = dateTimeFormat.parse(dateTimeString)

		val isoDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
		isoDateFormat.timeZone = TimeZone.getTimeZone("UTC")

		return isoDateFormat.format(dateTime)
	}

	fun onCreateButtonPressed(navigationCallback: () -> Unit) {
		viewModelScope.launch(Dispatchers.IO) {
			kotlin.runCatching {
				showLoading()
				val layoutList = uiStateFlow.value.layoutItemsList
				val locations = locationRepository.getLocations()
				val profileCityId = userProfileAndEnumsRepository.getProfile().cityId
				val district = locationDataMapper.findDistrictIntFromString(locations, profileCityId, (layoutList[0] as SurveyResultItem).resultOption)
				val date = formatDateAndTimeForRequest((layoutList[3] as SurveyResultItem).resultOption, (layoutList[4] as SurveyResultItem).resultOption)
				val recommendedValues = "${currentRating - 150} - ${currentRating + 150}"

				repository.postAddRequest(RequestNetwork(
					cityId = profileCityId,
					districtId = district,
					date = date,
					gameType = currentGameTypeId,
					paymentTypeId = currentGamePayId,
					comment = context.getString(R.string.request_creation_comment,
						recommendedValues,
						currentGamePay,
						"Хард") // кандидат на удаление (покрытие не выставляем),
				))


			}.onFailure {
				onStopLoading()
				context.showToast(context.getString(R.string.error_no_network_message))
			}.onSuccess {
				onStopLoading()
				navigationCallback.invoke()
			}
		}
	}
}