package tennis.bot.mobile.profile.edit

import android.content.Context
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
import tennis.bot.mobile.utils.convertDateAndTime
import tennis.bot.mobile.utils.convertLocationIntToString
import tennis.bot.mobile.utils.convertLocationStringToInt
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
	private val userProfileRepo: UserProfileAndEnumsRepository,
	private val locationRepo: LocationRepository,
	private val locationDataMapper: LocationDataMapper,
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
	private val _currentlyEditedData = MutableStateFlow(
		EditProfileUiState(
			profilePicture = "",
			editProfileItems // todo тут храним измененные юзером данные и сохраняем их на бэкенд по выходу из этого экрана
		)
	)

	fun onStartup() {
		viewModelScope.launch {
			withContext(Dispatchers.IO) {
				val currentProfileData = userProfileRepo.getProfile()
				val updatedData = _currentlyEditedData.value.categoriesList
				val locations = locationRepo.getLocations()
				val country = if (currentProfileData.primaryLocation != null) {
					locationDataMapper.getCountryList(locations)[currentProfileData.primaryLocation].name
				} else { null }
				val city = if (currentProfileData.secondaryLocation != null && country != null) {
					locationDataMapper.getCityList(locations, country)[currentProfileData.secondaryLocation].name
				} else { null }
				val location = if (country != null && city != null) { "$country, $city" } else { country ?: context.getString(R.string.survey_option_null) }

				val receivedDataList = listOf(
					updatedData[0].title ?: currentProfileData.name,
					convertDateAndTime(updatedData[1].title ?: currentProfileData.birthday ?: "")
						?: context.getString(R.string.survey_option_null),
					updatedData[2].title ?: location,
					(updatedData[3].title ?: userProfileRepo.getPhoneNumber()) ?: context.getString(R.string.survey_option_null),
					(updatedData[4].title ?: currentProfileData.telegram) ?: context.getString(R.string.survey_option_null)
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
		val currentData = _currentlyEditedData.value

		val updatedList = currentData.categoriesList.mapIndexed { index, editProfileItem ->
			if (key == index) {
				when (index) {
					0 -> { editProfileItem.copy(title = value) }
					1 -> { editProfileItem.copy(title = value) }
					2 -> { editProfileItem.copy(title = value) }
					3 -> { editProfileItem.copy(title = value) }
					4 -> { editProfileItem.copy(title = value) }
					else -> { editProfileItem }
				}
			} else { editProfileItem }
		}

		_currentlyEditedData.value = currentData.copy(categoriesList = updatedList)
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
