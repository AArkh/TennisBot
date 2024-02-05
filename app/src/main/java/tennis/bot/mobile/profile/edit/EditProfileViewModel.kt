package tennis.bot.mobile.profile.edit

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tennis.bot.mobile.R
import tennis.bot.mobile.onboarding.location.LocationDataMapper
import tennis.bot.mobile.onboarding.location.LocationRepository
import tennis.bot.mobile.profile.account.UserProfileAndEnumsRepository
import tennis.bot.mobile.utils.DEFAULT_DATE_TIME
import tennis.bot.mobile.utils.convertDateAndTime
import tennis.bot.mobile.utils.convertLocationIntToString
import tennis.bot.mobile.utils.convertLocationStringToInt
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
	private val userProfileRepo: UserProfileAndEnumsRepository,
	private val locationRepo: LocationRepository,
	private val locationDataMapper: LocationDataMapper,
	private val editProfileRepository: EditProfileRepository,
	@ApplicationContext private val context: Context
) : ViewModel() {

	companion object {
		const val NAME_SURNAME_REQUEST_KEY = "NAME_SURNAME_REQUEST_KEY"
		const val SELECTED_NAME_SURNAME = "SELECTED_NAME_SURNAME"
		const val BIRTHDAY_REQUEST_KEY = "BIRTHDAY_REQUEST_KEY"
		const val SELECTED_BIRTHDAY = "SELECTED_BIRTHDAY"
		const val LOCATION_STRINGS_REQUEST_KEY = "LOCATION_STRINGS_REQUEST_KEY"
		const val SELECTED_LOCATION_STRINGS = "SELECTED_LOCATION_STRINGS"
		const val PHONE_NUMBER_REQUEST_KEY = "PHONE_NUMBER_REQUEST_KEY"
		const val SELECTED_PHONE_NUMBER = "SELECTED_PHONE_NUMBER"
		const val TELEGRAM_REQUEST_KEY = "TELEGRAM_REQUEST_KEY"
		const val SELECTED_TELEGRAM = "SELECTED_TELEGRAM"
		const val DEFAULT_COUNTRY = "Страна"
		const val DEFAULT_CITY = "Город"
		const val DEFAULT_DISTRICT = "Район"
	}

	private val _uiStateFlow = MutableStateFlow(
		EditProfileUiState(
			profilePicture = "",
			emptyList()
		)
	)
	val uiStateFlow = _uiStateFlow.asStateFlow()

	private val editProfileItems = listOf(
		EditProfileItem(icon = R.drawable.user, title = null),
		EditProfileItem(icon = R.drawable.birthday_cake, title = null),
		EditProfileItem(icon = R.drawable.location, title = null),
		EditProfileItem(icon = R.drawable.telephone, title = null),
		EditProfileItem(icon = R.drawable.telegram, title = null),
	)
	private val calendarDateAndTimeFormat = "dd/MM/yy"
	private val networkDateAndTimeFormat = "yyyy-MM-dd'T'hh:mm:ss'Z'"

	fun onStartup() {
		viewModelScope.launch {
			withContext(Dispatchers.IO) {
				val currentProfileData = userProfileRepo.getProfile()
				val locations = locationRepo.getLocations()
				val country = locationDataMapper.findCountryFromCity(locations, currentProfileData.cityId)
				val city = locationDataMapper.findCityString(locations, currentProfileData.cityId)
				val location = if (currentProfileData.districtId == null) { "$country, $city" } else { "$country, $city(${currentProfileData.districtId})" }

				val receivedDataList = listOf(
					currentProfileData.name,
					convertDateAndTime(currentProfileData.birthday ?: "") ?: context.getString(R.string.survey_option_null),
					location,
					userProfileRepo.getPhoneNumber() ?: context.getString(R.string.survey_option_null),
					currentProfileData.telegram ?: context.getString(R.string.survey_option_null)
				)

				val categoriesDataList = editProfileItems.mapIndexed { index, editProfileItem ->
					if (index == editProfileItems.lastIndex) {
						editProfileItem.copy(
							title = receivedDataList.getOrElse(index) { editProfileItem.title },
							noUnderline = true
						)
					} else {
						editProfileItem.copy(title = receivedDataList.getOrElse(index) { editProfileItem.title })
					}
				}

				_uiStateFlow.value = EditProfileUiState(
					profilePicture = currentProfileData.photo,
					categoriesList = categoriesDataList
				)
			}
		}
	}

	fun onUpdatedValues(key: Int, value: String) {
		viewModelScope.launch {
			when(key) {
				EditProfileAdapter.CHANGE_NAME_INDEX -> {
					kotlin.runCatching {
					withContext(Dispatchers.IO) {
						val nameSurname = value.split(" ")
						editProfileRepository.putNameSurname(nameSurname[0], nameSurname[1])
					}  }.onFailure {
						Log.d("123456", "Name - something went wrong")
					}.onSuccess {
						userProfileRepo.updateCachedProfile("name", value)
						Log.d("123456", "Name - success")
					}
				}
				EditProfileAdapter.CHANGE_BIRTHDAY_INDEX -> {
					withContext(Dispatchers.IO) {

						val networkDateTime = convertDateAndTimeToNetwork(value) // wrong format - ask and check which is the right one

						editProfileRepository.putBirthday(networkDateTime ?: "")
					}
				}
				EditProfileAdapter.CHANGE_LOCATION_INDEX -> {}
				EditProfileAdapter.CHANGE_PHONE_INDEX -> {}
				EditProfileAdapter.CHANGE_TELEGRAM_INDEX -> {}
			}
		}
	}

	private fun convertDateAndTimeToNetwork(dateTime: String): String? {
		if (dateTime == DEFAULT_DATE_TIME) return null

		val dateTimeFormatter = SimpleDateFormat(calendarDateAndTimeFormat, Locale.getDefault())
		val timeStampMs = dateTimeFormatter.parse(dateTime)
		val someOtherFormatter = SimpleDateFormat(networkDateAndTimeFormat, Locale("ru", "RU"))
		return someOtherFormatter.format(timeStampMs) ?: ""

	}

	fun updateLocation(countryString: String?, cityString: String?, districtString: String?) {
		onUpdatedValues(
			EditProfileAdapter.CHANGE_LOCATION_INDEX,
			formLocationToFragmentResult(countryString, cityString, districtString) ?: context.getString(R.string.survey_option_null)
		)
	}

	private fun formLocationToFragmentResult(countryString: String?, cityString: String?, districtString: String?): String? {
		val result =
			if (countryString != DEFAULT_COUNTRY && cityString != DEFAULT_CITY && districtString != DEFAULT_DISTRICT) {
				"$countryString, $cityString($districtString)"
			} else if (countryString != DEFAULT_COUNTRY && cityString != DEFAULT_CITY) {
				"$countryString, $cityString"
			} else if (countryString != DEFAULT_COUNTRY) countryString else context.getString(R.string.survey_option_null)

		return result
	}
}
