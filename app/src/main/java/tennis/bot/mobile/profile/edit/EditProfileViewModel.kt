package tennis.bot.mobile.profile.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
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
) : ViewModel()  {

	private val _uiStateFlow = MutableStateFlow(
		EditProfileUiState(
			profilePicture = "",
			nameSurname = "",
			dateOfBirthday = "",
			location = "",
			phoneNumber = "",
			telegramId = "",
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
			val currentProfileData = userProfileRepo.getProfile()
			val locations = locationRepo.getLocations()
			val country = locationDataMapper.getCountryList(locations)[currentProfileData.primaryLocation!!].countryName
			val city = locationDataMapper.getCityList(locations, country)[currentProfileData.secondaryLocation!!].countryName
			val location = "$country, $city"

			_uiStateFlow.value = EditProfileUiState(
				profilePicture = currentProfileData.photo ?: "", // mb add explicit 'user' placeholder here
				nameSurname = currentProfileData.name,
				dateOfBirthday = currentProfileData.birthday ?: "",
				location = location,
				phoneNumber = "", // where to get phone number?
				telegramId = currentProfileData.telegram ?: "",
			)
		}
	}

//	private fun ImageView.loadPlayerImage() { // from AccountPage
//		if (profileImageUrl == null) return
//
//		if (profileImageUrl.contains("default")) {
//			val resourceId = getDefaultDrawableResourceId(
//				binding.accountPhoto.context,
//				profileImageUrl.removeSuffix(".png")
//			)
//			binding.accountPhoto.visibility = View.VISIBLE
//			if (resourceId != null) binding.accountPhoto.setImageResource(resourceId)
//			binding.placeholderPhoto.visibility = View.GONE
//		} else {
//			binding.accountPhoto.visibility = View.VISIBLE
//			binding.accountPhoto.load(AccountPageAdapter.IMAGES_LINK + profileImageUrl) { crossfade(true) }
//			binding.placeholderPhoto.visibility = View.GONE
//		}
//	}

}
