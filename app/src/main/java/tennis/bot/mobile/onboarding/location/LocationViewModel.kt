package tennis.bot.mobile.onboarding.location

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tennis.bot.mobile.onboarding.survey.OnboardingRepository
import tennis.bot.mobile.utils.isRuLocale
import javax.inject.Inject

@HiltViewModel
open class LocationViewModel @Inject constructor(
	private val repository: LocationRepository,
	private val accountInfo: OnboardingRepository
) : ViewModel() {

	private val _uiStateFlow = MutableStateFlow<LocationUiState>(LocationUiState.Initial)
	val uiStateFlow = _uiStateFlow.asStateFlow()
	val isRuLocale = isRuLocale()

	fun onCountrySelected(selectedCountry: String) {
		viewModelScope.launch {
			withContext(Dispatchers.IO) {
				kotlin.runCatching {
					val locations = repository.getLocations()
					val nextButtonEnabled = locations.find { country: Location ->
						(if(isRuLocale) country.countryName else country.countryNameEn) == selectedCountry
					}?.cities.isNullOrEmpty()
					_uiStateFlow.value = LocationUiState.CountrySelected(
						country = selectedCountry,
						nextButtonEnabled = nextButtonEnabled
					)
				}.onFailure {
					_uiStateFlow.value = LocationUiState.Error
					FirebaseCrashlytics.getInstance().recordException(it)
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
							.find { (if(isRuLocale) it.countryName else it.countryNameEn) == selectedCountry }
							?.cities!!.find { (if(isRuLocale) it.name else it.nameEn) == selectedCity }
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
				var countryInt: Int = 0
				var cityInt: Int = 0
				var districtInt: Int? = null
				kotlin.runCatching {
					countryInt = repository.getLocations().find { (if(isRuLocale) it.countryName else it.countryNameEn) == selectedCountry }!!.id
				}.onFailure {
					FirebaseCrashlytics.getInstance().log("recordLocationValues: country error")
				}
				kotlin.runCatching {
					cityInt = repository.getLocations().find { (if(isRuLocale) it.countryName else it.countryNameEn) == selectedCountry }
						?.cities!!.find { (if(isRuLocale) it.name else it.nameEn) == selectedCity }!!.id
				}.onFailure {
					FirebaseCrashlytics.getInstance().log("recordLocationValues: city error")
				}
				kotlin.runCatching {
					districtInt = repository.getLocations().find { (if(isRuLocale) it.countryName else it.countryNameEn) == selectedCountry }
						?.cities!!.find { (if(isRuLocale) it.name else it.nameEn) == selectedCity }
						?.districts!!.find { (if(isRuLocale) it.title else it.nameEn) == selectedDistrict }!!.id
				}.onFailure {
					FirebaseCrashlytics.getInstance().log("recordLocationValues: district error")
				}
				accountInfo.recordLocationData(countryInt, cityInt, districtInt)
			}
		}
	}
}
