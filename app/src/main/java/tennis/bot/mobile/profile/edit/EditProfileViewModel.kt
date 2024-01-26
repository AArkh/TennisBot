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
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
	private val userProfileRepo: UserProfileAndEnumsRepository,
	private val locationRepo: LocationRepository,
	private val locationDataMapper: LocationDataMapper,
	@ApplicationContext private val context: Context
) : ViewModel()  {

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
					locationDataMapper.getCountryList(locations)[currentProfileData.primaryLocation].countryName } else { null }
				val city = if (currentProfileData.secondaryLocation != null && country != null) {
					locationDataMapper.getCityList(locations, country)[currentProfileData.secondaryLocation].countryName } else { null }
				val location = if (country != null && city != null) {
					"$country, $city"
				} else { country ?: context.getString(R.string.survey_option_null) }

				val receivedDataList = listOf(
					currentProfileData.name,
					currentProfileData.birthday ?: context.getString(R.string.survey_option_null),
					"$country, $city",
					userProfileRepo.getPhoneNumber() ?: context.getString(R.string.survey_option_null),
					currentProfileData.telegram ?: context.getString(R.string.survey_option_null)
				)

				val categoriesDataList = editProfileItems.mapIndexed { index, editProfileItem ->
					editProfileItem.copy(title = receivedDataList.getOrElse(index) { editProfileItem.title })
				}

				_uiStateFlow.value = EditProfileUiState(
					profilePicture = currentProfileData.photo,
					categoriesList = categoriesDataList
				)
			}
		}
	}
}
