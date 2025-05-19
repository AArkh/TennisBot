package tennis.bot.mobile.onboarding.location

import android.view.View
import tennis.bot.mobile.onboarding.phone.CountryCodeItemViewHolder
import tennis.bot.mobile.onboarding.phone.CountryItem
import tennis.bot.mobile.onboarding.phone.CountryCodesAdapter
import javax.inject.Inject

class LocationAdapter @Inject constructor() : CountryCodesAdapter() {

    override fun onBindViewHolder(holder: CountryCodeItemViewHolder, item: Any) {
        val countryItem = item as? CountryItem ?: throw IllegalArgumentException("Item must be CountryItem")
        holder.binding.countryIconIv.visibility = View.GONE
        holder.binding.countryNameTv.text = countryItem.name
        holder.binding.countryCodeTv.visibility = View.GONE
        holder.itemView.setOnClickListener {
            clickListener?.invoke(item)
        }
    }
}
