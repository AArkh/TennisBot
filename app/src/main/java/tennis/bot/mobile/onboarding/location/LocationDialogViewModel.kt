package tennis.bot.mobile.onboarding.location

import android.util.Log
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import tennis.bot.mobile.onboarding.phone.CountryItem
import javax.inject.Inject

@HiltViewModel
class LocationDialogViewModel @Inject constructor(
    private val repository: LocationRepo,
    private val dataMapper: LocationDataMapper,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {




    private val currentAction = savedStateHandle.get<String>(LocationFragment.SELECT_ACTION_KEY) ?: ""
    private val currentCountry = savedStateHandle.get<String>(LocationFragment.SELECTED_COUNTRY_NAME_KEY) ?: ""
    private val currentCity = savedStateHandle.get<String>(LocationFragment.SELECTED_CITY_NAME_KEY) ?: ""

    private val _uiStateFlow = MutableStateFlow<LocationDialogUiState>(
        LocationDialogUiState.Loading(
            // preselected values
            currentCountry, currentCity
        )
    )
    val uiStateFlow = _uiStateFlow.asStateFlow()

    fun onLoadingSelectedList() {
        when (currentAction) {
            LocationFragment.SELECT_COUNTRY -> {
                loadCountriesList()
            }
            LocationFragment.SELECT_CITY -> {
                // get selected country also
                loadCitiesList(currentCountry)
            }
            LocationFragment.SELECT_DISTRICT -> {
                // get selected country and city also
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

    fun loadCountriesList() {
        Log.d("1234567", "loadCountriesList: ")
        val currentState: LocationDialogUiState = _uiStateFlow.value
        val newLoadingState = LocationDialogUiState.Loading(
            currentState.currentCountry,
            currentState.currentCity,
        )
        _uiStateFlow.value = newLoadingState // Начинаем загрузку
        viewModelScope.launch(Dispatchers.IO) { // Уходим с ui-потока, юзер пока видит крутилку загрузочную
            kotlin.runCatching {
                val dataToPortray =
                    repository.getLocations() // Асинхронно получаем locations, если не получится, то поймаем ошибку
                val formatted = dataMapper.getCountryList(dataToPortray) // Маппим полученные данные в нужный формат
                _uiStateFlow.value = LocationDialogUiState.DataPassed(
                    currentState.currentCountry,
                    currentState.currentCity,
                    dataList = formatted) // Обновляем данные
            }.onFailure {
                // Если по какой-то причине репо или маппер взорвались при получении location - показываем юзеру ошибку
                _uiStateFlow.value = LocationDialogUiState.Error(
                    currentState.currentCountry,
                    currentState.currentCity,
                )
                Log.d("1234567", "loadCountriesList: error")
            }
        }
    }

    fun loadCitiesList(pickedCountry: String) {
        // аналогично как loadCountriesList fixme remove logs
        Log.d("1234567", "loadCitiesList: ")
        val currentState: LocationDialogUiState = _uiStateFlow.value
        val newLoadingState = LocationDialogUiState.Loading(
            currentState.currentCountry,
            currentState.currentCity,
        )
        _uiStateFlow.value = newLoadingState // Начинаем загрузку // Начинаем загрузку
        viewModelScope.launch(Dispatchers.IO) { // Уходим с ui-потока, юзер пока видит крутилку загрузочную
            kotlin.runCatching {
                val dataToPortray =
                    repository.getLocations() // Асинхронно получаем locations, если не получится, то поймаем ошибку
                val formatted =
                    dataMapper.getCityList(dataToPortray, pickedCountry) // Маппим полученные данные в нужный формат
                _uiStateFlow.value = LocationDialogUiState.DataPassed(
                    currentState.currentCountry,
                    currentState.currentCity,
                    dataList = formatted) // Обновляем данные
            }.onFailure {
                // Если по какой-то причине репо или маппер взорвались при получении location - показываем юзеру ошибку
                _uiStateFlow.value = LocationDialogUiState.Error(
                    currentState.currentCountry,
                    currentState.currentCity,
                )
                Log.d("1234567", "loadCitiesList: error")
            }
        }
    }

    fun loadDistrictsList(pickedCountry: String, pickedCity: String) {

        // todo remove comments
        // аналогично как loadCountriesList
        Log.d("1234567", "loadDistrictsList: ")
        val currentState: LocationDialogUiState = _uiStateFlow.value
        val newLoadingState = LocationDialogUiState.Loading(
            currentState.currentCountry,
            currentState.currentCity,
        )
        _uiStateFlow.value = newLoadingState // Начинаем загрузку// Начинаем загрузку
        viewModelScope.launch(Dispatchers.IO) { // Уходим с ui-потока, юзер пока видит крутилку загрузочную
            kotlin.runCatching {
                val dataToPortray =
                    repository.getLocations() // Асинхронно получаем locations, если не получится, то поймаем ошибку
                val formatted = dataMapper.getDistrictList(
                    dataToPortray,
                    pickedCountry,
                    pickedCity
                ) // Маппим полученные данные в нужный формат
                _uiStateFlow.value = LocationDialogUiState.DataPassed(
                    currentState.currentCountry,
                    currentState.currentCity,
                    dataList = formatted) // Обновляем данные
            }.onFailure {
                // Если по какой-то причине репо или маппер взорвались при получении location - показываем юзеру ошибку
                _uiStateFlow.value = LocationDialogUiState.Error(
                    currentState.currentCountry,
                    currentState.currentCity,
                )
                Log.d("1234567", "loadDistrictsList: error")
            }
        }
    }

    fun onSearchInputChanged(userInput: String) {
        val currentState = _uiStateFlow.value as? LocationDialogUiState.DataPassed ?: return
        val filteredList = currentState.dataList.filter { countryItem: CountryItem ->
            countryItem.countryName.contains(userInput, ignoreCase = true)
        }

        // option 1
        _uiStateFlow.value = LocationDialogUiState.DataPassed(
            currentState.currentCountry,
            currentState.currentCity,
            filteredList, // меняется
            userInput // меняется
        )

        // option 2, if both are data classes
//        _uiStateFlow.value = currentState.copy(
//            dataList = filteredList,
//            searchInput = userInput
//        )
    }
}
