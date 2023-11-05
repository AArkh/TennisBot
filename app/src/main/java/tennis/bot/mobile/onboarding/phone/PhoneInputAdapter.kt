package tennis.bot.mobile.onboarding.phone

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.ImageLoader
import coil.decode.SvgDecoder
import coil.disk.DiskCache
import coil.load
import coil.request.ImageRequest
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
        holder.binding.countryIconIv
            .load("https://hatscripts.github.io/circle-flags/flags/${countryItem.icon.lowercase()}.svg",
                imageLoader(holder.itemView.context)) { size(28, 28) }

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

fun imageLoader(context: Context): ImageLoader {
    return ImageLoader.Builder(context)
        .components {
            add(SvgDecoder.Factory())
        }
        .diskCache {
            DiskCache.Builder()
                .directory(context.cacheDir.resolve("image_cache"))
                .maxSizePercent(0.02)
                .build()
        }
        .build()
}

@Serializable
data class CountryItem(
    @SerialName("code") val icon: String,
    @SerialName("name") val countryName: String,
    @SerialName("dial_code") val countryCode: String
) : CoreUtilsItem()