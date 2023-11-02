package tennis.bot.mobile.onboarding.phone

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import tennis.bot.mobile.R
import javax.inject.Inject

@HiltViewModel
class CountryCodesViewModel @Inject constructor(
    private val repository: CountryCodeRepository,
): ViewModel() {

    private val _uiStateFlow: MutableStateFlow<List<CountryItem>> = MutableStateFlow(
        listOf(
            CountryItem(R.drawable.russia_big, "Россия", "+7"),
            CountryItem(R.drawable.ukraine, "Украина", "+380"),
            CountryItem(R.drawable.belarus, "Беларусь", "+375"),
            CountryItem(R.drawable.kazakhstan, "Казахстан", "+7"),
            CountryItem(R.drawable.canada, "Канада", "+1")
        )
    )
    val uiStateFlow = _uiStateFlow.asStateFlow()

    fun onClick(item: CountryItem) {
        repository.selectedCountryFlow.value = item
    }
}