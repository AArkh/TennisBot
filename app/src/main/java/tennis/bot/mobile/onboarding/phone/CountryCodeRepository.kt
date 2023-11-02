package tennis.bot.mobile.onboarding.phone

import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CountryCodeRepository @Inject constructor() {

    val selectedCountryFlow = MutableStateFlow<CountryItem?>(null)
}