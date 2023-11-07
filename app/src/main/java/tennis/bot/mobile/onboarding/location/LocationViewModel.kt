package tennis.bot.mobile.onboarding.location

import android.util.Log
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import tennis.bot.mobile.R
import tennis.bot.mobile.onboarding.phone.CountryItem
import javax.inject.Inject

@HiltViewModel
class LocationViewModel @Inject constructor(
    private val repository: LocationRepository,
) : ViewModel() {

    @Inject lateinit var locationApi: LocationApi

    private val initialList = listOf(
        CountryItem(R.drawable.russia, "Россия", "+7"),
        CountryItem(R.drawable.ukraine, "Украина", "+380"),
        CountryItem(R.drawable.belarus, "Беларусь", "+375"),
        CountryItem(R.drawable.kazakhstan, "Казахстан", "+7"),
        CountryItem(R.drawable.canada, "Канада", "+1")
    )

    private val initialList2 =
        locationApi.getLocationData().enqueue(object: Callback<CountryList> {
            override fun onResponse(call: Call<CountryList>, response: Response<CountryList>) {
                val responseItem = response.body()?.listIterator()
                val listOfCountries = ArrayList<CountryItem>()
                if (responseItem != null) {
                    while (responseItem.hasNext()) {
                        val item: CountryItem = responseItem.next()
                        listOfCountries.add(
                            element = CountryItem(
                                null,
                                item.countryName,
                                null
                            )
                        )
                        Log.i("items", item.countryName)
                    }

                }
            }

            override fun onFailure(call: Call<CountryList>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })

    private val _uiStateFlow: MutableStateFlow<List<CountryItem>> = MutableStateFlow(initialList)
    val uiStateFlow = _uiStateFlow.asStateFlow()

    fun onClick(item: CountryItem) {
        repository.selectedCountryFlow.value = item
    }

    fun onSearchInput(userInput: String) {
        val filteredList = initialList.filter {
            it.countryName.contains(userInput, ignoreCase = true)
        }
        _uiStateFlow.value = filteredList
    }
}
