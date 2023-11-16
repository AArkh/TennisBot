package tennis.bot.mobile.onboarding.location

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
) : ViewModel(){

    private val _uiStateFlow = MutableStateFlow<LocationDialogUiState>(LocationDialogUiState.Loading)
    val uiStateFlow = _uiStateFlow.asStateFlow()

    var locations: List<Location> = emptyList()
    var dataToPortray: List<CountryItem> = emptyList()

    init {
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                locations = repository.getLocations()
                loadCountriesList()
            }
        }
    }

    fun loadCountriesList() {
        dataToPortray = LocationRepo.DataMapper().getCountryList(locations)
        _uiStateFlow.value = LocationDialogUiState.countryDataPassed(dataToPortray)
    }

    fun loadCitiesList() {
        dataToPortray = LocationRepo.DataMapper().getCityList(locations, "Russia")
        _uiStateFlow.value = LocationDialogUiState.cityDataPassed(dataToPortray)
    }

    fun loadDistrictsList() {
        dataToPortray = LocationRepo.DataMapper().getDistrictList(locations, "Russia", "SPb")
        _uiStateFlow.value = LocationDialogUiState.districtDataPassed(dataToPortray)
    }



}
