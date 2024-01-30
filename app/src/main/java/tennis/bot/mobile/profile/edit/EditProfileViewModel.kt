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
) : ViewModel() {

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

    private val _currentlyEditedData = MutableStateFlow(
        EditProfileUiState(
            profilePicture = "",
            emptyList() // todo тут храним измененные юзером данные и сохраняем их на бэкенд по выходу из этого экрана
        )
    )

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
                val editedProfileData = _currentlyEditedData.value


                val locations = locationRepo.getLocations()
                val country = getCountry(currentProfileData, editedProfileData)
//                    if (editedProfileData.primaryLocation != null) {
//                    locationDataMapper.getCountryList(locations)[editedProfileData.primaryLocation].name
//                } else if (currentProfileData.primaryLocation != null) {
//                    locationDataMapper.getCountryList(locations)[currentProfileData.primaryLocation].name
//                } else {
//                    null
//                }
                val city = if (currentProfileData.secondaryLocation != null && country != null) {
                    locationDataMapper.getCityList(locations, country)[currentProfileData.secondaryLocation].name
                } else {
                    null
                }
                val location = if (country != null && city != null) {
                    "$country, $city"
                } else {
                    country ?: context.getString(R.string.survey_option_null)
                }

                val receivedDataList =
                    listOf( // todo тут уже выставляем либо фактические данные, либо, при наличии, те данные, которые юзер сейчас редактирует из _currentlyEditedData
                        currentProfileData.name,
                        convertDateAndTime(currentProfileData.birthday ?: ""),
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
//		userProfileRepo.setProfileData(key, value)
        // todo если обновление имени и фамилии случаются тут, то делаем так ^^

        // todo если обновление случается после выхода с этого фрагмента, то делаем так:
//		_currentlyEditedData.value = editProfileItems[key].copy()
    }
}
