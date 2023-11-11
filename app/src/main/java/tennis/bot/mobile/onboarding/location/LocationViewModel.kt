package tennis.bot.mobile.onboarding.location

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocationViewModel @Inject constructor(
    private val repository: LocationRepo,
) : ViewModel() {

    @Inject lateinit var locationApi: LocationApi

    private val _uiStateFlow = MutableStateFlow<LocationUiState>(LocationUiState.Loading)


    val uiStateFlow = _uiStateFlow.asStateFlow()
    private var locations = emptyList<Location>()
    init {
        viewModelScope.launch {
            locations = repository.getLocations()
            val country = locations.find{
                it.countryName == "Россия"
                // default country = picked phone code
            }
            if(country == null) {
                _uiStateFlow.value = LocationUiState.Initial
            } else {
                _uiStateFlow.value = LocationUiState.CountrySelected(country.countryName, false)
            }

        }

    }

    fun onCountrySelected(selectedCountry: String) {
        val prevState = uiStateFlow.value as LocationUiState.Initial
        val newState = LocationUiState.CountrySelected(
            country = selectedCountry,
            nextButtonEnabled = false
        )
    }

    fun onCitySelected(selectedCountry: String, selectedCity: String) {
        val prevState = uiStateFlow.value as LocationUiState.CountrySelected
        val newState = LocationUiState.CitySelected(
            country = selectedCountry,
            city = selectedCity,
            nextButtonEnabled = false
            )
    }

    fun onDistrictSelected(selectedCountry: String, selectedCity: String, selectedDistrict: String) {
        val prevState = uiStateFlow.value as LocationUiState.CitySelected
        val newState = LocationUiState.DistrictSelected(
            country = selectedCountry,
            city = selectedCity,
            district = selectedDistrict,
            nextButtonEnabled = true
        )
    }




//    fun onSearchInput(userInput: String) {
//        val filteredList = initialList.filter {
//            it.countryName.contains(userInput, ignoreCase = true)
//        }
//        _uiStateFlow.value = filteredList
//    }
}
