package tennis.bot.mobile.onboarding.location

import kotlinx.coroutines.flow.MutableStateFlow
import tennis.bot.mobile.onboarding.phone.CountryItem
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationRepository @Inject constructor() {

    val selectedCountryFlow = MutableStateFlow<CountryItem?>(null)
}
