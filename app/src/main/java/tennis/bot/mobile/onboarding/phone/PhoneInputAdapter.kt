package tennis.bot.mobile.onboarding.phone

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.RecyclerView
import tennis.bot.mobile.core.CoreAdapter
import tennis.bot.mobile.databinding.RecyclerCountryItemBinding
import javax.inject.Inject

class PhoneInputAdapter @Inject constructor() : CoreAdapter<CountryCodeItemViewHolder>() {
    override fun onBindViewHolder(holder: CountryCodeItemViewHolder, item: Any) {
        val countryItem = item as? CountryItem ?: throw IllegalArgumentException("Item must be CountryItem")
        holder.binding.countryIconIv.setImageResource(countryItem.icon)
        holder.binding.countryNameTv.text = countryItem.countryName
        holder.binding.countryCodeTv.text = countryItem.countryCode
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CountryCodeItemViewHolder {
        val binding = RecyclerCountryItemBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return CountryCodeItemViewHolder(binding)
    }

}

class CountryCodeItemViewHolder(
    val binding: RecyclerCountryItemBinding
) : RecyclerView.ViewHolder(binding.root)

data class CountryItem(
    val icon: Int,
    val countryName: String,
    val countryCode: String
)

interface CountryCodesClickHandler {
    fun onItemClick(position: Int)
}