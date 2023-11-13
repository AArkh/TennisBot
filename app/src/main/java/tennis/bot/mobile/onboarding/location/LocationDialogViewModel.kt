package tennis.bot.mobile.onboarding.location

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.AndroidEntryPoint
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

    private var locations = emptyList<Location>()
    private var countries = emptyList<CountryItem>()

    init {
        viewModelScope.launch {
            withContext(Dispatchers.IO){
            locations = repository.getLocations()
            }
            countries = LocationRepo.DataMapper().getCountryList(locations)
        }
    }

    private val _uiStateFlow: MutableStateFlow<List<CountryItem>> = MutableStateFlow(countries)
    val uiStateFlow = _uiStateFlow.asStateFlow()
}
