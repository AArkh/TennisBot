package tennis.bot.mobile.core

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import tennis.bot.mobile.onboarding.phone.CountryItem

interface apiService {
    @GET("core/api/dictionaries/countries")
    fun getData(): Call<CountryItem>

}