package tennis.bot.mobile.profile.editprofile

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.icu.util.TimeZone
import android.net.Uri
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
import tennis.bot.mobile.onboarding.survey.OnboardingRepository
import tennis.bot.mobile.profile.account.AccountPageAdapter.Companion.NULL_STRING
import tennis.bot.mobile.profile.account.UserProfileAndEnumsRepository
import tennis.bot.mobile.utils.DEFAULT_DATE_TIME
import tennis.bot.mobile.utils.convertDateAndTime
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
	private val userProfileRepo: UserProfileAndEnumsRepository,
	private val locationRepo: LocationRepository,
	private val locationDataMapper: LocationDataMapper,
	private val onboardingRepository: OnboardingRepository,
	@ApplicationContext private val context: Context
) : ViewModel() {

	companion object {
		const val NAME_SURNAME_REQUEST_KEY = "NAME_SURNAME_REQUEST_KEY"
		const val SELECTED_NAME_SURNAME = "SELECTED_NAME_SURNAME"
		const val LOCATION_STRINGS_REQUEST_KEY = "LOCATION_STRINGS_REQUEST_KEY"
		const val SELECTED_LOCATION_STRINGS = "SELECTED_LOCATION_STRINGS"
		const val PHONE_NUMBER_REQUEST_KEY = "PHONE_NUMBER_REQUEST_KEY"
		const val SELECTED_PHONE_NUMBER = "SELECTED_PHONE_NUMBER"
		const val TELEGRAM_REQUEST_KEY = "TELEGRAM_REQUEST_KEY"
		const val SELECTED_TELEGRAM = "SELECTED_TELEGRAM"
		const val DEFAULT_COUNTRY = "Страна"
		const val DEFAULT_CITY = "Город"
		const val DEFAULT_DISTRICT = "Район"
		const val NAME_SURNAME_KEY = "name"
		const val BIRTHDAY_KEY = "birthday"
		const val CITY_KEY = "cityId"
		const val DISTRICT_KEY = "districtId"
		const val PHONE_NUMBER_KEY = "phoneNumber"
		const val TELEGRAM_KEY = "telegram"
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
				val district = if (currentProfileData.districtId != null) {
					locationDataMapper.findDistrictFromCity(locations, currentProfileData.cityId, currentProfileData.districtId)
				} else {
					null
				}
				val location = if (currentProfileData.districtId == null) {
					"$country, $city"
				} else {
					"$country, $city(${district})"
				}

				val receivedDataList = listOf(
					currentProfileData.name,
					convertDateAndTime(currentProfileData.birthday ?: "") ?: context.getString(R.string.survey_option_null),
					location,
					userProfileRepo.getPhoneNumber() ?: context.getString(R.string.survey_option_null),
					if (currentProfileData.telegram != NULL_STRING) currentProfileData.telegram else context.getString(R.string.survey_option_null)
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
			when (key) {
				EditProfileAdapter.CHANGE_NAME_INDEX -> { updateNameSurname(value) }
				EditProfileAdapter.CHANGE_BIRTHDAY_INDEX -> { updateBirthday(value) }
				EditProfileAdapter.CHANGE_LOCATION_INDEX -> { updateLocation(value) }
				EditProfileAdapter.CHANGE_PHONE_INDEX -> { updatePhoneNumber(value) }
				EditProfileAdapter.CHANGE_TELEGRAM_INDEX -> { updateTelegram(value) }
			}
			onStartup()
		}
	}

	private suspend fun updateTelegram(value: String) {
		kotlin.runCatching {
			withContext(Dispatchers.IO) {
				userProfileRepo.putTelegramNetwork(value)
			}
		}.onFailure {
			Log.d("123456", "CHANGE_TELEGRAM_INDEX - failure")
		}.onSuccess {
			userProfileRepo.updateCachedProfile(TELEGRAM_KEY, value)
			Log.d("123456", "CHANGE_TELEGRAM_INDEX - success")
		}
	}

	private suspend fun updatePhoneNumber(value: String) {
		kotlin.runCatching {
			withContext(Dispatchers.IO) {
				userProfileRepo.putPhoneNumber(value)
			}
		}.onFailure {
			Log.d("123456", "CHANGE_PHONE_INDEX - failure")
		}.onSuccess {
			userProfileRepo.updateCachedProfile(PHONE_NUMBER_KEY, value)
			Log.d("123456", "CHANGE_PHONE_INDEX - success")
		}
	}

	private suspend fun updateLocation(value: String) {
		kotlin.runCatching {
			withContext(Dispatchers.IO) {
				val cityString = value.split(", ")[1]
				val districtString = extractDistrictFromParentheses(value)
				val locations = locationRepo.getLocations()
				val cityInt = locationDataMapper.findCityIntFromString(locations, cityString)
				val districtInt = locationDataMapper.findDistrictIntFromString(locations, cityInt, districtString)
				if (cityInt != null) {
					userProfileRepo.putLocation(cityInt, districtInt)
					userProfileRepo.updateCachedProfile(CITY_KEY, cityInt.toString())
					userProfileRepo.updateCachedProfile(DISTRICT_KEY, districtInt.toString())
					Log.d("123456", "LocationNetwork - success")
				}
			}
		}.onFailure {
			Log.d("123456", "CHANGE_LOCATION_INDEX - failure")
		}.onSuccess {
			Log.d("123456", "CHANGE_LOCATION_INDEX - success")
		}
	}

	private suspend fun updateBirthday(value: String) {
		kotlin.runCatching {
			withContext(Dispatchers.IO) {
				val networkDateTime =
					convertDateAndTimeToNetwork(value)

				userProfileRepo.putBirthday(networkDateTime ?: "")
			}
		}.onFailure {
			Log.d("123456", "BirthdayNetwork - failure")
		}.onSuccess {
			userProfileRepo.updateCachedProfile(BIRTHDAY_KEY, value)
			Log.d("123456", "BirthdayNetwork - success")
		}
	}

	private suspend fun updateNameSurname(value: String) {
		kotlin.runCatching {
			withContext(Dispatchers.IO) {
				val nameSurname = value.split(" ")
				userProfileRepo.putNameSurname(nameSurname[0], nameSurname[1])
			}
		}.onFailure {
			Log.d("123456", "Name - something went wrong")
		}.onSuccess {
			userProfileRepo.updateCachedProfile(NAME_SURNAME_KEY, value)
			Log.d("123456", "Name - success")
		}
	}

	fun onPickedProfilePic(uri: Uri) {
		onboardingRepository.recordUserPickedPictureUri(uri.toString())
		viewModelScope.launch(Dispatchers.IO) {
			onboardingRepository.postProfilePicture(userProfileRepo.getProfile().photo == null)
			userProfileRepo.precacheProfile() // only way to update photo link
			onStartup()
		}
	}

	private fun convertDateAndTimeToNetwork(dateTime: String): String? {
		if (dateTime == DEFAULT_DATE_TIME) return null

		val dateTimeFormatter = SimpleDateFormat(calendarDateAndTimeFormat, Locale.getDefault())
		val timeStampMs = dateTimeFormatter.parse(dateTime)
		val someOtherFormatter = SimpleDateFormat(networkDateAndTimeFormat, Locale.getDefault())
		someOtherFormatter.timeZone = TimeZone.getTimeZone("UTC")
		return someOtherFormatter.format(timeStampMs) ?: ""
	}

	fun updateLocation(countryString: String?, cityString: String?, districtString: String?) {
		onUpdatedValues(
			EditProfileAdapter.CHANGE_LOCATION_INDEX,
			formLocationToFragmentResult(countryString, cityString, districtString)
				?: context.getString(R.string.survey_option_null)
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

	private fun extractDistrictFromParentheses(inputString: String): String? {
		val startIndex = inputString.indexOf('(')
		val endIndex = inputString.indexOf(')')

		return if (startIndex != -1 && endIndex != -1 && endIndex > startIndex) {
			inputString.substring(startIndex + 1, endIndex)
		} else {
			null
		}
	}
}
