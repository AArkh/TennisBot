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
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
	private val userProfileRepo: UserProfileAndEnumsRepository,
	private val locationRepo: LocationRepository,
	private val locationDataMapper: LocationDataMapper,
	@ApplicationContext private val context: Context
) : ViewModel()  {

	companion object {
		const val NAME_SURNAME_REQUEST_KEY = "NAME_SURNAME_REQUEST_KEY"
		const val SELECTED_NAME_SURNAME = "SELECTED_NAME_SURNAME"
		const val BIRTHDAY_REQUEST_KEY = "BIRTHDAY_REQUEST_KEY"
		const val SELECTED_BIRTHDAY = "SELECTED_BIRTHDAY"
		const val LOCATION_REQUEST_KEY = "LOCATION_REQUEST_KEY"
		const val SELECTED_LOCATION = "SELECTED_LOCATION"
		const val PHONE_NUMBER_REQUEST_KEY = "PHONE_NUMBER_REQUEST_KEY"
		const val SELECTED_PHONE_NUMBER = "SELECTED_PHONE_NUMBER"
		const val TELEGRAM_REQUEST_KEY = "TELEGRAM_REQUEST_KEY"
		const val SELECTED_TELEGRAM = "SELECTED_TELEGRAM"
	}

	private val _uiStateFlow = MutableStateFlow(
		EditProfileUiState(
			profilePicture = "",
			emptyList()
		)
	)
	val uiStateFlow = _uiStateFlow.asStateFlow()

	val editProfileItems = listOf(
		EditProfileItem(icon = R.drawable.user, title = "Name Surname"),
		EditProfileItem(icon = R.drawable.birthday_cake, title = "d MMMM yyyy"),
		EditProfileItem(icon = R.drawable.location, title = "Country, City (District)"),
		EditProfileItem(icon = R.drawable.telephone, title = "+77777777777"),
		EditProfileItem(icon = R.drawable.telegram, title = "telegram"),
	)

	fun onStartup() {
		viewModelScope.launch {
			withContext(Dispatchers.IO) {
				val currentProfileData = userProfileRepo.getProfile()
				val locations = locationRepo.getLocations()
				val country = if (currentProfileData.primaryLocation != null) {
					locationDataMapper.getCountryList(locations)[currentProfileData.primaryLocation].name } else { null }
				val city = if (currentProfileData.secondaryLocation != null && country != null) {
					locationDataMapper.getCityList(locations, country)[currentProfileData.secondaryLocation].name } else { null }
				val location = if (country != null && city != null) {
					"$country, $city"
				} else { country ?: context.getString(R.string.survey_option_null) }

				val receivedDataList = listOf(
					currentProfileData.name,
					convertDateAndTime(currentProfileData.birthday ?: ""),
					location,
					userProfileRepo.getPhoneNumber() ?: context.getString(R.string.survey_option_null),
					currentProfileData.telegram ?: context.getString(R.string.survey_option_null)
				)

				val categoriesDataList = editProfileItems.mapIndexed { index, editProfileItem ->
					if (index == editProfileItems.lastIndex) {
						editProfileItem.copy(title = receivedDataList.getOrElse(index) { editProfileItem.title }, noUnderline = true)
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
		val currentState = uiStateFlow.value

		val updatedList = currentState.categoriesList.mapIndexed { index, editProfileItem ->
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

//		when(key) {
//			SELECTED_NAME_SURNAME -> {
//				currentState.categoriesList[0].copy(title = value)
//			}
//			SELECTED_BIRTHDAY -> {}
//			SELECTED_LOCATION -> {}
//			SELECTED_PHONE_NUMBER -> {}
//			SELECTED_TELEGRAM -> {}
//		}

		_uiStateFlow.value = currentState.copy(categoriesList = updatedList)
	}
}
