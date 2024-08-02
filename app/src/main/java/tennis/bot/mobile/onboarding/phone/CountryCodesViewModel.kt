package tennis.bot.mobile.onboarding.phone

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.i18n.phonenumbers.PhoneNumberUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import tennis.bot.mobile.onboarding.location.LocationRepository
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class CountryCodesViewModel @Inject constructor(
    private val locationRepository: LocationRepository
) : ViewModel() {

//    private val initialList = listOf(
//        CountryItem(R.drawable.russia, "Россия", "+7"),
//        CountryItem(R.drawable.ukraine, "Украина", "+380"),
//        CountryItem(R.drawable.belarus, "Беларусь", "+375"),
//        CountryItem(R.drawable.kazakhstan, "Казахстан", "+7"),
//        CountryItem(R.drawable.canada, "Канада", "+1")
//    )
    private val _uiStateFlow: MutableStateFlow<List<CountryItem>> = MutableStateFlow(emptyList())
    val uiStateFlow = _uiStateFlow.asStateFlow().onStart { getAllCountryNamesAndCodes() }

    fun getAllCountryNamesAndCodes() {
        viewModelScope.launch(Dispatchers.IO) {
            val countryList = locationRepository.getLocations()
            val availableLocales = Locale.getAvailableLocales()
            val countryItemList = mutableListOf<CountryItem>()
            val nullCountryItemList = mutableListOf<CountryItem>()

            for (location in countryList) {
                val countryName = location.countryNameEn ?: ""
                val countryCode = availableLocales.find { it.displayCountry == countryName }?.country ?: ""
                if (countryName.isNotBlank() && countryCode.isNotBlank()) {

                    val item = CountryItem(
                        "https://hatscripts.github.io/circle-flags/flags/${countryCode.lowercase()}.svg",
                        countryName,
                        getPhoneCodeFromCountryCode(countryCode) ?: ""
                    )
                    countryItemList.add(item)
                    Log.d("getAllCountryNamesAndCodes", item.toString())
                } else {
                    nullCountryItemList.add(CountryItem(
                        "https://hatscripts.github.io/circle-flags/flags/${countryCode.lowercase()}.svg",
                        countryName,
                        getPhoneCodeFromCountryCode(countryCode) ?: ""
                    ))
                }
                for (item in nullCountryItemList) {
                    Log.d("getAllNullCountryNamesAndCodes", item.toString())
                }
                _uiStateFlow.value = countryItemList.toList()
            }
        }
    }

    fun getPhoneCodeFromCountryCode(cca2Code: String): String? {
        val phoneNumberUtil = PhoneNumberUtil.getInstance()
        return try {
            val regionCode = phoneNumberUtil.getRegionCodeForCountryCode(phoneNumberUtil.getCountryCodeForRegion(cca2Code))
            if (regionCode != null) {
                "+" + phoneNumberUtil.getCountryCodeForRegion(regionCode).toString()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    fun onSearchInput(userInput: String) {
        val filteredList = _uiStateFlow.value.filter {
            it.name.contains(userInput, ignoreCase = true)
                || it.code.contains(userInput, ignoreCase = true)
        }
        _uiStateFlow.value = filteredList
    }
}