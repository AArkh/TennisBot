package tennis.bot.mobile.onboarding.location

import retrofit2.Call
import retrofit2.http.GET
import tennis.bot.mobile.onboarding.phone.CountryItem

interface LocationApi {

    @GET("api/dictionaries/countries")
    fun getLocationData() : Call<CountryList>
}

class CountryList: ArrayList<CountryItem>()