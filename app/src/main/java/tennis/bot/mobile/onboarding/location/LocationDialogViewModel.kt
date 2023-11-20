package tennis.bot.mobile.onboarding.location

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tennis.bot.mobile.onboarding.phone.CountryItem
import javax.inject.Inject

@HiltViewModel
class LocationDialogViewModel @Inject constructor(
    private val repository: LocationRepo,
    private val dataMapper: LocationDataMapper
) : ViewModel(){

    private val _uiStateFlow = MutableStateFlow<LocationDialogUiState>(LocationDialogUiState.Loading)
    val uiStateFlow = _uiStateFlow.asStateFlow()



    fun loadCountriesList() {
        Log.d("1234567", "loadCountriesList: ")
        _uiStateFlow.value = LocationDialogUiState.Loading // Начинаем загрузку
        viewModelScope.launch(Dispatchers.IO) { // Уходим с ui-потока, юзер пока видит крутилку загрузочную
            kotlin.runCatching {
                val dataToPortray = repository.getLocations() // Асинхронно получаем locations, если не получится, то поймаем ошибку
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
        // аналогично как loadCountriesList
        Log.d("1234567", "loadCitiesList: ")
        _uiStateFlow.value = LocationDialogUiState.Loading // Начинаем загрузку
        viewModelScope.launch(Dispatchers.IO) { // Уходим с ui-потока, юзер пока видит крутилку загрузочную
            kotlin.runCatching {
                val dataToPortray = repository.getLocations() // Асинхронно получаем locations, если не получится, то поймаем ошибку
                val formatted = dataMapper.getCityList(dataToPortray, pickedCountry) // Маппим полученные данные в нужный формат
                _uiStateFlow.value = LocationDialogUiState.DataPassed(formatted) // Обновляем данные
            }.onFailure {
                // Если по какой-то причине репо или маппер взорвались при получении location - показываем юзеру ошибку
                _uiStateFlow.value = LocationDialogUiState.Error
                Log.d("1234567", "loadCitiesList: error")
            }
        }
    }

    fun loadDistrictsList(pickedCountry: String, pickedCity: String) {
        // аналогично как loadCountriesList
        Log.d("1234567", "loadDistrictsList: ")
        _uiStateFlow.value = LocationDialogUiState.Loading // Начинаем загрузку
        viewModelScope.launch(Dispatchers.IO) { // Уходим с ui-потока, юзер пока видит крутилку загрузочную
            kotlin.runCatching {
                val dataToPortray = repository.getLocations() // Асинхронно получаем locations, если не получится, то поймаем ошибку
                val formatted = dataMapper.getDistrictList(dataToPortray, pickedCountry, pickedCity) // Маппим полученные данные в нужный формат
                _uiStateFlow.value = LocationDialogUiState.DataPassed(formatted) // Обновляем данные
            }.onFailure {
                // Если по какой-то причине репо или маппер взорвались при получении location - показываем юзеру ошибку
                _uiStateFlow.value = LocationDialogUiState.Error
                Log.d("1234567", "loadDistrictsList: error")
            }
        }
    }
}
