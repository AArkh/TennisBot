package tennis.bot.mobile.onboarding.phone

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.RecyclerView
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import tennis.bot.mobile.core.CoreAdapter
import tennis.bot.mobile.core.CoreUtilsItem
import tennis.bot.mobile.databinding.RecyclerCountryItemBinding
import javax.inject.Inject

class PhoneInputAdapter @Inject constructor() : CoreAdapter<CountryCodeItemViewHolder>() {

    var clickListener: ((item: CountryItem) -> Unit)? = null

    override fun onBindViewHolder(holder: CountryCodeItemViewHolder, item: Any) {
        val countryItem = item as? CountryItem ?: throw IllegalArgumentException("Item must be CountryItem")
//        holder.binding.countryIconIv.setImageResource(countryItem.icon)
        holder.binding.countryNameTv.text = countryItem.countryName
        holder.binding.countryCodeTv.text = countryItem.countryCode
        holder.itemView.setOnClickListener {
            clickListener?.invoke(item)
        }
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

@Serializable
data class CountryItem(
    @SerialName("code") val icon: String,
    @SerialName("name") val countryName: String,
    @SerialName("dial_code") val countryCode: String
) : CoreUtilsItem()