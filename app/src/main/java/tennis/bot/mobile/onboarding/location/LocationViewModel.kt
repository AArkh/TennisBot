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
import tennis.bot.mobile.onboarding.survey.AccountInfoRepository
import javax.inject.Inject

@HiltViewModel
class LocationViewModel @Inject constructor(
	private val repository: LocationRepository,
	private val accountInfo: AccountInfoRepository
) : ViewModel() {

	private val _uiStateFlow = MutableStateFlow<LocationUiState>(LocationUiState.Initial)
	val uiStateFlow = _uiStateFlow.asStateFlow()

	fun onCountrySelected(selectedCountry: String) {
		viewModelScope.launch {
			withContext(Dispatchers.IO) {
				kotlin.runCatching {
					val locations = repository.getLocations()
					val nextButtonEnabled = locations.find { country: Location ->
						country.countryName == selectedCountry
					}?.cities.isNullOrEmpty()
					_uiStateFlow.value = LocationUiState.CountrySelected(
						country = selectedCountry,
						nextButtonEnabled = nextButtonEnabled
					)
				}.onFailure {
					_uiStateFlow.value = LocationUiState.Error
				}
			}
		}
	}

	fun onCitySelected(selectedCountry: String, selectedCity: String) {
		viewModelScope.launch {
			withContext(Dispatchers.IO) {
				kotlin.runCatching {
					val newState = LocationUiState.CitySelected(
						country = selectedCountry,
						city = selectedCity,
						nextButtonEnabled = repository.getLocations()
							.find { it.countryName == selectedCountry }
							?.cities!!.find { it.name == selectedCity }
							?.districts!!.isEmpty()
					)
					_uiStateFlow.value = newState
					Log.d("1234567", "onCitySelected: success")
				}.onFailure {
					_uiStateFlow.value = LocationUiState.Error
					Log.d("1234567", "onCitySelected: error")
				}
			}
		}
	}

	fun onDistrictSelected(selectedCountry: String, selectedCity: String, selectedDistrict: String) {
		val newState = LocationUiState.DistrictSelected(
			country = selectedCountry,
			city = selectedCity,
			district = selectedDistrict,
			nextButtonEnabled = true
		)
		_uiStateFlow.value = newState

		recordLocationValues(
			selectedCountry = selectedCountry,
			selectedCity = selectedCity,
			selectedDistrict = selectedDistrict
		)
	}

	fun recordLocationValues(selectedCountry: String, selectedCity: String, selectedDistrict: String) {
		viewModelScope.launch {
			withContext(Dispatchers.IO) {
				var countryInt = 0
				var cityInt = 0
				var districtInt = 0
				kotlin.runCatching {
					countryInt = repository.getLocations().find { it.countryName == selectedCountry }!!.id
				}.onFailure {
					Log.d("1234567", "recordLocationValues: country error")
				}
				kotlin.runCatching {
					cityInt = repository.getLocations().find { it.countryName == selectedCountry }
						?.cities!!.find { it.name == selectedCity }!!.id
				}.onFailure {
					Log.d("1234567", "recordLocationValues: city error")
				}
				kotlin.runCatching {
					districtInt = repository.getLocations().find { it.countryName == selectedCountry }
						?.cities!!.find { it.name == selectedCity }
						?.districts!!.find { it.title == selectedDistrict }!!.id
				}.onFailure {
					Log.d("1234567", "recordLocationValues: district error")
				}
				accountInfo.recordLocationData(countryInt, cityInt, districtInt)
			}
		}
	}
}
