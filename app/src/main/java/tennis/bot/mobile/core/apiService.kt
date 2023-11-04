package tennis.bot.mobile.core

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import tennis.bot.mobile.onboarding.phone.CountryItem

interface apiService {
    @GET("apod")
    fun getData(@Query("api_key") key: String = "4nf26H20czhc8GqRbiB8fadE8PI3UH8darWvNel9"): Call<CountryItem>

}