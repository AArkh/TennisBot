package tennis.bot.mobile.onboarding.location

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import tennis.bot.mobile.onboarding.location.LocationFragment.Companion.SELECT_COUNTRY
import tennis.bot.mobile.onboarding.phone.CountryItem
import javax.inject.Inject

@HiltViewModel
class LocationDialogViewModel @Inject constructor(
    private val repository: LocationRepo,
    private val dataMapper: LocationDataMapper,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val currentAction = savedStateHandle.get<String>(LocationFragment.SELECT_ACTION) ?: ""

    private val _uiStateFlow = MutableStateFlow<LocationDialogUiState>(
        LocationDialogUiState.Loading(
            // preselected values
            "", "", ""
        )
    )
    val uiStateFlow = _uiStateFlow.asStateFlow()

    init {
        uiStateFlow.value as LocationDialogUiState.Loading

        when (currentAction) {
            SELECT_COUNTRY -> { // todo consts
                loadCountriesList()
            }
            "city" -> {
                // get selected country also
                loadCitiesList(LocationDialogFragment.currentCountry)
            }
            "district" -> {
                // get selected country and city also
                loadDistrictsList(LocationDialogFragment.currentCountry, LocationDialogFragment.currentCity)
            }
        }
    }

    fun loadCountriesList() {
        Log.d("1234567", "loadCountriesList: ")
        val currentState: LocationDialogUiState = _uiStateFlow.value
        val newLoadingState = LocationDialogUiState.Loading(
            currentState.currentCountry,
            currentState.currentCity,
            currentState.currentDistrict
        )
        _uiStateFlow.value = newLoadingState // Начинаем загрузку
        viewModelScope.launch(Dispatchers.IO) { // Уходим с ui-потока, юзер пока видит крутилку загрузочную
            kotlin.runCatching {
                val dataToPortray =
                    repository.getLocations() // Асинхронно получаем locations, если не получится, то поймаем ошибку
                val formatted = dataMapper.getCountryList(dataToPortray) // Маппим полученные данные в нужный формат
                _uiStateFlow.value = LocationDialogUiState.DataPassed(formatted) // Обновляем данные
            }.onFailure {
                // Если по какой-то причине репо или маппер взорвались при получении location - показываем юзеру ошибку
                _uiStateFlow.value = LocationDialogUiState.Error
                Log.d("1234567", "loadCountriesList: error")
            }
        }
    }

    fun loadCitiesList(pickedCountry: String) {
        // аналогично как loadCountriesList fixme remove logs
        Log.d("1234567", "loadCitiesList: ")
        _uiStateFlow.value = LocationDialogUiState.Loading // Начинаем загрузку
        viewModelScope.launch(Dispatchers.IO) { // Уходим с ui-потока, юзер пока видит крутилку загрузочную
            kotlin.runCatching {
                val dataToPortray =
                    repository.getLocations() // Асинхронно получаем locations, если не получится, то поймаем ошибку
                val formatted =
                    dataMapper.getCityList(dataToPortray, pickedCountry) // Маппим полученные данные в нужный формат
                _uiStateFlow.value = LocationDialogUiState.DataPassed(formatted) // Обновляем данные
            }.onFailure {
                // Если по какой-то причине репо или маппер взорвались при получении location - показываем юзеру ошибку
                _uiStateFlow.value = LocationDialogUiState.Error
                Log.d("1234567", "loadCitiesList: error")
            }
        }
    }

    fun loadDistrictsList(pickedCountry: String, pickedCity: String) {

        // todo remove comments
        // аналогично как loadCountriesList
        Log.d("1234567", "loadDistrictsList: ")
        _uiStateFlow.value = LocationDialogUiState.Loading // Начинаем загрузку
        viewModelScope.launch(Dispatchers.IO) { // Уходим с ui-потока, юзер пока видит крутилку загрузочную
            kotlin.runCatching {
                val dataToPortray =
                    repository.getLocations() // Асинхронно получаем locations, если не получится, то поймаем ошибку
                val formatted = dataMapper.getDistrictList(
                    dataToPortray,
                    pickedCountry,
                    pickedCity
                ) // Маппим полученные данные в нужный формат
                _uiStateFlow.value = LocationDialogUiState.DataPassed(formatted) // Обновляем данные
            }.onFailure {
                // Если по какой-то причине репо или маппер взорвались при получении location - показываем юзеру ошибку
                _uiStateFlow.value = LocationDialogUiState.Error
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
            currentState.currentDistrict,
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
