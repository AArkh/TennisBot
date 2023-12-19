package tennis.bot.mobile.onboarding.location

import android.content.Context
import android.util.Log
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import tennis.bot.mobile.R
import tennis.bot.mobile.onboarding.phone.CountryItem
import javax.inject.Inject

@HiltViewModel
class LocationDialogViewModel @Inject constructor(
    private val repository: LocationRepository,
    private val dataMapper: LocationDataMapper,
    private val savedStateHandle: SavedStateHandle,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val currentAction = savedStateHandle.get<String>(LocationFragment.SELECT_ACTION_KEY) ?: ""
    private val currentCountry = savedStateHandle.get<String>(LocationFragment.SELECTED_COUNTRY_NAME_KEY) ?: ""
    private val currentCity = savedStateHandle.get<String>(LocationFragment.SELECTED_CITY_NAME_KEY) ?: ""

    private val _uiStateFlow = MutableStateFlow<LocationDialogUiState>(
        LocationDialogUiState.Loading(
            currentCountry, currentCity, context.getString(R.string.location_choose_country)
        )
    )
    val uiStateFlow = _uiStateFlow.asStateFlow()

    fun onLoadingSelectedList() {
        when (currentAction) {
            LocationFragment.SELECT_COUNTRY -> {
                loadCountriesList()
            }
            LocationFragment.SELECT_CITY -> {
                loadCitiesList(currentCountry)
            }
            LocationFragment.SELECT_DISTRICT -> {
                loadDistrictsList(currentCountry, currentCity)
            }
        }
    }

    init {
        uiStateFlow.value as LocationDialogUiState.Loading

        onLoadingSelectedList()
    }

    companion object {
        const val COUNTRY_REQUEST_KEY = "COUNTRY_KEY"
        const val SELECTED_COUNTRY_KEY = "SELECTED_COUNTRY_KEY"
        const val CITY_REQUEST_KEY = "CITY_KEY"
        const val SELECTED_CITY_KEY = "SELECTED_CITY_KEY"
        const val DISTRICT_REQUEST_KEY = "DISTRICT_KEY"
        const val SELECTED_DISTRICT_KEY = "SELECTED_DISTRICT_KEY"
    }


    fun onPickedListItem(countryItem: CountryItem, activity: FragmentActivity) {
        when(currentAction) {
            LocationFragment.SELECT_COUNTRY -> {
                activity.supportFragmentManager.setFragmentResult(
                    COUNTRY_REQUEST_KEY,
                    bundleOf(SELECTED_COUNTRY_KEY to countryItem.countryName)
                )
            }
            LocationFragment.SELECT_CITY -> {
                activity.supportFragmentManager.setFragmentResult(
                    CITY_REQUEST_KEY,
                    bundleOf(SELECTED_CITY_KEY to countryItem.countryName)
                )
            }
            LocationFragment.SELECT_DISTRICT -> {
                activity.supportFragmentManager.setFragmentResult(
                    DISTRICT_REQUEST_KEY,
                    bundleOf(SELECTED_DISTRICT_KEY to countryItem.countryName)
                )
            }
        }
    }

    private fun loadCountriesList() {
        val currentState: LocationDialogUiState = _uiStateFlow.value
        val newLoadingState = LocationDialogUiState.Loading(
            currentState.currentCountry,
            currentState.currentCity,
            currentState.title
        )
        _uiStateFlow.value = newLoadingState
        viewModelScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                val dataToPortray = repository.getLocations()
                val formatted = dataMapper.getCountryList(dataToPortray)
                _uiStateFlow.value = LocationDialogUiState.DataPassed(
                    currentState.currentCountry,
                    currentState.currentCity,
                    dataList = formatted,
                    title = context.getString(R.string.location_choose_country))
            }.onFailure {
                _uiStateFlow.value = LocationDialogUiState.Error(
                    currentState.currentCountry,
                    currentState.currentCity,
                    currentState.title
                )
                Log.d("1234567", "loadCountriesList: error")
            }
        }
    }

    private fun loadCitiesList(pickedCountry: String) {
        val currentState: LocationDialogUiState = _uiStateFlow.value
        val newLoadingState = LocationDialogUiState.Loading(
            currentState.currentCountry,
            currentState.currentCity,
            currentState.title
        )
        _uiStateFlow.value = newLoadingState
        viewModelScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                val dataToPortray = repository.getLocations()
                val formatted = dataMapper.getCityList(dataToPortray, pickedCountry)
                _uiStateFlow.value = LocationDialogUiState.DataPassed(
                    currentState.currentCountry,
                    currentState.currentCity,
                    dataList = formatted,
                    title = context.getString(R.string.location_choose_city))
            }.onFailure {
                _uiStateFlow.value = LocationDialogUiState.Error(
                    currentState.currentCountry,
                    currentState.currentCity,
                    currentState.title
                )
            }
        }
    }

    private fun loadDistrictsList(pickedCountry: String, pickedCity: String) {
        val currentState: LocationDialogUiState = _uiStateFlow.value
        val newLoadingState = LocationDialogUiState.Loading(
            currentState.currentCountry,
            currentState.currentCity,
            currentState.title
        )
        _uiStateFlow.value = newLoadingState
        viewModelScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                val dataToPortray = repository.getLocations()
                val formatted = dataMapper.getDistrictList(
                    dataToPortray,
                    pickedCountry,
                    pickedCity
                )
                _uiStateFlow.value = LocationDialogUiState.DataPassed(
                    currentState.currentCountry,
                    currentState.currentCity,
                    dataList = formatted,
                    title = context.getString(R.string.location_choose_district))
            }.onFailure {
                _uiStateFlow.value = LocationDialogUiState.Error(
                    currentState.currentCountry,
                    currentState.currentCity,
                    currentState.title
                )
            }
        }
    }

    fun onSearchInputChanged(userInput: String) {
        val currentState = _uiStateFlow.value as? LocationDialogUiState.DataPassed ?: return
        val filteredList = currentState.dataList.filter { countryItem: CountryItem ->
            countryItem.countryName.contains(userInput, ignoreCase = true)
        }

        _uiStateFlow.value = LocationDialogUiState.DataPassed(
            currentState.currentCountry,
            currentState.currentCity,
            title = currentState.title,
            filteredList,
            userInput
        )
    }
}
