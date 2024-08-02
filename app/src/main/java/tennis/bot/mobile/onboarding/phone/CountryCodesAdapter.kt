package tennis.bot.mobile.onboarding.phone

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import kotlinx.serialization.Serializable
import tennis.bot.mobile.core.CoreAdapter
import tennis.bot.mobile.core.CoreUtilsItem
import tennis.bot.mobile.databinding.RecyclerCountryItemBinding
import javax.inject.Inject

open class CountryCodesAdapter @Inject constructor() : CoreAdapter<CountryCodeItemViewHolder>() {
    var clickListener: ((item: CountryItem) -> Unit)? = null

    override fun onBindViewHolder(holder: CountryCodeItemViewHolder, item: Any) {
        val countryItem = item as? CountryItem ?: throw IllegalArgumentException("Item must be CountryItem")
        holder.binding.countryIconIv.load(countryItem.icon)
        holder.binding.countryNameTv.text = countryItem.name
        holder.binding.countryCodeTv.text = countryItem.code
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
    val icon: String,
    val name: String,
    val code: String
) : CoreUtilsItem()