package tennis.bot.mobile.onboarding.phone

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import tennis.bot.mobile.R
import javax.inject.Inject

@HiltViewModel
class CountryCodesViewModel @Inject constructor() : ViewModel() {

    private val initialList = listOf(
        CountryItem(R.drawable.russia, "Россия", "+7"),
        CountryItem(R.drawable.ukraine, "Украина", "+380"),
        CountryItem(R.drawable.belarus, "Беларусь", "+375"),
        CountryItem(R.drawable.kazakhstan, "Казахстан", "+7"),
        CountryItem(R.drawable.canada, "Канада", "+1")
    )
    private val _uiStateFlow: MutableStateFlow<List<CountryItem>> = MutableStateFlow(initialList)
    val uiStateFlow = _uiStateFlow.asStateFlow()

    fun onSearchInput(userInput: String) {
        val filteredList = initialList.filter {
            it.name.contains(userInput, ignoreCase = true)
                || it.code.contains(userInput, ignoreCase = true)
        }
        _uiStateFlow.value = filteredList
    }
}