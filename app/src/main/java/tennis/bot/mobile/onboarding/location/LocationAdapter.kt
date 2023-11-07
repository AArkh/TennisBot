package tennis.bot.mobile.onboarding.location

import android.view.View
import kotlinx.serialization.Serializable
import tennis.bot.mobile.core.CoreAdapter
import tennis.bot.mobile.onboarding.phone.CountryCodeItemViewHolder
import tennis.bot.mobile.onboarding.phone.PhoneInputAdapter
import javax.inject.Inject

class LocationAdapter @Inject constructor() : PhoneInputAdapter() {
    override fun onBindViewHolder(holder: CountryCodeItemViewHolder, item: Any) {
        super.onBindViewHolder(holder, item)
        holder.binding.countryIconIv.visibility = View.GONE
        holder.binding.countryCodeTv.visibility = View.GONE
    }
}